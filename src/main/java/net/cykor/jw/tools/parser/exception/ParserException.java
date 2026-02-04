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
package net.cykor.jw.tools.parser.exception;

/**
 * Base exception for all parser-related errors.
 *
 * @see UnsupportedFormatException
 * @see MalformedPublicationException
 * @see DownloadFailedException
 */
public class ParserException extends Exception {

  private static final long serialVersionUID = 1L;

  private final ErrorCode errorCode;

  public ParserException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ParserException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /** Returns the error code for this exception. */
  public ErrorCode getErrorCode() {
    return errorCode;
  }

  /** Error codes for parser exceptions. */
  public enum ErrorCode {
    /** File format is not supported (not JWPUB or EPUB) */
    UNSUPPORTED_FORMAT,
    /** File naming convention is incorrect */
    INVALID_FILENAME,
    /** Publication issue date is not supported */
    UNSUPPORTED_ISSUE,
    /** Publication content is malformed or corrupted */
    MALFORMED_CONTENT,
    /** ZIP archive is invalid or corrupted */
    INVALID_ARCHIVE,
    /** Database inside JWPUB is invalid */
    INVALID_DATABASE,
    /** Decryption of JWPUB content failed */
    DECRYPTION_FAILED,
    /** Failed to download publication from URL */
    DOWNLOAD_FAILED,
    /** Download timed out */
    DOWNLOAD_TIMEOUT,
    /** File size exceeds limit */
    FILE_TOO_LARGE,
    /** Archive contains too many files */
    TOO_MANY_FILES,
    /** Suspicious archive content (potential zip-slip attack) */
    SUSPICIOUS_CONTENT,
    /** I/O error during parsing */
    IO_ERROR
  }
}
