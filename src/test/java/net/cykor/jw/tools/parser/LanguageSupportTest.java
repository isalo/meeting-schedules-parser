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

import static org.assertj.core.api.Assertions.assertThat;

import net.cykor.jw.tools.parser.internal.LanguageSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class LanguageSupportTest {

  @ParameterizedTest
  @ValueSource(strings = {"E", "U", "P", "e", "u", "p"})
  void isEnhancedParsingAvailable_supportedLanguages_returnsTrue(String languageCode) {
    assertThat(LanguageSupport.isEnhancedParsingAvailable(languageCode)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"X", "ZZ", "FR", ""})
  void isEnhancedParsingAvailable_unsupportedLanguages_returnsFalse(String languageCode) {
    assertThat(LanguageSupport.isEnhancedParsingAvailable(languageCode)).isFalse();
  }

  @Test
  void isEnhancedParsingAvailable_null_returnsFalse() {
    assertThat(LanguageSupport.isEnhancedParsingAvailable(null)).isFalse();
  }

  @Test
  void getSupportedLanguages_returnsNonEmptySet() {
    assertThat(LanguageSupport.getSupportedLanguages()).isNotEmpty().contains("E", "U", "P");
  }

  @ParameterizedTest
  @CsvSource({"Song 123,123", "Пісня 45,45", "123,123", "Song 1 and Song 2,1"})
  void extractSongNumber_variousFormats_extractsNumber(String text, int expectedNumber) {
    Object result = LanguageSupport.extractSongNumber(text);
    assertThat(result).isEqualTo(expectedNumber);
  }

  @Test
  void extractSongNumber_noNumber_returnsOriginalText() {
    Object result = LanguageSupport.extractSongNumber("No number here");
    assertThat(result).isEqualTo("No number here");
  }

  @ParameterizedTest
  @CsvSource({"'10 min. talk',E,10", "'5 хв. виступ',U,5", "'15 min.',P,15"})
  void extractTime_variousFormats_extractsMinutes(String text, String lang, int expectedMinutes) {
    Integer result = LanguageSupport.extractTime(text, lang);
    assertThat(result).isEqualTo(expectedMinutes);
  }

  @ParameterizedTest
  @CsvSource({
    "january,E,1",
    "february,E,2",
    "december,E,12",
    "січня,U,1",
    "лютого,U,2",
    "грудня,U,12",
    "stycznia,P,1",
    "lutego,P,2",
    "grudnia,P,12"
  })
  void monthNameToNumber_variousMonths_returnsCorrectNumber(
      String monthName, String lang, int expectedMonth) {
    assertThat(LanguageSupport.monthNameToNumber(monthName, lang)).isEqualTo(expectedMonth);
  }

  @Test
  void monthNameToNumber_unknownMonth_returnsZero() {
    assertThat(LanguageSupport.monthNameToNumber("unknown", "E")).isZero();
  }

  @Test
  void getConfig_supportedLanguage_returnsConfig() {
    LanguageSupport.LanguageConfig config = LanguageSupport.getConfig("E");
    assertThat(config).isNotNull();
    assertThat(config.code()).isEqualTo("E");
    assertThat(config.name()).isEqualTo("English");
  }

  @Test
  void getConfig_unsupportedLanguage_returnsNull() {
    assertThat(LanguageSupport.getConfig("XX")).isNull();
  }
}
