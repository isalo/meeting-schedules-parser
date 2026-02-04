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

import net.cykor.jw.tools.parser.internal.ZipValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ZipValidatorTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "../../../etc/passwd",
        "..\\..\\windows\\system32",
        "foo/../../../bar",
        "/etc/passwd"
      })
  void isZipSlipVulnerable_maliciousPaths_returnsTrue(String path) {
    assertThat(ZipValidator.isZipSlipVulnerable(path)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"file.txt", "folder/file.txt", "folder/subfolder/file.txt", "OEBPS/content.html"})
  void isZipSlipVulnerable_safePaths_returnsFalse(String path) {
    assertThat(ZipValidator.isZipSlipVulnerable(path)).isFalse();
  }

  @Test
  void isZipSlipVulnerable_null_returnsTrue() {
    assertThat(ZipValidator.isZipSlipVulnerable(null)).isTrue();
  }
}
