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

import java.time.Duration;
import org.junit.jupiter.api.Test;

class ParserOptionsTest {

  @Test
  void defaults_returnsValidDefaults() {
    ParserOptions options = ParserOptions.defaults();

    assertThat(options.isStrict()).isFalse();
    assertThat(options.isEnhancedParsingEnabled()).isTrue();
    assertThat(options.getMaxFileSize()).isEqualTo(ParserOptions.DEFAULT_MAX_FILE_SIZE);
    assertThat(options.getMaxFiles()).isEqualTo(ParserOptions.DEFAULT_MAX_FILES);
    assertThat(options.getConnectionTimeout()).isEqualTo(ParserOptions.DEFAULT_CONNECTION_TIMEOUT);
    assertThat(options.getReadTimeout()).isEqualTo(ParserOptions.DEFAULT_READ_TIMEOUT);
  }

  @Test
  void builder_customValues_setsCorrectly() {
    ParserOptions options =
        ParserOptions.builder()
            .strict(true)
            .enableEnhancedParsing(false)
            .maxFileSize(1024)
            .maxFiles(100)
            .connectionTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(20))
            .build();

    assertThat(options.isStrict()).isTrue();
    assertThat(options.isEnhancedParsingEnabled()).isFalse();
    assertThat(options.getMaxFileSize()).isEqualTo(1024);
    assertThat(options.getMaxFiles()).isEqualTo(100);
    assertThat(options.getConnectionTimeout()).isEqualTo(Duration.ofSeconds(10));
    assertThat(options.getReadTimeout()).isEqualTo(Duration.ofSeconds(20));
  }

  @Test
  void builder_invalidMaxFileSize_throwsException() {
    assertThatThrownBy(() -> ParserOptions.builder().maxFileSize(0).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxFileSize");
  }

  @Test
  void builder_negativeMaxFileSize_throwsException() {
    assertThatThrownBy(() -> ParserOptions.builder().maxFileSize(-1).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxFileSize");
  }

  @Test
  void builder_invalidMaxFiles_throwsException() {
    assertThatThrownBy(() -> ParserOptions.builder().maxFiles(0).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxFiles");
  }

  @Test
  void defaults_returnsSameInstance() {
    ParserOptions options1 = ParserOptions.defaults();
    ParserOptions options2 = ParserOptions.defaults();

    assertThat(options1).isSameAs(options2);
  }
}
