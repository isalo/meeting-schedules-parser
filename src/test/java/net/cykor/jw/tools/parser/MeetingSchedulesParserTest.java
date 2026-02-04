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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import net.cykor.jw.tools.parser.exception.UnsupportedFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeetingSchedulesParserTest {

  private MeetingSchedulesParser parser;

  @BeforeEach
  void setUp() {
    parser = MeetingSchedulesParser.create();
  }

  @Test
  void create_defaultOptions_returnsParser() {
    MeetingSchedulesParser result = MeetingSchedulesParser.create();
    assertThat(result).isNotNull();
  }

  @Test
  void create_customOptions_returnsParser() {
    ParserOptions options = ParserOptions.builder().strict(true).build();
    MeetingSchedulesParser result = MeetingSchedulesParser.create(options);
    assertThat(result).isNotNull();
  }

  @Test
  void create_nullOptions_usesDefaults() {
    MeetingSchedulesParser result = MeetingSchedulesParser.create(null);
    assertThat(result).isNotNull();
  }

  @Test
  void parse_nullPath_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> parser.parse((java.nio.file.Path) null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("null");
  }

  @Test
  void parse_nullInputStream_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> parser.parse(null, Format.AUTO, "mwb_E_202401.jwpub"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("null");
  }

  @Test
  void parse_nullFilename_throwsIllegalArgumentException() {
    byte[] data = new byte[100];
    assertThatThrownBy(() -> parser.parse(data, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("null");
  }

  @Test
  void parse_emptyFilename_throwsIllegalArgumentException() {
    byte[] data = new byte[100];
    assertThatThrownBy(() -> parser.parse(data, ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("empty");
  }

  @Test
  void parse_invalidFilename_throwsUnsupportedFormatException() {
    byte[] data = new byte[100];
    assertThatThrownBy(() -> parser.parse(data, "invalid.jwpub"))
        .isInstanceOf(UnsupportedFormatException.class)
        .hasMessageContaining("Invalid filename");
  }

  @Test
  void parse_unsupportedMwbIssue_throwsUnsupportedFormatException() {
    byte[] data = new byte[100];
    assertThatThrownBy(() -> parser.parse(data, "mwb_E_202201.jwpub"))
        .isInstanceOf(UnsupportedFormatException.class)
        .hasMessageContaining("Unsupported publication issue");
  }

  @Test
  void parse_unsupportedWatchtowerIssue_throwsUnsupportedFormatException() {
    byte[] data = new byte[100];
    assertThatThrownBy(() -> parser.parse(data, "w_E_202301.jwpub"))
        .isInstanceOf(UnsupportedFormatException.class)
        .hasMessageContaining("Unsupported publication issue");
  }

  @Test
  void parse_nullUrl_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> parser.parse((java.net.URL) null, DownloadOptions.defaults()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("null");
  }

  @Test
  void parse_nullUri_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> parser.parse((java.net.URI) null, DownloadOptions.defaults()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("null");
  }

  @Test
  void parse_emptyData_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> parser.parse(new byte[0], "mwb_E_202401.jwpub"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("empty");
  }
}
