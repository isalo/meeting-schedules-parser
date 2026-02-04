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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
 * Parser for EPUB format publications.
 *
 * <p>Internal class - not part of the public API.
 */
public final class EpubParser {

  private EpubParser() {}

  /**
   * Parses an EPUB file from a byte array.
   *
   * @param data the EPUB file content
   * @param filename the original filename
   * @param options parser options
   * @return parsed result
   * @throws MalformedPublicationException if the EPUB is invalid
   * @throws IOException if reading fails
   */
  public static ParseResult parse(byte[] data, String filename, ParserOptions options)
      throws MalformedPublicationException, IOException {
    boolean isMwb = FileValidator.isMwbPublication(filename);
    boolean isW = FileValidator.isWatchtowerPublication(filename);

    int year = FileValidator.getYear(filename);
    int month = FileValidator.getMonth(filename);
    String lang = FileValidator.getLanguage(filename);

    Map<String, byte[]> files = extractZipFiles(data);

    List<Document> htmlDocs = getHtmlDocs(files, isMwb, isW);

    if (htmlDocs.isEmpty()) {
      throw new MalformedPublicationException(
          ErrorCode.MALFORMED_CONTENT,
          "The file is not a valid "
              + (isMwb ? "Meeting Workbook" : "Watchtower Study")
              + " EPUB file");
    }

    if (isW && htmlDocs.size() > 1) {
      throw new MalformedPublicationException(
          ErrorCode.MALFORMED_CONTENT, "The Watchtower EPUB file contains unexpected content");
    }

    ParseResult.Builder resultBuilder =
        ParseResult.builder()
            .publicationType(
                isMwb ? ParseResult.PublicationType.MWB : ParseResult.PublicationType.WATCHTOWER)
            .language(lang)
            .year(year)
            .month(month);

    if (isMwb) {
      List<MWBSchedule> schedules = HtmlParser.parseMwb(htmlDocs, year, lang);
      resultBuilder.mwbSchedules(schedules);
    }

    if (isW) {
      List<WSchedule> schedules = parseWEpub(htmlDocs.get(0), files, lang);
      resultBuilder.wSchedules(schedules);
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

  private static List<Document> getHtmlDocs(Map<String, byte[]> files, boolean isMwb, boolean isW)
      throws MalformedPublicationException {
    List<Document> validDocs = new ArrayList<>();

    for (Map.Entry<String, byte[]> entry : files.entrySet()) {
      String filename = entry.getKey();

      if (!isValidHtmlFile(filename)) {
        continue;
      }

      try {
        String content = new String(entry.getValue(), StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(content);

        doc.select("rt").remove();

        boolean isValidSchedule =
            isMwb
                ? HtmlParser.isValidMwbSchedule(doc)
                : isW ? HtmlParser.isValidWSchedule(doc) : false;

        if (isValidSchedule) {
          validDocs.add(doc);
        }
      } catch (Exception e) {
        // Skip invalid HTML files
      }
    }

    return validDocs;
  }

  private static List<WSchedule> parseWEpub(Document tocDoc, Map<String, byte[]> files, String lang)
      throws MalformedPublicationException {
    List<WSchedule> schedules = new ArrayList<>();

    Elements studyArticles = HtmlParser.getWStudyArticles(tocDoc);

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

      String articleFilename = getBasename(href);
      byte[] articleContent = findFileByBasename(files, articleFilename);

      if (articleContent == null) {
        continue;
      }

      try {
        String content = new String(articleContent, StandardCharsets.UTF_8);
        Document articleDoc = Jsoup.parse(content);
        articleDoc.select("rt").remove();

        WSchedule schedule = HtmlParser.parseWSchedule(article, articleDoc, lang);
        schedules.add(schedule);
      } catch (Exception e) {
        // Skip invalid articles
      }
    }

    return schedules;
  }

  private static boolean isValidHtmlFile(String filename) {
    if (filename == null) {
      return false;
    }
    String lower = filename.toLowerCase(Locale.ROOT);
    return lower.endsWith(".html") || lower.endsWith(".xhtml") || lower.endsWith(".htm");
  }

  private static String getBasename(String path) {
    if (path == null) {
      return null;
    }
    int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
    return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
  }

  private static byte[] findFileByBasename(Map<String, byte[]> files, String basename) {
    for (Map.Entry<String, byte[]> entry : files.entrySet()) {
      if (getBasename(entry.getKey()).equals(basename)) {
        return entry.getValue();
      }
    }
    return null;
  }
}
