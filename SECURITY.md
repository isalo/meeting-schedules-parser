# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability, please report it privately:

1. **Do not** open a public GitHub issue
2. Email security concerns to: info@cykor.net
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

## Response Timeline

- Initial response: within 48 hours
- Status update: within 7 days
- Resolution target: within 30 days (depending on severity)

## Security Measures

This library implements:

- **ZIP-slip protection**: Validates archive paths to prevent directory traversal
- **File size limits**: Prevents denial-of-service via oversized files
- **Archive file limits**: Prevents resource exhaustion from excessive files
- **Connection timeouts**: Prevents hanging on network operations
- **Input validation**: Validates all user inputs before processing

## Best Practices for Users

- Keep the library updated to the latest version
- Configure appropriate file size limits for your use case
- Validate file sources before parsing
- Handle exceptions appropriately
