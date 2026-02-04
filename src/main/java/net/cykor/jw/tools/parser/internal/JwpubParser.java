/*
 * Copyright 2024 Cykor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cykor.jw.tools.parser.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.cykor.jw.tools.parser.ParseResult;
import net.cykor.jw.tools.parser.ParserOptions;
import net.cykor.jw.tools.parser.exception.MalformedPublicationException;
import net.cykor.jw.tools.parser.exception.ParserException.ErrorCode;
import net.cykor.jw.tools.parser.model.MWBSchedule;
import net.cykor.jw.tools.parser.model.WSchedule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parser for JWPUB format publications.
 *
 * <p>JWPUB files are nested ZIP archives containing an encrypted SQLite database with the
 * publication content.
 *
 * <p>Internal class - not part of the public API.
 */
public final class JwpubParser {

  private static final String ENCRYPTION_KEY_BASE64 =
      "MTFjYmI1NTg3ZTMyODQ2ZDRjMjY3OTBjNjMzZGEyODlmNjZmZTU4NDJhM2E1ODVjZTFiYzNhMjk0YWY1YWRhNw==";

  private JwpubParser() {}

  /**
   * Parses a JWPUB file from a byte array.
   *
   * @param data the JWPUB file content
   * @param filename the original filename
   * @param options parser options
   * @return parsed result
   * @throws MalformedPublicationException if the JWPUB is invalid
   * @throws IOException if reading fails
   */
  public static ParseResult parse(byte[] data, String filename, ParserOptions options)
      throws MalformedPublicationException, IOException {
    boolean isMwb = FileValidator.isMwbPublication(filename);
    boolean isW = FileValidator.isWatchtowerPublication(filename);

    int year = FileValidator.getYear(filename);
    int month = FileValidator.getMonth(filename);
    String lang = FileValidator.getLanguage(filename);

    Map<String, byte[]> outerFiles = extractZipFiles(data);

    byte[] contentsZip = outerFiles.get("contents");
    if (contentsZip == null) {
      throw new MalformedPublicationException(
          ErrorCode.INVALID_ARCHIVE, "JWPUB file missing 'contents' archive");
    }

    Map<String, byte[]> innerFiles = extractZipFiles(contentsZip);

    byte[] dbFile = findDatabaseFile(innerFiles);
    if (dbFile == null) {
      throw new MalformedPublicationException(
          ErrorCode.INVALID_DATABASE, "Database file not found in JWPUB file");
    }

    ParseResult.Builder resultBuilder =
        ParseResult.builder()
            .publicationType(
                isMwb ? ParseResult.PublicationType.MWB : ParseResult.PublicationType.WATCHTOWER)
            .language(lang)
            .year(year)
            .month(month);

    try {
      String pubCard = getPubCard(dbFile);
      KeyIv keyIv = getPubKeyIv(pubCard);

      if (isMwb) {
        List<Document> htmlDocs = getMwbDocs(dbFile, keyIv);
        List<MWBSchedule> schedules = HtmlParser.parseMwb(htmlDocs, year, lang);
        resultBuilder.mwbSchedules(schedules);
      }

      if (isW) {
        WDocsResult wDocs = getWDocs(dbFile, keyIv);
        List<WSchedule> schedules = parseWJwpub(wDocs, lang);
        resultBuilder.wSchedules(schedules);
      }
    } catch (MalformedPublicationException e) {
      throw e;
    } catch (Exception e) {
      throw new MalformedPublicationException(
          ErrorCode.DECRYPTION_FAILED, "Failed to decrypt JWPUB content", e);
    }

    return resultBuilder.build();
  }

