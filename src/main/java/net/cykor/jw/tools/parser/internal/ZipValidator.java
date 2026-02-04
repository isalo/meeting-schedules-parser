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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.cykor.jw.tools.parser.ParserOptions;
import net.cykor.jw.tools.parser.exception.MalformedPublicationException;
import net.cykor.jw.tools.parser.exception.ParserException.ErrorCode;

/**
 * Validates ZIP archives for security and size constraints.
 *
 * <p>Internal class - not part of the public API.
 */
public final class ZipValidator {

  private ZipValidator() {}

  /** Validation result containing details about the archive. */
  public record ValidationResult(
      boolean isValid, boolean isTooLarge, boolean hasTooManyFiles, boolean isSuspicious) {}

  /**
   * Validates a ZIP archive.
   *
   * @param inputStream the input stream containing ZIP data
   * @param options parser options with size limits
   * @return validation result
   * @throws IOException if reading fails
   */
  public static ValidationResult validate(InputStream inputStream, ParserOptions options)
      throws IOException {
    long totalSize = 0;
    int fileCount = 0;

    try (ZipInputStream zis = new ZipInputStream(inputStream)) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        fileCount++;

        if (fileCount > options.getMaxFiles()) {
          return new ValidationResult(false, false, true, false);
        }

        if (isZipSlipVulnerable(entry.getName())) {
          return new ValidationResult(false, false, false, true);
        }

        long entrySize = entry.getSize();
        if (entrySize > 0) {
          totalSize += entrySize;
          if (totalSize > options.getMaxFileSize()) {
            return new ValidationResult(false, true, false, false);
          }
        }

        zis.closeEntry();
      }
    }

    return new ValidationResult(true, false, false, false);
  }

  /**
   * Checks if the entry name is vulnerable to zip-slip attack.
   *
   * @param entryName the ZIP entry name
   * @return true if vulnerable
   */
  public static boolean isZipSlipVulnerable(String entryName) {
    if (entryName == null) {
      return true;
    }

    String normalized = entryName.replace('\\', '/');

    if (normalized.startsWith("/") || normalized.startsWith("..") || normalized.contains("/../")) {
      return true;
    }

    Path entryPath = Path.of(normalized).normalize();
    return entryPath.startsWith("..");
  }

  /**
   * Validates and throws exception if invalid.
   *
   * @param inputStream the input stream
   * @param options parser options
   * @throws MalformedPublicationException if validation fails
   * @throws IOException if reading fails
   */
  public static void validateOrThrow(InputStream inputStream, ParserOptions options)
      throws MalformedPublicationException, IOException {
    ValidationResult result = validate(inputStream, options);

    if (result.isTooLarge()) {
      throw new MalformedPublicationException(
          ErrorCode.FILE_TOO_LARGE, "Publication size exceeds limit");
    }
    if (result.hasTooManyFiles()) {
      throw new MalformedPublicationException(
          ErrorCode.TOO_MANY_FILES, "Publication contains too many files");
    }
    if (result.isSuspicious()) {
      throw new MalformedPublicationException(
          ErrorCode.SUSPICIOUS_CONTENT, "Publication contains suspicious paths");
    }
  }
}
