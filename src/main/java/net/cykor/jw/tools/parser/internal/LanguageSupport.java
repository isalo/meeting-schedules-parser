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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Language-specific parsing support for enhanced date and part extraction.
 *
 * <p>Internal class - not part of the public API.
 */
public final class LanguageSupport {

  private static final Map<String, LanguageConfig> LANGUAGES = new HashMap<>();

  static {
    LANGUAGES.put(
        "E",
        new LanguageConfig(
            "E",
            "English",
            "january|february|march|april|may|june|july|august|september|october|november|december",
            "(\\d+)\\s*min\\.?",
            "min\\.",
            Pattern.compile(
                "(?i)(january|february|march|april|may|june|july|august|september|october|november|december)\\s+(\\d{1,2})(?:-(\\d{1,2}))?"),
            Pattern.compile(
                "(?i)Study Article\\s+(\\d+):\\s*(\\w+)\\s+(\\d{1,2})(?:-(\\d{1,2}))?,?\\s*(\\d{4})")));

    LANGUAGES.put(
        "K",
        new LanguageConfig(
            "K",
            "Ukrainian",
            "січня|лютого|березня|квітня|травня|червня|липня|серпня|вересня|жовтня|листопада|грудня|"
                + "січень|лютий|березень|квітень|травень|червень|липень|серпень|вересень|жовтень|листопад|грудень",
            "(\\d+)\\s*хв\\.?",
            "хв",
            Pattern.compile(
                "(?iu)(\\d{1,2})(?:[\\-\\u2013\\u2014](\\d{1,2}))?\\s+(січня|лютого|березня|квітня|травня|червня|липня|серпня|вересня|жовтня|листопада|грудня)"),
            Pattern.compile(
                "(?iu)Стаття(?:\\s+для\\s+вивчення)?\\s+(\\d+).*?(\\d{1,2})(?:[\\-\\u2013\\u2014](\\d{1,2}))?\\s+(січня|лютого|березня|квітня|травня|червня|липня|серпня|вересня|жовтня|листопада|грудня)\\s+(\\d{4})")));

    LANGUAGES.put(
        "P",
        new LanguageConfig(
            "P",
            "Polish",
            "stycznia|lutego|marca|kwietnia|maja|czerwca|lipca|sierpnia|września|października|listopada|grudnia|"
                + "styczeń|luty|marzec|kwiecień|maj|czerwiec|lipiec|sierpień|wrzesień|październik|listopad|grudzień",
            "(\\d+)\\s*min\\.?",
            "min\\.",
            Pattern.compile(
                "(?iu)(\\d{1,2})(?:-(\\d{1,2}))?\\s+(stycznia|lutego|marca|kwietnia|maja|czerwca|lipca|sierpnia|września|października|listopada|grudnia)"),
            Pattern.compile(
                "(?iu)Artykuł\\s+do\\s+studium\\s+(\\d+).*?(\\d{1,2})(?:-(\\d{1,2}))?\\s+(\\w+)\\s+(\\d{4})")));
  }

  private LanguageSupport() {}

  /** Returns true if enhanced parsing is available for the given language code. */
  public static boolean isEnhancedParsingAvailable(String languageCode) {
    return languageCode != null && LANGUAGES.containsKey(languageCode.toUpperCase(Locale.ROOT));
  }

  /** Returns the set of supported language codes. */
  public static Set<String> getSupportedLanguages() {
    return LANGUAGES.keySet();
  }

  /** Returns the language configuration for the given code. */
  public static LanguageConfig getConfig(String languageCode) {
    if (languageCode == null) {
      return null;
    }
    return LANGUAGES.get(languageCode.toUpperCase(Locale.ROOT));
  }

  /**
   * Extracts song number from text. Returns Integer if valid song number (≤162), otherwise the
   * original text.
   */
  public static Object extractSongNumber(String text) {
    if (text == null || text.isEmpty()) {
      return null;
    }

    Pattern pattern = Pattern.compile("(\\d+)");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      try {
        int songNumber = Integer.parseInt(matcher.group(1));
        // Only return as song number if within valid range (1-162)
        if (songNumber > 0 && songNumber <= MAX_SONG_NUMBER) {
          return songNumber;
        }
        return text;
      } catch (NumberFormatException e) {
        return text;
      }
    }
    return text;
  }

  /** Extracts time in minutes from text. */
  public static Integer extractTime(String text, String languageCode) {
    if (text == null || text.isEmpty()) {
      return null;
    }

    LanguageConfig config = getConfig(languageCode);
    if (config == null) {
      return extractTimeDefault(text);
    }

    Pattern pattern = Pattern.compile(config.minutesPattern());
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  private static Integer extractTimeDefault(String text) {
    Pattern pattern = Pattern.compile("(\\d+)\\s*min");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  /** Converts month name to month number. */
  public static int monthNameToNumber(String monthName, String languageCode) {
    if (monthName == null) {
      return 0;
    }

    String lower = monthName.toLowerCase(Locale.ROOT);

    if ("E".equalsIgnoreCase(languageCode)) {
      return switch (lower) {
        case "january" -> 1;
        case "february" -> 2;
        case "march" -> 3;
        case "april" -> 4;
        case "may" -> 5;
        case "june" -> 6;
        case "july" -> 7;
        case "august" -> 8;
        case "september" -> 9;
        case "october" -> 10;
        case "november" -> 11;
        case "december" -> 12;
        default -> 0;
      };
    }

    if ("K".equalsIgnoreCase(languageCode)) {
      return switch (lower) {
        case "січня", "січень" -> 1;
        case "лютого", "лютий" -> 2;
        case "березня", "березень" -> 3;
        case "квітня", "квітень" -> 4;
        case "травня", "травень" -> 5;
        case "червня", "червень" -> 6;
        case "липня", "липень" -> 7;
        case "серпня", "серпень" -> 8;
        case "вересня", "вересень" -> 9;
        case "жовтня", "жовтень" -> 10;
        case "листопада", "листопад" -> 11;
        case "грудня", "грудень" -> 12;
        default -> 0;
      };
    }

    if ("P".equalsIgnoreCase(languageCode)) {
      return switch (lower) {
        case "stycznia", "styczeń" -> 1;
        case "lutego", "luty" -> 2;
        case "marca", "marzec" -> 3;
        case "kwietnia", "kwiecień" -> 4;
        case "maja", "maj" -> 5;
        case "czerwca", "czerwiec" -> 6;
        case "lipca", "lipiec" -> 7;
        case "sierpnia", "sierpień" -> 8;
        case "września", "wrzesień" -> 9;
        case "października", "październik" -> 10;
        case "listopada", "listopad" -> 11;
        case "grudnia", "grudzień" -> 12;
        default -> 0;
      };
    }

    return 0;
  }

  /** Maximum song number in the songbook. */
  private static final int MAX_SONG_NUMBER = 162;

  /** Configuration for a specific language. */
  public record LanguageConfig(
      String code,
      String name,
      String monthVariations,
      String minutesPattern,
      String minutesSeparatorVariations,
      Pattern mwbDatePattern,
      Pattern wStudyDatePattern) {}
}
