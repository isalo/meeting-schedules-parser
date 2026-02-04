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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Options for downloading publications from URLs.
 *
 * <p>Use the {@link Builder} to create instances with custom settings.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class DownloadOptions {

  private static final DownloadOptions DEFAULT = new Builder().build();

  private final Duration connectionTimeout;
  private final Duration readTimeout;
  private final long maxSize;
  private final Map<String, String> headers;
  private final boolean followRedirects;

  private DownloadOptions(Builder builder) {
    this.connectionTimeout = builder.connectionTimeout;
    this.readTimeout = builder.readTimeout;
    this.maxSize = builder.maxSize;
    this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
    this.followRedirects = builder.followRedirects;
  }

  /** Returns the default download options. */
  public static DownloadOptions defaults() {
    return DEFAULT;
  }

  /** Returns a new builder for creating custom options. */
  public static Builder builder() {
    return new Builder();
  }

  /** Returns the connection timeout. */
  public Duration getConnectionTimeout() {
    return connectionTimeout;
  }

  /** Returns the read timeout. */
  public Duration getReadTimeout() {
    return readTimeout;
  }

  /** Returns the maximum download size in bytes. */
  public long getMaxSize() {
    return maxSize;
  }

  /** Returns the custom HTTP headers to include in the request. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /** Returns true if redirects should be followed. */
  public boolean isFollowRedirects() {
    return followRedirects;
  }

  /** Builder for {@link DownloadOptions}. */
  public static final class Builder {
    private Duration connectionTimeout = ParserOptions.DEFAULT_CONNECTION_TIMEOUT;
    private Duration readTimeout = ParserOptions.DEFAULT_READ_TIMEOUT;
    private long maxSize = ParserOptions.DEFAULT_MAX_FILE_SIZE;
    private Map<String, String> headers = new HashMap<>();
    private boolean followRedirects = true;

    private Builder() {}

    /**
     * Sets the connection timeout.
     *
     * @param connectionTimeout connection timeout
     * @return this builder
     */
    public Builder connectionTimeout(Duration connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    /**
     * Sets the read timeout.
     *
     * @param readTimeout read timeout
     * @return this builder
     */
    public Builder readTimeout(Duration readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    /**
     * Sets the maximum download size.
     *
     * @param maxSize maximum size in bytes
     * @return this builder
     */
    public Builder maxSize(long maxSize) {
      this.maxSize = maxSize;
      return this;
    }

    /**
     * Adds a custom HTTP header.
     *
     * @param name header name
     * @param value header value
     * @return this builder
     */
    public Builder header(String name, String value) {
      this.headers.put(name, value);
      return this;
    }

    /**
     * Sets whether to follow redirects.
     *
     * @param followRedirects true to follow redirects
     * @return this builder
     */
    public Builder followRedirects(boolean followRedirects) {
      this.followRedirects = followRedirects;
      return this;
    }

    /**
     * Builds the download options.
     *
     * @return new DownloadOptions instance
     */
    public DownloadOptions build() {
      return new DownloadOptions(this);
    }
  }
}
