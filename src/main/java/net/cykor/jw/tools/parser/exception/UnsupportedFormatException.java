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
 * Exception thrown when the file format is not supported.
 *
 * <p>This includes:
 *
 * <ul>
 *   <li>Files that are not JWPUB or EPUB format
 *   <li>Files with incorrect naming convention
 *   <li>Publication issues that are too old (MWB before July 2022, W before April 2023)
 * </ul>
 */
public class UnsupportedFormatException extends ParserException {

  private static final long serialVersionUID = 1L;

  public UnsupportedFormatException(String message) {
    super(ErrorCode.UNSUPPORTED_FORMAT, message);
  }

  public UnsupportedFormatException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public UnsupportedFormatException(String message, Throwable cause) {
    super(ErrorCode.UNSUPPORTED_FORMAT, message, cause);
  }
}
