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

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cykor.jw.tools.parser.Format;
import net.cykor.jw.tools.parser.ParseResult.PublicationType;

/**
 * Validates publication files and extracts metadata from filenames.
 *
 * <p>Internal class - not part of the public API.
 */
public final class FileValidator {

  private static final Pattern MWB_PATTERN =
      Pattern.compile(
          "^mwb_([A-Z]{1,3})_(20[2-9]\\d)(0[1-9]|1[0-2])\\.(jwpub|epub)$",
          Pattern.CASE_INSENSITIVE);

  private static final Pattern W_PATTERN =
      Pattern.compile(
          "^w_([A-Z]{1,3})_(20[2-9]\\d)(0[1-9]|1[0-2])\\.(jwpub|epub)$", Pattern.CASE_INSENSITIVE);

  private static final int MIN_MWB_ISSUE = 202207;
  private static final int MIN_W_ISSUE = 202304;

  private FileValidator() {}

  /** Validates that the filename follows the expected naming convention. */
  public static boolean isValidFilename(String filename) {
    if (filename == null || filename.isEmpty()) {
      return false;
    }
    String basename = getBasename(filename);
    return MWB_PATTERN.matcher(basename).matches() || W_PATTERN.matcher(basename).matches();
  }

  /** Returns true if the file is a Meeting Workbook publication. */
  public static boolean isMwbPublication(String filename) {
    if (filename == null) {
      return false;
    }
    return MWB_PATTERN.matcher(getBasename(filename)).matches();
  }

  /** Returns true if the file is a Watchtower Study publication. */
  public static boolean isWatchtowerPublication(String filename) {
    if (filename == null) {
      return false;
    }
    return W_PATTERN.matcher(getBasename(filename)).matches();
  }

  /** Validates that the publication issue is supported. */
  public static boolean isValidIssue(String filename) {
    if (filename == null) {
      return false;
    }
    String basename = getBasename(filename);

    Matcher mwbMatcher = MWB_PATTERN.matcher(basename);
    if (mwbMatcher.matches()) {
      int issue = Integer.parseInt(mwbMatcher.group(2) + mwbMatcher.group(3));
      return issue >= MIN_MWB_ISSUE;
    }

    Matcher wMatcher = W_PATTERN.matcher(basename);
    if (wMatcher.matches()) {
      int issue = Integer.parseInt(wMatcher.group(2) + wMatcher.group(3));
      return issue >= MIN_W_ISSUE;
    }

    return false;
  }

  /** Extracts the language code from the filename. */
  public static String getLanguage(String filename) {
    if (filename == null) {
      return null;
    }
    String basename = getBasename(filename);

    Matcher mwbMatcher = MWB_PATTERN.matcher(basename);
    if (mwbMatcher.matches()) {
      return mwbMatcher.group(1);
    }

    Matcher wMatcher = W_PATTERN.matcher(basename);
    if (wMatcher.matches()) {
      return wMatcher.group(1);
    }

    return null;
  }

  /** Extracts the year from the filename. */
  public static int getYear(String filename) {
    if (filename == null) {
      return 0;
    }
    String basename = getBasename(filename);

    Matcher mwbMatcher = MWB_PATTERN.matcher(basename);
    if (mwbMatcher.matches()) {
      return Integer.parseInt(mwbMatcher.group(2));
    }

    Matcher wMatcher = W_PATTERN.matcher(basename);
    if (wMatcher.matches()) {
      return Integer.parseInt(wMatcher.group(2));
    }

    return 0;
  }

  /** Extracts the month from the filename. */
  public static int getMonth(String filename) {
    if (filename == null) {
      return 0;
    }
    String basename = getBasename(filename);

    Matcher mwbMatcher = MWB_PATTERN.matcher(basename);
    if (mwbMatcher.matches()) {
      return Integer.parseInt(mwbMatcher.group(3));
    }

    Matcher wMatcher = W_PATTERN.matcher(basename);
    if (wMatcher.matches()) {
      return Integer.parseInt(wMatcher.group(3));
    }

    return 0;
  }

  /** Detects the format from the filename. */
  public static Format getFormat(String filename) {
    return Format.fromFilename(filename);
  }

  /** Gets the publication type from the filename. */
  public static PublicationType getPublicationType(String filename) {
    if (isMwbPublication(filename)) {
      return PublicationType.MWB;
    } else if (isWatchtowerPublication(filename)) {
      return PublicationType.WATCHTOWER;
    }
    return null;
  }

  /** Extracts the basename from a path or URL. */
  public static String getBasename(String pathOrUrl) {
    if (pathOrUrl == null) {
      return null;
    }
    int lastSlash = Math.max(pathOrUrl.lastIndexOf('/'), pathOrUrl.lastIndexOf('\\'));
    if (lastSlash >= 0) {
      return pathOrUrl.substring(lastSlash + 1);
    }
    return pathOrUrl;
  }

  /** Extracts the basename from a Path. */
  public static String getBasename(Path path) {
    if (path == null) {
      return null;
    }
    Path fileName = path.getFileName();
    return fileName != null ? fileName.toString() : null;
  }
}
