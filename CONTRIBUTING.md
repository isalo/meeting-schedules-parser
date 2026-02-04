# Contributing to Meeting Schedules Parser

Thank you for your interest in contributing to Meeting Schedules Parser!

## How to Contribute

### Reporting Issues

- Check existing issues before creating a new one
- Provide a clear description of the problem
- Include reproduction steps if applicable
- Attach sample files if relevant (ensure no sensitive data)

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Run tests: `./gradlew test`
5. Check formatting: `./gradlew spotlessCheck`
6. Commit with clear messages
7. Push and create a Pull Request

### Code Style

- Follow existing code conventions
- Run `./gradlew spotlessApply` to auto-format
- Write Javadoc for public APIs
- Add tests for new functionality

### Commit Messages

Use clear, descriptive commit messages:
- `feat: Add support for new language`
- `fix: Handle edge case in date parsing`
- `docs: Update README examples`
- `test: Add integration tests for URL parsing`

## Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/meeting-schedules-parser.git

# Build
./gradlew build

# Run tests
./gradlew test

# Format code
./gradlew spotlessApply
```

## Adding Language Support

To add enhanced parsing for a new language:

1. Update `LanguageSupport.java` with language-specific patterns
2. Add month name mappings
3. Add date parsing patterns
4. Write tests for the new language

## Questions?

Open an issue for discussion or clarification.

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.
