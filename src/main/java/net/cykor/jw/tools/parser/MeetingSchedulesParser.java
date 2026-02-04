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
package net.cykor.jw.tools.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import net.cykor.jw.tools.parser.exception.DownloadFailedException;
import net.cykor.jw.tools.parser.exception.MalformedPublicationException;
import net.cykor.jw.tools.parser.exception.ParserException;
import net.cykor.jw.tools.parser.exception.ParserException.ErrorCode;
import net.cykor.jw.tools.parser.exception.UnsupportedFormatException;
import net.cykor.jw.tools.parser.internal.EpubParser;
import net.cykor.jw.tools.parser.internal.FileValidator;
import net.cykor.jw.tools.parser.internal.JwpubParser;

/**
 * Main entry point for parsing JW Meeting Workbook and Watchtower Study publications.
 *
 * <p>This parser supports both JWPUB and EPUB formats and can read from files, input streams, or
 * URLs.
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * // Parse from file
 * MeetingSchedulesParser parser = MeetingSchedulesParser.create();
 * ParseResult result = parser.parse(Path.of("mwb_E_202401.jwpub"));
 *
 * // Parse from URL
 * ParseResult result = parser.parse(
 *     URI.create("https://example.com/mwb_E_202401.jwpub").toURL(),
 *     DownloadOptions.defaults()
 * );
 *
 * // Get JSON output
 * String json = result.toJson();
 * String schedulesJson = result.toSchedulesJson();
 * }</pre>
 *
 * <p>This class is thread-safe.
 *
 * @see ParseResult
 * @see ParserOptions
 */
public final class MeetingSchedulesParser {

  private final ParserOptions options;

  private MeetingSchedulesParser(ParserOptions options) {
    this.options = options;
  }

  /**
   * Creates a new parser with default options.
   *
   * @return new parser instance
   */
  public static MeetingSchedulesParser create() {
    return new MeetingSchedulesParser(ParserOptions.defaults());
  }

  /**
   * Creates a new parser with custom options.
   *
   * @param options parser options
   * @return new parser instance
   */
  public static MeetingSchedulesParser create(ParserOptions options) {
    return new MeetingSchedulesParser(options != null ? options : ParserOptions.defaults());
  }

  /**
   * Parses a publication file from the given path.
   *
   * @param file path to the JWPUB or EPUB file
   * @return parsed result containing schedules
   * @throws UnsupportedFormatException if the file format is not supported
   * @throws MalformedPublicationException if the file content is invalid
   * @throws ParserException for other parsing errors
   * @throws IOException if reading the file fails
   */
  public ParseResult parse(Path file) throws ParserException, IOException {
    if (file == null) {
      throw new IllegalArgumentException("file cannot be null");
    }

    String filename = FileValidator.getBasename(file);
    validateFilename(filename);

    byte[] data = Files.readAllBytes(file);
    return parseData(data, filename);
  }

  /**
   * Parses a publication from an input stream.
   *
   * @param inputStream input stream containing the publication data
   * @param format the format of the publication (use AUTO to detect from filename)
   * @param filename the original filename (used for format detection and metadata)
   * @return parsed result containing schedules
   * @throws UnsupportedFormatException if the file format is not supported
   * @throws MalformedPublicationException if the content is invalid
   * @throws ParserException for other parsing errors
   * @throws IOException if reading fails
   */
  public ParseResult parse(InputStream inputStream, Format format, String filename)
      throws ParserException, IOException {
    if (inputStream == null) {
      throw new IllegalArgumentException("inputStream cannot be null");
    }
    if (filename == null || filename.isEmpty()) {
      throw new IllegalArgumentException("filename cannot be null or empty");
    }

    validateFilename(filename);

    byte[] data = readAllBytes(inputStream);
    return parseData(data, filename);
  }

  /**
   * Parses a publication from a byte array.
   *
   * @param data the publication data
   * @param filename the original filename (used for format detection and metadata)
   * @return parsed result containing schedules
   * @throws UnsupportedFormatException if the file format is not supported
   * @throws MalformedPublicationException if the content is invalid
   * @throws ParserException for other parsing errors
   * @throws IOException if parsing fails
   */
  public ParseResult parse(byte[] data, String filename) throws ParserException, IOException {
    if (data == null || data.length == 0) {
      throw new IllegalArgumentException("data cannot be null or empty");
    }
    if (filename == null || filename.isEmpty()) {
      throw new IllegalArgumentException("filename cannot be null or empty");
    }

    validateFilename(filename);

    return parseData(data, filename);
  }

