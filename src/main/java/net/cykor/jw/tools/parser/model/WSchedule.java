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
package net.cykor.jw.tools.parser.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents a parsed Watchtower Study (W) schedule for one week.
 *
 * <p>This class is immutable and thread-safe.
 *
 * @see MWBSchedule
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class WSchedule {

  @JsonProperty("w_study_date")
  private final String studyDate;

  @JsonProperty("w_study_date_locale")
  private final String studyDateLocale;

  @JsonProperty("w_study_title")
  private final String studyTitle;

  @JsonProperty("w_study_opening_song")
  private final Integer openingSong;

  @JsonProperty("w_study_concluding_song")
  private final Integer concludingSong;

  private WSchedule(Builder builder) {
    this.studyDate = builder.studyDate;
    this.studyDateLocale = builder.studyDateLocale;
    this.studyTitle = builder.studyTitle;
    this.openingSong = builder.openingSong;
    this.concludingSong = builder.concludingSong;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getStudyDate() {
    return studyDate;
  }

  public String getStudyDateLocale() {
    return studyDateLocale;
  }

  public String getStudyTitle() {
    return studyTitle;
  }

  public Integer getOpeningSong() {
    return openingSong;
  }

  public Integer getConcludingSong() {
    return concludingSong;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WSchedule wSchedule = (WSchedule) o;
    return Objects.equals(studyDate, wSchedule.studyDate)
        && Objects.equals(studyDateLocale, wSchedule.studyDateLocale)
        && Objects.equals(studyTitle, wSchedule.studyTitle)
        && Objects.equals(openingSong, wSchedule.openingSong)
        && Objects.equals(concludingSong, wSchedule.concludingSong);
  }

  @Override
  public int hashCode() {
    return Objects.hash(studyDate, studyDateLocale, studyTitle, openingSong, concludingSong);
  }

  @Override
  public String toString() {
    return "WSchedule{"
        + "studyDate='"
        + studyDate
        + '\''
        + ", studyTitle='"
        + studyTitle
        + '\''
        + '}';
  }

  public static final class Builder {
    private String studyDate;
    private String studyDateLocale;
    private String studyTitle;
    private Integer openingSong;
    private Integer concludingSong;

    private Builder() {}

    public Builder studyDate(String studyDate) {
      this.studyDate = studyDate;
      return this;
    }

    public Builder studyDateLocale(String studyDateLocale) {
      this.studyDateLocale = studyDateLocale;
      return this;
    }

    public Builder studyTitle(String studyTitle) {
      this.studyTitle = studyTitle;
      return this;
    }

    public Builder openingSong(Integer openingSong) {
      this.openingSong = openingSong;
      return this;
    }

    public Builder concludingSong(Integer concludingSong) {
      this.concludingSong = concludingSong;
      return this;
    }

    public WSchedule build() {
      return new WSchedule(this);
    }
  }
}
