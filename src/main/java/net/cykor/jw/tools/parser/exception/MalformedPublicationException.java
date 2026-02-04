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
 * Exception thrown when the publication content is malformed or corrupted.
 *
 * <p>This includes:
 *
 * <ul>
 *   <li>Invalid or corrupted ZIP archives
 *   <li>Missing required files inside the archive
 *   <li>Invalid database structure in JWPUB files
 *   <li>Decryption failures
 *   <li>Malformed HTML content
 * </ul>
 */
public class MalformedPublicationException extends ParserException {

  private static final long serialVersionUID = 1L;

  public MalformedPublicationException(String message) {
    super(ErrorCode.MALFORMED_CONTENT, message);
  }

  public MalformedPublicationException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public MalformedPublicationException(String message, Throwable cause) {
    super(ErrorCode.MALFORMED_CONTENT, message, cause);
  }

  public MalformedPublicationException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
