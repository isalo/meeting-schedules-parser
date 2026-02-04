# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2024-XX-XX

### Added

- Initial release
- Parse JWPUB and EPUB format publications
- Support for Meeting Workbook (MWB) schedules
- Support for Watchtower Study (W) schedules
- Parse from local files, InputStreams, byte arrays, and URLs
- Strongly-typed Java POJOs for schedule data
- JSON serialization with Jackson
- Enhanced parsing for English, Ukrainian, and Polish languages
- Configurable parser options (file size limits, timeouts, strict mode)
- Custom exceptions for error handling
- ZIP-slip protection
- Thread-safe implementation
- Comprehensive unit tests
- CI/CD pipeline with GitHub Actions
- Maven Central publishing support

### Security

- ZIP-slip vulnerability protection
- File size limits to prevent DoS
- Archive file count limits
