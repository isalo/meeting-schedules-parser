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

import java.util.List;
import net.cykor.jw.tools.parser.model.MWBSchedule;
import net.cykor.jw.tools.parser.model.WSchedule;
import org.junit.jupiter.api.Test;

class ParseResultTest {

  @Test
  void builder_mwbResult_buildsCorrectly() {
    MWBSchedule schedule =
        MWBSchedule.builder().weekDate("2024/01/01").weeklyBibleReading("Genesis 1").build();

    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .mwbSchedules(List.of(schedule))
            .build();

    assertThat(result.getSchemaVersion()).isEqualTo("1.0.0");
    assertThat(result.getPublicationType()).isEqualTo(ParseResult.PublicationType.MWB);
    assertThat(result.getLanguage()).isEqualTo("E");
    assertThat(result.getYear()).isEqualTo(2024);
    assertThat(result.getMonth()).isEqualTo(1);
    assertThat(result.getMwbSchedules()).hasSize(1);
    assertThat(result.getWSchedules()).isNull();
    assertThat(result.isMwb()).isTrue();
    assertThat(result.isWatchtower()).isFalse();
  }

  @Test
  void builder_watchtowerResult_buildsCorrectly() {
    WSchedule schedule =
        WSchedule.builder().studyDate("2024/01/06").studyTitle("Test Article").build();

    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.WATCHTOWER)
            .language("U")
            .year(2024)
            .month(1)
            .wSchedules(List.of(schedule))
            .build();

    assertThat(result.getPublicationType()).isEqualTo(ParseResult.PublicationType.WATCHTOWER);
    assertThat(result.getLanguage()).isEqualTo("U");
    assertThat(result.getMwbSchedules()).isNull();
    assertThat(result.getWSchedules()).hasSize(1);
    assertThat(result.isMwb()).isFalse();
    assertThat(result.isWatchtower()).isTrue();
  }

  @Test
  void builder_missingPublicationType_throwsException() {
    assertThatThrownBy(() -> ParseResult.builder().language("E").year(2024).month(1).build())
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("publicationType");
  }

  @Test
  void builder_missingLanguage_throwsException() {
    assertThatThrownBy(
            () ->
                ParseResult.builder()
                    .publicationType(ParseResult.PublicationType.MWB)
                    .year(2024)
                    .month(1)
                    .build())
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("language");
  }

  @Test
  void builder_invalidYear_throwsException() {
    assertThatThrownBy(
            () ->
                ParseResult.builder()
                    .publicationType(ParseResult.PublicationType.MWB)
                    .language("E")
                    .year(2020)
                    .month(1)
                    .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("year");
  }

  @Test
  void builder_invalidMonth_throwsException() {
    assertThatThrownBy(
            () ->
                ParseResult.builder()
                    .publicationType(ParseResult.PublicationType.MWB)
                    .language("E")
                    .year(2024)
                    .month(13)
                    .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("month");
  }

  @Test
  void toJson_mwbResult_returnsValidJson() {
    MWBSchedule schedule =
        MWBSchedule.builder()
            .weekDate("2024/01/01")
            .weeklyBibleReading("Genesis 1")
            .songFirst(1)
            .build();

    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .mwbSchedules(List.of(schedule))
            .build();

    String json = result.toJson();

    assertThat(json).contains("\"schemaVersion\" : \"1.0.0\"");
    assertThat(json).contains("\"publicationType\" : \"MWB\"");
    assertThat(json).contains("\"language\" : \"E\"");
    assertThat(json).contains("\"mwb_week_date\" : \"2024/01/01\"");
  }

  @Test
  void toSchedulesJson_mwbResult_returnsJsonArray() {
    MWBSchedule schedule =
        MWBSchedule.builder()
            .weekDate("2024/01/01")
            .weeklyBibleReading("Genesis 1")
            .songFirst(1)
            .build();

    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .mwbSchedules(List.of(schedule))
            .build();

    String json = result.toSchedulesJson();

    assertThat(json).startsWith("[");
    assertThat(json).endsWith("]");
    assertThat(json).contains("\"mwb_week_date\" : \"2024/01/01\"");
  }

  @Test
  void toSchedulesJson_emptyResult_returnsEmptyArray() {
    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .build();

    String json = result.toSchedulesJson();
    assertThat(json).isEqualTo("[]");
  }

  @Test
  void getMwbSchedules_returnsUnmodifiableList() {
    MWBSchedule schedule = MWBSchedule.builder().weekDate("2024/01/01").build();

    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .mwbSchedules(List.of(schedule))
            .build();

    assertThatThrownBy(() -> result.getMwbSchedules().add(schedule))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void equals_sameValues_returnsTrue() {
    ParseResult result1 =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .build();

    ParseResult result2 =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .build();

    assertThat(result1).isEqualTo(result2);
    assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
  }

  @Test
  void toString_returnsReadableString() {
    ParseResult result =
        ParseResult.builder()
            .publicationType(ParseResult.PublicationType.MWB)
            .language("E")
            .year(2024)
            .month(1)
            .build();

    String str = result.toString();

    assertThat(str).contains("MWB");
    assertThat(str).contains("E");
    assertThat(str).contains("2024");
  }
}
