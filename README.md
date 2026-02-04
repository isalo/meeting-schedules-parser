# Meeting Schedules Parser

[![CI](https://github.com/isalo/meeting-schedules-parser/actions/workflows/ci.yml/badge.svg)](https://github.com/nicecykor/meeting-schedules-parser/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/net.cykor.jw.tools/meeting-schedules-parser)](https://central.sonatype.com/artifact/net.cykor.jw.tools/meeting-schedules-parser)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Java library for parsing JW Meeting Workbook (MWB) and Watchtower Study (W) schedules from JWPUB and EPUB files.

## Features

- Parse JWPUB and EPUB publication files
- Support for local files, InputStreams, byte arrays, and URLs
- Strongly-typed Java POJOs for schedule data
- JSON serialization with Jackson
- Enhanced parsing for English, Ukrainian, and Polish languages
- Thread-safe and immutable models
- Comprehensive error handling with custom exceptions
- ZIP-slip protection and file size limits

## Requirements

- Java 21 or later

## Installation

### Gradle

```groovy
implementation 'net.cykor.jw.tools:meeting-schedules-parser:1.0.0'
```

### Gradle (Kotlin DSL)

```kotlin
implementation("net.cykor.jw.tools:meeting-schedules-parser:1.0.0")
```

### Maven

```xml
<dependency>
    <groupId>net.cykor.jw.tools</groupId>
    <artifactId>meeting-schedules-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Parse from File

```java
import net.cykor.jw.tools.parser.MeetingSchedulesParser;
import net.cykor.jw.tools.parser.ParseResult;
import java.nio.file.Path;

MeetingSchedulesParser parser = MeetingSchedulesParser.create();
ParseResult result = parser.parse(Path.of("mwb_E_202401.jwpub"));

// Access MWB schedules
for (MWBSchedule schedule : result.getMwbSchedules()) {
    System.out.println("Week: " + schedule.getWeekDate());
    System.out.println("Bible Reading: " + schedule.getWeeklyBibleReading());
    System.out.println("First Song: " + schedule.getSongFirst());
}
```

### Parse from URL

```java
import net.cykor.jw.tools.parser.DownloadOptions;
import java.net.URI;

ParseResult result = parser.parse(
    URI.create("https://example.com/mwb_E_202401.jwpub"),
    DownloadOptions.defaults()
);
```

### Parse from InputStream

```java
import net.cykor.jw.tools.parser.Format;
import java.io.InputStream;

try (InputStream is = new FileInputStream("mwb_E_202401.epub")) {
    ParseResult result = parser.parse(is, Format.EPUB, "mwb_E_202401.epub");
}
```

### Get JSON Output

```java
// Full result with metadata
String fullJson = result.toJson();

// Just schedules array (compatible with Node.js library)
String schedulesJson = result.toSchedulesJson();
```

### Custom Parser Options

```java
import net.cykor.jw.tools.parser.ParserOptions;
import java.time.Duration;

ParserOptions options = ParserOptions.builder()
    .strict(true)
    .enableEnhancedParsing(true)
    .maxFileSize(50 * 1024 * 1024)  // 50 MB
    .maxFiles(500)
    .connectionTimeout(Duration.ofSeconds(60))
    .readTimeout(Duration.ofSeconds(120))
    .build();

MeetingSchedulesParser parser = MeetingSchedulesParser.create(options);
```

## Supported Publications

| Publication | Format | Minimum Issue |
|-------------|--------|---------------|
| Meeting Workbook (mwb) | JWPUB, EPUB | July 2022 |
| Watchtower Study (w) | JWPUB, EPUB | April 2023 |

### Filename Convention

Files must follow the naming convention:
- MWB: `mwb_LANG_YYYYMM.jwpub` or `mwb_LANG_YYYYMM.epub`
- Watchtower: `w_LANG_YYYYMM.jwpub` or `w_LANG_YYYYMM.epub`

Where:
- `LANG` is the language code (e.g., E for English, U for Ukrainian, P for Polish)
- `YYYY` is the 4-digit year
- `MM` is the 2-digit month

## Enhanced Parsing

Enhanced parsing extracts additional metadata like part types, timing, and formatted dates. Currently supported languages:

- **English (E)**
- **Ukrainian (U)**
- **Polish (P)**

For other languages, basic parsing is available with raw text content.

## Error Handling

The library provides specific exceptions for different error cases:

```java
import net.cykor.jw.tools.parser.exception.*;

try {
    ParseResult result = parser.parse(path);
} catch (UnsupportedFormatException e) {
    // Invalid filename format or unsupported publication issue
    System.err.println("Format error: " + e.getMessage());
} catch (MalformedPublicationException e) {
    // Invalid or corrupted file content
    System.err.println("Content error: " + e.getMessage());
} catch (DownloadFailedException e) {
    // Network/download issues (for URL parsing)
    System.err.println("Download error: " + e.getMessage());
    if (e.getHttpStatusCode() > 0) {
        System.err.println("HTTP Status: " + e.getHttpStatusCode());
    }
} catch (ParserException e) {
    // General parser error
    System.err.println("Parser error: " + e.getErrorCode());
}
```

## Thread Safety

- `MeetingSchedulesParser` instances are thread-safe and can be shared
- All model classes (`MWBSchedule`, `WSchedule`, `ParseResult`) are immutable
- `ParserOptions` and `DownloadOptions` are immutable

## Building from Source

```bash
# Clone the repository
git clone https://github.com/isalo/meeting-schedules-parser.git
cd meeting-schedules-parser

# Build
./gradlew build

# Run tests
./gradlew test

# Generate Javadoc
./gradlew javadoc

# Install to local Maven repository
./gradlew publishToMavenLocal
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history.
