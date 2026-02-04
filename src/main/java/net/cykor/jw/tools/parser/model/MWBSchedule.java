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
 * Represents a parsed Meeting Workbook (MWB) schedule for one week.
 *
 * <p>This class is immutable and thread-safe.
 *
 * @see WSchedule
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MWBSchedule {

  @JsonProperty("mwb_week_date")
  private final String weekDate;

  @JsonProperty("mwb_week_date_locale")
  private final String weekDateLocale;

  @JsonProperty("mwb_weekly_bible_reading")
  private final String weeklyBibleReading;

  @JsonProperty("mwb_song_first")
  private final Integer songFirst;

  @JsonProperty("mwb_tgw_talk")
  private final String tgwTalk;

  @JsonProperty("mwb_tgw_talk_title")
  private final String tgwTalkTitle;

  @JsonProperty("mwb_tgw_gems_title")
  private final String tgwGemsTitle;

  @JsonProperty("mwb_tgw_bread")
  private final String tgwBread;

  @JsonProperty("mwb_tgw_bread_title")
  private final String tgwBreadTitle;

  @JsonProperty("mwb_ayf_count")
  private final Integer ayfCount;

  @JsonProperty("mwb_ayf_part1")
  private final String ayfPart1;

  @JsonProperty("mwb_ayf_part1_time")
  private final Integer ayfPart1Time;

  @JsonProperty("mwb_ayf_part1_type")
  private final String ayfPart1Type;

  @JsonProperty("mwb_ayf_part1_title")
  private final String ayfPart1Title;

  @JsonProperty("mwb_ayf_part2")
  private final String ayfPart2;

  @JsonProperty("mwb_ayf_part2_time")
  private final Integer ayfPart2Time;

  @JsonProperty("mwb_ayf_part2_type")
  private final String ayfPart2Type;

  @JsonProperty("mwb_ayf_part2_title")
  private final String ayfPart2Title;

  @JsonProperty("mwb_ayf_part3")
  private final String ayfPart3;

  @JsonProperty("mwb_ayf_part3_time")
  private final Integer ayfPart3Time;

  @JsonProperty("mwb_ayf_part3_type")
  private final String ayfPart3Type;

  @JsonProperty("mwb_ayf_part3_title")
  private final String ayfPart3Title;

  @JsonProperty("mwb_ayf_part4")
  private final String ayfPart4;

  @JsonProperty("mwb_ayf_part4_time")
  private final Integer ayfPart4Time;

  @JsonProperty("mwb_ayf_part4_type")
  private final String ayfPart4Type;

  @JsonProperty("mwb_ayf_part4_title")
  private final String ayfPart4Title;

  @JsonProperty("mwb_song_middle")
  private final Object songMiddle;

  @JsonProperty("mwb_lc_count")
  private final Integer lcCount;

  @JsonProperty("mwb_lc_part1")
  private final String lcPart1;

  @JsonProperty("mwb_lc_part1_time")
  private final Integer lcPart1Time;

  @JsonProperty("mwb_lc_part1_content")
  private final String lcPart1Content;

  @JsonProperty("mwb_lc_part1_title")
  private final String lcPart1Title;

  @JsonProperty("mwb_lc_part2")
  private final String lcPart2;

  @JsonProperty("mwb_lc_part2_time")
  private final Integer lcPart2Time;

  @JsonProperty("mwb_lc_part2_content")
  private final String lcPart2Content;

  @JsonProperty("mwb_lc_part2_title")
  private final String lcPart2Title;

  @JsonProperty("mwb_lc_cbs")
  private final String lcCbs;

  @JsonProperty("mwb_lc_cbs_title")
  private final String lcCbsTitle;

  @JsonProperty("mwb_song_conclude")
  private final Object songConclude;

  private MWBSchedule(Builder builder) {
    this.weekDate = builder.weekDate;
    this.weekDateLocale = builder.weekDateLocale;
    this.weeklyBibleReading = builder.weeklyBibleReading;
    this.songFirst = builder.songFirst;
    this.tgwTalk = builder.tgwTalk;
    this.tgwTalkTitle = builder.tgwTalkTitle;
    this.tgwGemsTitle = builder.tgwGemsTitle;
    this.tgwBread = builder.tgwBread;
    this.tgwBreadTitle = builder.tgwBreadTitle;
    this.ayfCount = builder.ayfCount;
    this.ayfPart1 = builder.ayfPart1;
    this.ayfPart1Time = builder.ayfPart1Time;
    this.ayfPart1Type = builder.ayfPart1Type;
    this.ayfPart1Title = builder.ayfPart1Title;
    this.ayfPart2 = builder.ayfPart2;
    this.ayfPart2Time = builder.ayfPart2Time;
    this.ayfPart2Type = builder.ayfPart2Type;
    this.ayfPart2Title = builder.ayfPart2Title;
    this.ayfPart3 = builder.ayfPart3;
    this.ayfPart3Time = builder.ayfPart3Time;
    this.ayfPart3Type = builder.ayfPart3Type;
    this.ayfPart3Title = builder.ayfPart3Title;
    this.ayfPart4 = builder.ayfPart4;
    this.ayfPart4Time = builder.ayfPart4Time;
    this.ayfPart4Type = builder.ayfPart4Type;
    this.ayfPart4Title = builder.ayfPart4Title;
    this.songMiddle = builder.songMiddle;
    this.lcCount = builder.lcCount;
    this.lcPart1 = builder.lcPart1;
    this.lcPart1Time = builder.lcPart1Time;
    this.lcPart1Content = builder.lcPart1Content;
    this.lcPart1Title = builder.lcPart1Title;
    this.lcPart2 = builder.lcPart2;
    this.lcPart2Time = builder.lcPart2Time;
    this.lcPart2Content = builder.lcPart2Content;
    this.lcPart2Title = builder.lcPart2Title;
    this.lcCbs = builder.lcCbs;
    this.lcCbsTitle = builder.lcCbsTitle;
    this.songConclude = builder.songConclude;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getWeekDate() {
    return weekDate;
  }

  public String getWeekDateLocale() {
    return weekDateLocale;
  }

  public String getWeeklyBibleReading() {
    return weeklyBibleReading;
  }

  public Integer getSongFirst() {
    return songFirst;
  }

  public String getTgwTalk() {
    return tgwTalk;
  }

  public String getTgwTalkTitle() {
    return tgwTalkTitle;
  }

  public String getTgwGemsTitle() {
    return tgwGemsTitle;
  }

  public String getTgwBread() {
    return tgwBread;
  }

  public String getTgwBreadTitle() {
    return tgwBreadTitle;
  }

  public Integer getAyfCount() {
    return ayfCount;
  }

  public String getAyfPart1() {
    return ayfPart1;
  }

  public Integer getAyfPart1Time() {
    return ayfPart1Time;
  }

  public String getAyfPart1Type() {
    return ayfPart1Type;
  }

  public String getAyfPart1Title() {
    return ayfPart1Title;
  }

  public String getAyfPart2() {
    return ayfPart2;
  }

  public Integer getAyfPart2Time() {
    return ayfPart2Time;
  }

  public String getAyfPart2Type() {
    return ayfPart2Type;
  }

  public String getAyfPart2Title() {
    return ayfPart2Title;
  }

  public String getAyfPart3() {
    return ayfPart3;
  }

  public Integer getAyfPart3Time() {
    return ayfPart3Time;
  }

  public String getAyfPart3Type() {
    return ayfPart3Type;
  }

  public String getAyfPart3Title() {
    return ayfPart3Title;
  }

  public String getAyfPart4() {
    return ayfPart4;
  }

  public Integer getAyfPart4Time() {
    return ayfPart4Time;
  }

  public String getAyfPart4Type() {
    return ayfPart4Type;
  }

  public String getAyfPart4Title() {
    return ayfPart4Title;
  }

  public Object getSongMiddle() {
    return songMiddle;
  }

  public Integer getLcCount() {
    return lcCount;
  }

  public String getLcPart1() {
    return lcPart1;
  }

  public Integer getLcPart1Time() {
    return lcPart1Time;
  }

  public String getLcPart1Content() {
    return lcPart1Content;
  }

  public String getLcPart1Title() {
    return lcPart1Title;
  }

  public String getLcPart2() {
    return lcPart2;
  }

  public Integer getLcPart2Time() {
    return lcPart2Time;
  }

  public String getLcPart2Content() {
    return lcPart2Content;
  }

  public String getLcPart2Title() {
    return lcPart2Title;
  }

  public String getLcCbs() {
    return lcCbs;
  }

  public String getLcCbsTitle() {
    return lcCbsTitle;
  }

  public Object getSongConclude() {
    return songConclude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MWBSchedule that = (MWBSchedule) o;
    return Objects.equals(weekDate, that.weekDate)
        && Objects.equals(weekDateLocale, that.weekDateLocale)
        && Objects.equals(weeklyBibleReading, that.weeklyBibleReading)
        && Objects.equals(songFirst, that.songFirst);
  }

  @Override
  public int hashCode() {
    return Objects.hash(weekDate, weekDateLocale, weeklyBibleReading, songFirst);
  }

  @Override
  public String toString() {
    return "MWBSchedule{"
        + "weekDate='"
        + weekDate
        + '\''
        + ", weeklyBibleReading='"
        + weeklyBibleReading
        + '\''
        + '}';
  }

  public static final class Builder {
    private String weekDate;
    private String weekDateLocale;
    private String weeklyBibleReading;
    private Integer songFirst;
    private String tgwTalk;
    private String tgwTalkTitle;
    private String tgwGemsTitle;
    private String tgwBread;
    private String tgwBreadTitle;
    private Integer ayfCount;
    private String ayfPart1;
    private Integer ayfPart1Time;
    private String ayfPart1Type;
    private String ayfPart1Title;
    private String ayfPart2;
    private Integer ayfPart2Time;
    private String ayfPart2Type;
    private String ayfPart2Title;
    private String ayfPart3;
    private Integer ayfPart3Time;
    private String ayfPart3Type;
    private String ayfPart3Title;
    private String ayfPart4;
    private Integer ayfPart4Time;
    private String ayfPart4Type;
    private String ayfPart4Title;
    private Object songMiddle;
    private Integer lcCount;
    private String lcPart1;
    private Integer lcPart1Time;
    private String lcPart1Content;
    private String lcPart1Title;
    private String lcPart2;
    private Integer lcPart2Time;
    private String lcPart2Content;
    private String lcPart2Title;
    private String lcCbs;
    private String lcCbsTitle;
    private Object songConclude;

    private Builder() {}

    public Builder weekDate(String weekDate) {
      this.weekDate = weekDate;
      return this;
    }

    public Builder weekDateLocale(String weekDateLocale) {
      this.weekDateLocale = weekDateLocale;
      return this;
    }

    public Builder weeklyBibleReading(String weeklyBibleReading) {
      this.weeklyBibleReading = weeklyBibleReading;
      return this;
    }

    public Builder songFirst(Integer songFirst) {
      this.songFirst = songFirst;
      return this;
    }

    public Builder tgwTalk(String tgwTalk) {
      this.tgwTalk = tgwTalk;
      return this;
    }

    public Builder tgwTalkTitle(String tgwTalkTitle) {
      this.tgwTalkTitle = tgwTalkTitle;
      return this;
    }

    public Builder tgwGemsTitle(String tgwGemsTitle) {
      this.tgwGemsTitle = tgwGemsTitle;
      return this;
    }

    public Builder tgwBread(String tgwBread) {
      this.tgwBread = tgwBread;
      return this;
    }

    public Builder tgwBreadTitle(String tgwBreadTitle) {
      this.tgwBreadTitle = tgwBreadTitle;
      return this;
    }

    public Builder ayfCount(Integer ayfCount) {
      this.ayfCount = ayfCount;
      return this;
    }

    public Builder ayfPart1(String ayfPart1) {
      this.ayfPart1 = ayfPart1;
      return this;
    }

    public Builder ayfPart1Time(Integer ayfPart1Time) {
      this.ayfPart1Time = ayfPart1Time;
      return this;
    }

    public Builder ayfPart1Type(String ayfPart1Type) {
      this.ayfPart1Type = ayfPart1Type;
      return this;
    }

    public Builder ayfPart1Title(String ayfPart1Title) {
      this.ayfPart1Title = ayfPart1Title;
      return this;
    }

    public Builder ayfPart2(String ayfPart2) {
      this.ayfPart2 = ayfPart2;
      return this;
    }

    public Builder ayfPart2Time(Integer ayfPart2Time) {
      this.ayfPart2Time = ayfPart2Time;
      return this;
    }

    public Builder ayfPart2Type(String ayfPart2Type) {
      this.ayfPart2Type = ayfPart2Type;
      return this;
    }

    public Builder ayfPart2Title(String ayfPart2Title) {
      this.ayfPart2Title = ayfPart2Title;
      return this;
    }

    public Builder ayfPart3(String ayfPart3) {
      this.ayfPart3 = ayfPart3;
      return this;
    }

    public Builder ayfPart3Time(Integer ayfPart3Time) {
      this.ayfPart3Time = ayfPart3Time;
      return this;
    }

    public Builder ayfPart3Type(String ayfPart3Type) {
      this.ayfPart3Type = ayfPart3Type;
      return this;
    }

    public Builder ayfPart3Title(String ayfPart3Title) {
      this.ayfPart3Title = ayfPart3Title;
      return this;
    }

    public Builder ayfPart4(String ayfPart4) {
      this.ayfPart4 = ayfPart4;
      return this;
    }

    public Builder ayfPart4Time(Integer ayfPart4Time) {
      this.ayfPart4Time = ayfPart4Time;
      return this;
    }

    public Builder ayfPart4Type(String ayfPart4Type) {
      this.ayfPart4Type = ayfPart4Type;
      return this;
    }

    public Builder ayfPart4Title(String ayfPart4Title) {
      this.ayfPart4Title = ayfPart4Title;
      return this;
    }

    public Builder songMiddle(Object songMiddle) {
      this.songMiddle = songMiddle;
      return this;
    }

    public Builder lcCount(Integer lcCount) {
      this.lcCount = lcCount;
      return this;
    }

    public Builder lcPart1(String lcPart1) {
      this.lcPart1 = lcPart1;
      return this;
    }

    public Builder lcPart1Time(Integer lcPart1Time) {
      this.lcPart1Time = lcPart1Time;
      return this;
    }

    public Builder lcPart1Content(String lcPart1Content) {
      this.lcPart1Content = lcPart1Content;
      return this;
    }

    public Builder lcPart1Title(String lcPart1Title) {
      this.lcPart1Title = lcPart1Title;
      return this;
    }

    public Builder lcPart2(String lcPart2) {
      this.lcPart2 = lcPart2;
      return this;
    }

    public Builder lcPart2Time(Integer lcPart2Time) {
      this.lcPart2Time = lcPart2Time;
      return this;
    }

    public Builder lcPart2Content(String lcPart2Content) {
      this.lcPart2Content = lcPart2Content;
      return this;
    }

    public Builder lcPart2Title(String lcPart2Title) {
      this.lcPart2Title = lcPart2Title;
      return this;
    }

    public Builder lcCbs(String lcCbs) {
      this.lcCbs = lcCbs;
      return this;
    }

    public Builder lcCbsTitle(String lcCbsTitle) {
      this.lcCbsTitle = lcCbsTitle;
      return this;
    }

    public Builder songConclude(Object songConclude) {
      this.songConclude = songConclude;
      return this;
    }

    public MWBSchedule build() {
      return new MWBSchedule(this);
    }
  }
}
