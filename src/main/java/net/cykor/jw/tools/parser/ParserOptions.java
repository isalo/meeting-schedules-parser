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

import java.time.Duration;

/**
 * Configuration options for the parser.
 *
 * <p>Use the {@link Builder} to create instances with custom settings.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class ParserOptions {

  /** Default maximum file size: 20 MB */
  public static final long DEFAULT_MAX_FILE_SIZE = 20 * 1024 * 1024L;

  /** Default maximum number of files in archive: 300 */
  public static final int DEFAULT_MAX_FILES = 300;

  /** Default connection timeout: 30 seconds */
  public static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(30);

  /** Default read timeout: 60 seconds */
  public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(60);

  private static final ParserOptions DEFAULT = new Builder().build();

  private final boolean strict;
  private final boolean enableEnhancedParsing;
  private final long maxFileSize;
  private final int maxFiles;
  private final Duration connectionTimeout;
  private final Duration readTimeout;

  private ParserOptions(Builder builder) {
    this.strict = builder.strict;
    this.enableEnhancedParsing = builder.enableEnhancedParsing;
    this.maxFileSize = builder.maxFileSize;
    this.maxFiles = builder.maxFiles;
    this.connectionTimeout = builder.connectionTimeout;
    this.readTimeout = builder.readTimeout;
  }

  /** Returns the default parser options. */
  public static ParserOptions defaults() {
    return DEFAULT;
  }

  /** Returns a new builder for creating custom options. */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns true if strict mode is enabled.
   *
   * <p>In strict mode, the parser will fail on any unexpected content. In lenient mode (default),
   * the parser will try to extract as much data as possible.
   */
  public boolean isStrict() {
    return strict;
  }

  /**
   * Returns true if enhanced parsing is enabled.
   *
   * <p>Enhanced parsing extracts additional metadata like part types and timing for supported
   * languages.
   */
  public boolean isEnhancedParsingEnabled() {
    return enableEnhancedParsing;
  }

  /** Returns the maximum allowed file size in bytes. */
  public long getMaxFileSize() {
    return maxFileSize;
  }

  /** Returns the maximum number of files allowed in an archive. */
  public int getMaxFiles() {
    return maxFiles;
  }

  /** Returns the connection timeout for HTTP downloads. */
  public Duration getConnectionTimeout() {
    return connectionTimeout;
  }

  /** Returns the read timeout for HTTP downloads. */
  public Duration getReadTimeout() {
    return readTimeout;
  }

  /** Builder for {@link ParserOptions}. */
  public static final class Builder {
    private boolean strict = false;
    private boolean enableEnhancedParsing = true;
    private long maxFileSize = DEFAULT_MAX_FILE_SIZE;
    private int maxFiles = DEFAULT_MAX_FILES;
    private Duration connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private Duration readTimeout = DEFAULT_READ_TIMEOUT;

    private Builder() {}

    /**
     * Enables or disables strict mode.
     *
     * @param strict true to enable strict mode
     * @return this builder
     */
    public Builder strict(boolean strict) {
      this.strict = strict;
      return this;
    }

    /**
     * Enables or disables enhanced parsing.
     *
     * @param enableEnhancedParsing true to enable enhanced parsing
     * @return this builder
     */
    public Builder enableEnhancedParsing(boolean enableEnhancedParsing) {
      this.enableEnhancedParsing = enableEnhancedParsing;
      return this;
    }

    /**
     * Sets the maximum file size.
     *
     * @param maxFileSize maximum file size in bytes
     * @return this builder
     */
    public Builder maxFileSize(long maxFileSize) {
      if (maxFileSize <= 0) {
        throw new IllegalArgumentException("maxFileSize must be positive");
      }
      this.maxFileSize = maxFileSize;
      return this;
    }

    /**
     * Sets the maximum number of files allowed in an archive.
     *
     * @param maxFiles maximum number of files
     * @return this builder
     */
    public Builder maxFiles(int maxFiles) {
      if (maxFiles <= 0) {
        throw new IllegalArgumentException("maxFiles must be positive");
      }
      this.maxFiles = maxFiles;
      return this;
    }

    /**
     * Sets the connection timeout for HTTP downloads.
     *
     * @param connectionTimeout connection timeout
     * @return this builder
     */
    public Builder connectionTimeout(Duration connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    /**
     * Sets the read timeout for HTTP downloads.
     *
     * @param readTimeout read timeout
     * @return this builder
     */
    public Builder readTimeout(Duration readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    /**
     * Builds the parser options.
     *
     * @return new ParserOptions instance
     */
    public ParserOptions build() {
      return new ParserOptions(this);
    }
  }
}