  private static Map<String, byte[]> extractZipFiles(byte[] data) throws IOException {
    Map<String, byte[]> files = new HashMap<>();

    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          String name = entry.getName();
          byte[] content = zis.readAllBytes();
          files.put(name, content);
        }
        zis.closeEntry();
      }
    }

    return files;
  }

  private static byte[] findDatabaseFile(Map<String, byte[]> files) {
    for (Map.Entry<String, byte[]> entry : files.entrySet()) {
      if (entry.getKey().endsWith(".db")) {
        return entry.getValue();
      }
    }
    return null;
  }

  private static String getPubCard(byte[] dbFile) throws MalformedPublicationException {
    java.nio.file.Path tempDb = null;
    try {
      tempDb = java.nio.file.Files.createTempFile("jwpub_", ".db");
      java.nio.file.Files.write(tempDb, dbFile);

      Class.forName("org.sqlite.JDBC");
      try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + tempDb.toAbsolutePath());
          Statement stmt = conn.createStatement();
          ResultSet rs =
              stmt.executeQuery(
                  "SELECT MepsLanguageIndex, Symbol, Year, IssueTagNumber FROM Publication")) {

        if (rs.next()) {
          String mepsLangIndex = rs.getString(1);
          String symbol = rs.getString(2);
          String yearVal = rs.getString(3);
          String issueTag = rs.getString(4);

          return mepsLangIndex + "_" + symbol + "_" + yearVal + "_" + issueTag;
        }

        throw new MalformedPublicationException(
            ErrorCode.INVALID_DATABASE, "Publication table is empty");
      }
    } catch (ClassNotFoundException e) {
      throw new MalformedPublicationException(
          ErrorCode.INVALID_DATABASE, "SQLite driver not found", e);
    } catch (SQLException e) {
      throw new MalformedPublicationException(
          ErrorCode.INVALID_DATABASE, "Failed to read publication database", e);
    } catch (IOException e) {
      throw new MalformedPublicationException(ErrorCode.IO_ERROR, "Failed to create temp file", e);
    } finally {
      if (tempDb != null) {
        try {
          java.nio.file.Files.deleteIfExists(tempDb);
        } catch (IOException ignored) {
          // Ignore cleanup errors
        }
      }
    }
  }

  private static KeyIv getPubKeyIv(String pubCard) throws MalformedPublicationException {
    try {
      String xorKey =
          new String(Base64.getDecoder().decode(ENCRYPTION_KEY_BASE64), StandardCharsets.UTF_8);

      byte[] pubCardHash = sha256(pubCard);
      byte[] xorKeyBytes = hexToBytes(xorKey);

      byte[] xored = xorBuffers(pubCardHash, xorKeyBytes);
      String xoredHex = bytesToHex(xored);

      String key = xoredHex.substring(0, 32);
      String iv = xoredHex.substring(32);

      return new KeyIv(key, iv);
    } catch (Exception e) {
      throw new MalformedPublicationException(
          ErrorCode.DECRYPTION_FAILED, "Failed to derive encryption key", e);
    }
  }

  private static List<Document> getMwbDocs(byte[] dbFile, KeyIv keyIv)
      throws MalformedPublicationException {
    List<Document> docs = new ArrayList<>();

    java.nio.file.Path tempDb = null;
    try {
      tempDb = java.nio.file.Files.createTempFile("jwpub_", ".db");
      java.nio.file.Files.write(tempDb, dbFile);

      try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + tempDb.toAbsolutePath());
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT Content FROM Document WHERE Class='106'")) {

        while (rs.next()) {
          byte[] content = rs.getBytes(1);
          String html = getRawContent(content, keyIv);
          Document doc = Jsoup.parse(html);
          doc.select("rt").remove();
          docs.add(doc);
        }
      }
    } catch (SQLException e) {
      throw new MalformedPublicationException(
          ErrorCode.INVALID_DATABASE, "Failed to read MWB documents", e);
    } catch (IOException e) {
      throw new MalformedPublicationException(ErrorCode.IO_ERROR, "Failed to create temp file", e);
    } finally {
      if (tempDb != null) {
        try {
          java.nio.file.Files.deleteIfExists(tempDb);
        } catch (IOException ignored) {
          // Ignore cleanup errors
        }
      }
    }

    return docs;
  }

  private static WDocsResult getWDocs(byte[] dbFile, KeyIv keyIv)
      throws MalformedPublicationException {
    Document toc = null;
    List<StudyArticle> articles = new ArrayList<>();

    java.nio.file.Path tempDb = null;
    try {
      tempDb = java.nio.file.Files.createTempFile("jwpub_", ".db");
      java.nio.file.Files.write(tempDb, dbFile);

      try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + tempDb.toAbsolutePath());
          Statement stmt = conn.createStatement()) {

        try (ResultSet rs = stmt.executeQuery("SELECT Content FROM Document WHERE Class='68'")) {
          if (rs.next()) {
            byte[] content = rs.getBytes(1);
            String html = getRawContent(content, keyIv);
            toc = Jsoup.parse(html);
            toc.select("rt").remove();
          }
        }

        try (ResultSet rs =
            stmt.executeQuery("SELECT MepsDocumentId, Content FROM Document WHERE Class='40'")) {
          while (rs.next()) {
            int id = rs.getInt(1);
            byte[] content = rs.getBytes(2);
            String html = getRawContent(content, keyIv);
            Document article = Jsoup.parse(html);
            article.select("rt").remove();
            articles.add(new StudyArticle(id, article));
          }
        }
      }
    } catch (SQLException e) {
      throw new MalformedPublicationException(
          ErrorCode.INVALID_DATABASE, "Failed to read Watchtower documents", e);
    } catch (IOException e) {
      throw new MalformedPublicationException(ErrorCode.IO_ERROR, "Failed to create temp file", e);
    } finally {
      if (tempDb != null) {
        try {
          java.nio.file.Files.deleteIfExists(tempDb);
        } catch (IOException ignored) {
          // Ignore cleanup errors
        }
      }
    }

    return new WDocsResult(toc, articles);
  }

  private static List<WSchedule> parseWJwpub(WDocsResult wDocs, String lang) {
    List<WSchedule> schedules = new ArrayList<>();

    if (wDocs.toc() == null) {
      return schedules;
    }

    Elements studyArticles = HtmlParser.getWStudyArticles(wDocs.toc());

    for (Element article : studyArticles) {
      Element nextSibling = article.nextElementSibling();
      if (nextSibling == null) {
        continue;
      }

      Element link = nextSibling.selectFirst("a");
      if (link == null) {
        continue;
      }

      String href = link.attr("href");
      if (href == null || href.isEmpty()) {
        continue;
      }

      java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(".+:(\\w+)/$");
      java.util.regex.Matcher matcher = pattern.matcher(href);

      if (!matcher.find()) {
        continue;
      }

      int articleId;
      try {
        articleId = Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        continue;
      }

      Document articleDoc = findArticleById(wDocs.articles(), articleId);
      if (articleDoc == null) {
        continue;
      }

      WSchedule schedule = HtmlParser.parseWSchedule(article, articleDoc, lang);
      schedules.add(schedule);
    }

    return schedules;
  }

  private static Document findArticleById(List<StudyArticle> articles, int id) {
    for (StudyArticle article : articles) {
      if (article.id() == id) {
        return article.document();
      }
    }
    return null;
  }

  private static String getRawContent(byte[] data, KeyIv keyIv)
      throws MalformedPublicationException {
    try {
      byte[] decrypted = decryptAes128Cbc(data, keyIv.key(), keyIv.iv());

      try (ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
          InflaterInputStream iis = new InflaterInputStream(bis, new Inflater(true));
          ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

        byte[] buffer = new byte[4096];
        int len;
        while ((len = iis.read(buffer)) != -1) {
          bos.write(buffer, 0, len);
        }

        return bos.toString(StandardCharsets.UTF_8);
      }
    } catch (Exception e) {
      try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
          InflaterInputStream iis = new InflaterInputStream(bis);
          ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

        byte[] buffer = new byte[4096];
        int len;
        while ((len = iis.read(buffer)) != -1) {
          bos.write(buffer, 0, len);
        }

        return bos.toString(StandardCharsets.UTF_8);
      } catch (IOException fallbackError) {
        throw new MalformedPublicationException(
            ErrorCode.DECRYPTION_FAILED, "Failed to decrypt content", e);
      }
    }
  }

  private static byte[] decryptAes128Cbc(byte[] data, String keyHex, String ivHex)
      throws Exception {
    byte[] keyBytes = hexToBytes(keyHex);
    byte[] ivBytes = hexToBytes(ivHex);

    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

    return cipher.doFinal(data);
  }

  private static byte[] sha256(String text) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return digest.digest(text.getBytes(StandardCharsets.UTF_8));
  }

  private static byte[] hexToBytes(String hex) {
    String clean = hex.replaceAll("[^a-fA-F0-9]", "");
    byte[] bytes = new byte[clean.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16);
    }
    return bytes;
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  private static byte[] xorBuffers(byte[] buf1, byte[] buf2) {
    int len = Math.min(buf1.length, buf2.length);
    byte[] result = new byte[len];
    for (int i = 0; i < len; i++) {
      result[i] = (byte) (buf1[i] ^ buf2[i % buf2.length]);
    }
    return result;
  }

  private record KeyIv(String key, String iv) {}

  private record StudyArticle(int id, Document document) {}

  private record WDocsResult(Document toc, List<StudyArticle> articles) {}
}
