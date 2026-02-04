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

import net.cykor.jw.tools.parser.internal.FileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FileValidatorTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "mwb_E_202401.jwpub",
        "mwb_E_202401.epub",
        "mwb_U_202312.jwpub",
        "mwb_UK_202407.epub",
        "w_E_202401.jwpub",
        "w_E_202401.epub",
        "w_P_202304.jwpub"
      })
  void isValidFilename_validFilenames_returnsTrue(String filename) {
    assertThat(FileValidator.isValidFilename(filename)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "invalid.jwpub",
        "mwb_202401.jwpub",
        "mwb_E_2024.jwpub",
        "mwb_E_202413.jwpub",
        "mwb_E_202400.jwpub",
        "mwb_E_202401.pdf",
        "w_202401.epub",
        "",
        "some_random_file.txt"
      })
  void isValidFilename_invalidFilenames_returnsFalse(String filename) {
    assertThat(FileValidator.isValidFilename(filename)).isFalse();
  }

  @Test
  void isValidFilename_null_returnsFalse() {
    assertThat(FileValidator.isValidFilename(null)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"mwb_E_202207.jwpub", "mwb_E_202301.epub", "mwb_U_202412.jwpub"})
  void isValidIssue_validMwbIssues_returnsTrue(String filename) {
    assertThat(FileValidator.isValidIssue(filename)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"mwb_E_202206.jwpub", "mwb_E_202106.epub", "mwb_U_202201.jwpub"})
  void isValidIssue_invalidMwbIssues_returnsFalse(String filename) {
    assertThat(FileValidator.isValidIssue(filename)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"w_E_202304.jwpub", "w_E_202401.epub", "w_P_202312.jwpub"})
  void isValidIssue_validWatchtowerIssues_returnsTrue(String filename) {
    assertThat(FileValidator.isValidIssue(filename)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"w_E_202303.jwpub", "w_E_202212.epub", "w_P_202301.jwpub"})
  void isValidIssue_invalidWatchtowerIssues_returnsFalse(String filename) {
    assertThat(FileValidator.isValidIssue(filename)).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "mwb_E_202401.jwpub,E",
    "mwb_UK_202401.epub,UK",
    "w_P_202304.jwpub,P",
    "w_PLM_202401.epub,PLM"
  })
  void getLanguage_validFilenames_returnsCorrectLanguage(String filename, String expectedLanguage) {
    assertThat(FileValidator.getLanguage(filename)).isEqualTo(expectedLanguage);
  }

  @ParameterizedTest
  @CsvSource({"mwb_E_202401.jwpub,2024", "mwb_U_202312.epub,2023", "w_E_202507.jwpub,2025"})
  void getYear_validFilenames_returnsCorrectYear(String filename, int expectedYear) {
    assertThat(FileValidator.getYear(filename)).isEqualTo(expectedYear);
  }

  @ParameterizedTest
  @CsvSource({"mwb_E_202401.jwpub,1", "mwb_U_202312.epub,12", "w_E_202507.jwpub,7"})
  void getMonth_validFilenames_returnsCorrectMonth(String filename, int expectedMonth) {
    assertThat(FileValidator.getMonth(filename)).isEqualTo(expectedMonth);
  }

  @ParameterizedTest
  @CsvSource({"mwb_E_202401.jwpub,JWPUB", "mwb_E_202401.epub,EPUB", "w_E_202401.JWPUB,JWPUB"})
  void getFormat_validFilenames_returnsCorrectFormat(String filename, Format expectedFormat) {
    assertThat(FileValidator.getFormat(filename)).isEqualTo(expectedFormat);
  }

  @Test
  void getFormat_unknownExtension_returnsAuto() {
    assertThat(FileValidator.getFormat("file.txt")).isEqualTo(Format.AUTO);
  }

  @ParameterizedTest
  @ValueSource(strings = {"mwb_E_202401.jwpub", "mwb_U_202312.epub"})
  void isMwbPublication_mwbFiles_returnsTrue(String filename) {
    assertThat(FileValidator.isMwbPublication(filename)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"w_E_202401.jwpub", "w_U_202312.epub"})
  void isMwbPublication_watchtowerFiles_returnsFalse(String filename) {
    assertThat(FileValidator.isMwbPublication(filename)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"w_E_202401.jwpub", "w_U_202312.epub"})
  void isWatchtowerPublication_watchtowerFiles_returnsTrue(String filename) {
    assertThat(FileValidator.isWatchtowerPublication(filename)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"mwb_E_202401.jwpub", "mwb_U_202312.epub"})
  void isWatchtowerPublication_mwbFiles_returnsFalse(String filename) {
    assertThat(FileValidator.isWatchtowerPublication(filename)).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
    "/path/to/mwb_E_202401.jwpub,mwb_E_202401.jwpub",
    "C:\\Users\\file\\w_E_202401.epub,w_E_202401.epub",
    "mwb_E_202401.jwpub,mwb_E_202401.jwpub"
  })
  void getBasename_variousPaths_returnsBasename(String path, String expectedBasename) {
    assertThat(FileValidator.getBasename(path)).isEqualTo(expectedBasename);
  }
}
