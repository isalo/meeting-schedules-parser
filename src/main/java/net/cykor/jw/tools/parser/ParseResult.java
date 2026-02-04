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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.cykor.jw.tools.parser.model.MWBSchedule;
import net.cykor.jw.tools.parser.model.WSchedule;

/**
 * Result of parsing a JWPUB or EPUB publication file.
 *
 * <p>Contains either Meeting Workbook (MWB) schedules or Watchtower Study (W) schedules, depending
 * on the publication type.
 *
 * <p>This class is immutable and thread-safe.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ParseResult {

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  @JsonProperty("schemaVersion")
  private final String schemaVersion;

  @JsonProperty("publicationType")
  private final PublicationType publicationType;

  @JsonProperty("language")
  private final String language;

  @JsonProperty("year")
  private final int year;

  @JsonProperty("month")
  private final int month;

  @JsonProperty("mwbSchedules")
  private final List<MWBSchedule> mwbSchedules;

  @JsonProperty("wSchedules")
  private final List<WSchedule> wSchedules;

  private ParseResult(Builder builder) {
    this.schemaVersion = "1.0.0";
    this.publicationType = builder.publicationType;
    this.language = builder.language;
    this.year = builder.year;
    this.month = builder.month;
    this.mwbSchedules =
        builder.mwbSchedules != null ? Collections.unmodifiableList(builder.mwbSchedules) : null;
    this.wSchedules =
        builder.wSchedules != null ? Collections.unmodifiableList(builder.wSchedules) : null;
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Returns the schema version of this result format. */
  public String getSchemaVersion() {
    return schemaVersion;
  }

  /** Returns the type of publication that was parsed. */
  public PublicationType getPublicationType() {
    return publicationType;
  }

  /** Returns the language code of the publication (e.g., "E" for English, "U" for Ukrainian). */
  public String getLanguage() {
    return language;
  }

  /** Returns the year of the publication. */
  public int getYear() {
    return year;
  }

  /** Returns the month of the publication (1-12). */
  public int getMonth() {
    return month;
  }

  /**
   * Returns the parsed MWB schedules, or null if this is not an MWB publication.
   *
   * @return unmodifiable list of MWB schedules, or null
   */
  public List<MWBSchedule> getMwbSchedules() {
    return mwbSchedules;
  }

  /**
   * Returns the parsed Watchtower schedules, or null if this is not a Watchtower publication.
   *
   * @return unmodifiable list of W schedules, or null
   */
  public List<WSchedule> getWSchedules() {
    return wSchedules;
  }

  /** Returns true if this result contains MWB schedules. */
  public boolean isMwb() {
    return publicationType == PublicationType.MWB;
  }

  /** Returns true if this result contains Watchtower schedules. */
  public boolean isWatchtower() {
    return publicationType == PublicationType.WATCHTOWER;
  }

  /**
   * Serializes this result to a JSON string.
   *
   * @return JSON representation of this result
   */
  public String toJson() {
    try {
      return OBJECT_MAPPER.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize ParseResult to JSON", e);
    }
  }

  /**
   * Serializes the schedules only to a JSON array string (compatible with Node.js library output).
   *
   * @return JSON array of schedules
   */
  public String toSchedulesJson() {
    try {
      if (mwbSchedules != null) {
        return OBJECT_MAPPER.writeValueAsString(mwbSchedules);
      } else if (wSchedules != null) {
        return OBJECT_MAPPER.writeValueAsString(wSchedules);
      }
      return "[]";
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize schedules to JSON", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParseResult that = (ParseResult) o;
    return year == that.year
        && month == that.month
        && publicationType == that.publicationType
        && Objects.equals(language, that.language)
        && Objects.equals(mwbSchedules, that.mwbSchedules)
        && Objects.equals(wSchedules, that.wSchedules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(publicationType, language, year, month, mwbSchedules, wSchedules);
  }

  @Override
  public String toString() {
    return "ParseResult{"
        + "publicationType="
        + publicationType
        + ", language='"
        + language
        + '\''
        + ", year="
        + year
        + ", month="
        + month
        + ", schedulesCount="
        + (mwbSchedules != null ? mwbSchedules.size() : wSchedules != null ? wSchedules.size() : 0)
        + '}';
  }

  /** Type of publication. */
  public enum PublicationType {
    /** Meeting Workbook */
    MWB,
    /** Watchtower Study */
    WATCHTOWER
  }

  public static final class Builder {
    private PublicationType publicationType;
    private String language;
    private int year;
    private int month;
    private List<MWBSchedule> mwbSchedules;
    private List<WSchedule> wSchedules;

    private Builder() {}

    public Builder publicationType(PublicationType publicationType) {
      this.publicationType = publicationType;
      return this;
    }

    public Builder language(String language) {
      this.language = language;
      return this;
    }

    public Builder year(int year) {
      this.year = year;
      return this;
    }

    public Builder month(int month) {
      this.month = month;
      return this;
    }

    public Builder mwbSchedules(List<MWBSchedule> mwbSchedules) {
      this.mwbSchedules = mwbSchedules;
      return this;
    }

    public Builder wSchedules(List<WSchedule> wSchedules) {
      this.wSchedules = wSchedules;
      return this;
    }

    public ParseResult build() {
      Objects.requireNonNull(publicationType, "publicationType is required");
      Objects.requireNonNull(language, "language is required");
      if (year < 2022) {
        throw new IllegalArgumentException("year must be 2022 or later");
      }
      if (month < 1 || month > 12) {
        throw new IllegalArgumentException("month must be between 1 and 12");
      }
      return new ParseResult(this);
    }
  }
}
