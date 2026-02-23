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

import java.util.Locale;

/**
 * Publication format types supported by the parser.
 *
 * @see MeetingScheduleParser
 */
public enum Format {
  /** JWPUB format (encrypted SQLite database inside ZIP) */
  JWPUB(".jwpub"),

  /** EPUB format (standard ZIP with HTML files) */
  EPUB(".epub"),

  /** Auto-detect format from file extension or content */
  AUTO(null);

  private final String extension;

  Format(String extension) {
    this.extension = extension;
  }

  /** Returns the file extension for this format, or null for AUTO. */
  public String getExtension() {
    return extension;
  }

  /**
   * Detects format from filename.
   *
   * @param filename the filename to check
   * @return detected format, or AUTO if cannot be determined
   */
  public static Format fromFilename(String filename) {
    if (filename == null) {
      return AUTO;
    }
    String lower = filename.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".jwpub")) {
      return JWPUB;
    } else if (lower.endsWith(".epub")) {
      return EPUB;
    }
    return AUTO;
  }
}