  /**
   * Downloads and parses a publication from a URL.
   *
   * @param url URL to download the publication from
   * @param downloadOptions options for the download
   * @return parsed result containing schedules
   * @throws UnsupportedFormatException if the file format is not supported
   * @throws MalformedPublicationException if the content is invalid
   * @throws DownloadFailedException if downloading fails
   * @throws ParserException for other parsing errors
   * @throws IOException if reading fails
   */
  public ParseResult parse(java.net.URL url, DownloadOptions downloadOptions)
      throws ParserException, IOException {
    if (url == null) {
      throw new IllegalArgumentException("url cannot be null");
    }

    DownloadOptions opts = downloadOptions != null ? downloadOptions : DownloadOptions.defaults();

    String filename = FileValidator.getBasename(url.getPath());
    validateFilename(filename);

    byte[] data = download(url, opts);
    return parseData(data, filename);
  }

  /**
   * Downloads and parses a publication from a URI.
   *
   * @param uri URI to download the publication from
   * @param downloadOptions options for the download
   * @return parsed result containing schedules
   * @throws UnsupportedFormatException if the file format is not supported
   * @throws MalformedPublicationException if the content is invalid
   * @throws DownloadFailedException if downloading fails
   * @throws ParserException for other parsing errors
   * @throws IOException if reading fails
   */
  public ParseResult parse(URI uri, DownloadOptions downloadOptions)
      throws ParserException, IOException {
    if (uri == null) {
      throw new IllegalArgumentException("uri cannot be null");
    }

    return parse(uri.toURL(), downloadOptions);
  }

  private void validateFilename(String filename) throws UnsupportedFormatException {
    if (!FileValidator.isValidFilename(filename)) {
      throw new UnsupportedFormatException(
          ErrorCode.INVALID_FILENAME,
          "Invalid filename format. Expected: mwb_LANG_YYYYMM.jwpub/epub or w_LANG_YYYYMM.jwpub/epub");
    }

    if (!FileValidator.isValidIssue(filename)) {
      throw new UnsupportedFormatException(
          ErrorCode.UNSUPPORTED_ISSUE,
          "Unsupported publication issue. MWB is supported from July 2022, Watchtower from April 2023.");
    }
  }

  private ParseResult parseData(byte[] data, String filename) throws ParserException, IOException {
    if (data.length > options.getMaxFileSize()) {
      throw new MalformedPublicationException(
          ErrorCode.FILE_TOO_LARGE,
          "File size exceeds limit: " + options.getMaxFileSize() + " bytes");
    }

    Format format = FileValidator.getFormat(filename);

    return switch (format) {
      case JWPUB -> JwpubParser.parse(data, filename, options);
      case EPUB -> EpubParser.parse(data, filename, options);
      case AUTO ->
          throw new UnsupportedFormatException(
              "Cannot auto-detect format from data. Please specify format.");
    };
  }

  private byte[] download(java.net.URL url, DownloadOptions opts)
      throws DownloadFailedException, IOException {
    try {
      HttpClient.Builder clientBuilder =
          HttpClient.newBuilder()
              .connectTimeout(opts.getConnectionTimeout())
              .followRedirects(
                  opts.isFollowRedirects()
                      ? HttpClient.Redirect.NORMAL
                      : HttpClient.Redirect.NEVER);

      HttpClient client = clientBuilder.build();

      HttpRequest.Builder requestBuilder =
          HttpRequest.newBuilder().uri(url.toURI()).timeout(opts.getReadTimeout()).GET();

      for (var entry : opts.getHeaders().entrySet()) {
        requestBuilder.header(entry.getKey(), entry.getValue());
      }

      HttpRequest request = requestBuilder.build();

      HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

      int statusCode = response.statusCode();
      if (statusCode != 200) {
        throw new DownloadFailedException(
            "Download failed with HTTP status: " + statusCode, statusCode);
      }

      byte[] data = response.body();

      if (data.length > opts.getMaxSize()) {
        throw new DownloadFailedException(
            ErrorCode.FILE_TOO_LARGE, "Downloaded file exceeds size limit");
      }

      return data;
    } catch (DownloadFailedException e) {
      throw e;
    } catch (java.net.http.HttpTimeoutException e) {
      throw new DownloadFailedException(ErrorCode.DOWNLOAD_TIMEOUT, "Download timed out", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new DownloadFailedException("Download was interrupted", e);
    } catch (Exception e) {
      throw new DownloadFailedException("Failed to download publication: " + e.getMessage(), e);
    }
  }

  private static byte[] readAllBytes(InputStream inputStream) throws IOException {
    try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      byte[] data = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(data)) != -1) {
        buffer.write(data, 0, bytesRead);
      }
      return buffer.toByteArray();
    }
  }
}
