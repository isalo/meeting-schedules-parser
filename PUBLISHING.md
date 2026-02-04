# Publishing Guide

This document provides step-by-step instructions for publishing the library to Maven Central using the **Sonatype Central Portal** (the new process as of 2024).

> **Note**: The old OSSRH process via `issues.sonatype.org` has been decommissioned. This guide uses the new Central Portal at https://central.sonatype.com

---

## Table of Contents

1. [First-Time Setup](#first-time-setup)
   - [Step 1: Create Central Portal Account](#step-1-create-central-portal-account)
   - [Step 2: Verify Your Namespace](#step-2-verify-your-namespace)
   - [Step 3: Generate GPG Key](#step-3-generate-gpg-key)
   - [Step 4: Generate Portal Token](#step-4-generate-portal-token)
   - [Step 5: Configure GitHub Secrets](#step-5-configure-github-secrets)
2. [Publishing Process](#publishing-process)
3. [Local Development](#local-development)
4. [Troubleshooting](#troubleshooting)

---

## First-Time Setup

### Step 1: Create Central Portal Account

1. Go to https://central.sonatype.com
2. Click **"Sign In"** in the top right
3. Choose your sign-in method:
   - **GitHub** (recommended - easiest)
   - Google
   - Email/Password
4. Complete the registration process

### Step 2: Verify Your Namespace

Before you can publish, you must verify ownership of your namespace (`net.cykor`).

1. Log in to https://central.sonatype.com
2. Click **"Namespaces"** in the left sidebar (or go to https://central.sonatype.com/publishing/namespaces)
3. Click **"Add Namespace"**
4. Enter your namespace: `net.cykor`
5. Choose verification method:

   **Option A: Domain Verification (Recommended for custom domains)**
   - Add a TXT record to your domain's DNS:
     - Host: `@` or root
     - Value: The verification code provided by Central Portal
   - Wait for DNS propagation (can take up to 24 hours)
   - Click "Verify" once DNS is set

   **Option B: GitHub Verification (If using `io.github.username`)**
   - Create a public repository with the exact name provided
   - The portal will verify ownership automatically

6. Once verified, your namespace status will show **"Verified"** ✅

### Step 3: Generate GPG Key

GPG signing is required for Maven Central. Follow these steps:

#### 3.1 Install GPG

```bash
# macOS
brew install gnupg

# Ubuntu/Debian
sudo apt-get install gnupg

# Windows - download from https://gpg4win.org
```

#### 3.2 Generate a Key Pair

```bash
gpg --full-generate-key
```

When prompted:
- **Key type**: Select `(1) RSA and RSA`
- **Key size**: `4096`
- **Expiration**: `0` (does not expire) or set your preference
- **Real name**: Your name
- **Email**: Your email
- **Passphrase**: Choose a strong passphrase (you'll need this later!)

#### 3.3 List Your Keys

```bash
gpg --list-secret-keys --keyid-format LONG
```

Output example:
```
sec   rsa4096/ABCD1234EFGH5678 2024-01-15 [SC]
      1234567890ABCDEF1234567890ABCDEF12345678
uid                 [ultimate] Your Name <your@email.com>
ssb   rsa4096/1234ABCD5678EFGH 2024-01-15 [E]
```

The key ID is `ABCD1234EFGH5678` (the part after `rsa4096/`).

#### 3.4 Publish Your Public Key

Your public key must be available on a key server for verification:

```bash
# Replace ABCD1234EFGH5678 with your key ID
gpg --keyserver keyserver.ubuntu.com --send-keys ABCD1234EFGH5678

# Also publish to other servers for redundancy
gpg --keyserver keys.openpgp.org --send-keys ABCD1234EFGH5678
gpg --keyserver pgp.mit.edu --send-keys ABCD1234EFGH5678
```

#### 3.5 Export Private Key for CI/CD

```bash
# Export the private key (replace KEY_ID with your key ID)
gpg --armor --export-secret-keys ABCD1234EFGH5678 > private-key.asc

# View the content (you'll paste this into GitHub Secrets)
cat private-key.asc
```

**⚠️ Security Warning**: Never commit `private-key.asc` to Git! Delete it after copying to GitHub Secrets.

### Step 4: Generate Portal Token

1. Log in to https://central.sonatype.com
2. Click your username in the top right → **"View Account"**
3. Go to **"Generate User Token"** section
4. Click **"Generate Token"**
5. **Copy both values immediately** - they won't be shown again!
   - **Username**: Something like `wXyZ1234`
   - **Password**: A long token string

### Step 5: Configure GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these secrets:

| Secret Name | Value | Description |
|-------------|-------|-------------|
| `CENTRAL_USERNAME` | `wXyZ1234` | Portal token username from Step 4 |
| `CENTRAL_PASSWORD` | `your-token-password` | Portal token password from Step 4 |
| `GPG_SIGNING_KEY` | Contents of `private-key.asc` | Your exported GPG private key |
| `GPG_SIGNING_PASSWORD` | Your GPG passphrase | The passphrase you set in Step 3.2 |

---

## Publishing Process

### Automated Release (Recommended)

#### 1. Update Version

Edit `build.gradle`:
```groovy
version = '1.0.0'  // Set your release version
```

#### 2. Update Changelog

Update `CHANGELOG.md` with release notes for this version.

#### 3. Commit, Tag, and Push

```bash
git add .
git commit -m "Release v1.0.0"
git tag v1.0.0
git push origin main --tags
```

#### 4. Monitor the Release

1. Go to **Actions** tab in your GitHub repository
2. Watch the "Release" workflow run
3. Once successful, go to https://central.sonatype.com/publishing/deployments
4. Find your deployment and click **"Publish"** (if not auto-published)

#### 5. Verify Publication

After publishing, your artifact will be available at:
- **Maven Central**: https://repo1.maven.org/maven2/net/cykor/jw/tools/meeting-schedules-parser/
- **Search**: https://central.sonatype.com/artifact/net.cykor.jw.tools/meeting-schedules-parser

> **Note**: It may take 10-30 minutes for artifacts to appear on Maven Central after publishing.

### Manual Release

If you need to publish manually:

```bash
# Set environment variables
export CENTRAL_USERNAME=your-portal-token-username
export CENTRAL_PASSWORD=your-portal-token-password
export GPG_SIGNING_KEY="$(cat private-key.asc)"
export GPG_SIGNING_PASSWORD=your-gpg-passphrase

# Build and publish
./gradlew clean build publishMavenJavaPublicationToCentralPortalRepository

# Trigger validation and upload to Central Portal
curl -X POST \
  "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/net.cykor" \
  -u "$CENTRAL_USERNAME:$CENTRAL_PASSWORD"
```

Then go to https://central.sonatype.com/publishing/deployments to review and publish.

---

## Local Development

### Publish to Local Maven Repository

For testing your library locally before publishing:

```bash
./gradlew publishToMavenLocal
```

Artifacts will be in: `~/.m2/repository/net/cykor/jw/tools/meeting-schedules-parser/`

### Verify Generated Artifacts

```bash
# Build all artifacts
./gradlew build

# Check the build directory
ls -la build/libs/

# Should contain:
# - meeting-schedules-parser-1.0.0.jar
# - meeting-schedules-parser-1.0.0-javadoc.jar
# - meeting-schedules-parser-1.0.0-sources.jar

# Verify POM
./gradlew generatePomFileForMavenJavaPublication
cat build/publications/mavenJava/pom-default.xml
```

---

## Troubleshooting

### "Namespace not verified"

- Ensure your namespace is verified at https://central.sonatype.com/publishing/namespaces
- DNS changes can take up to 24 hours to propagate

### "401 Unauthorized"

- Regenerate your Portal token (they can expire)
- Ensure you're using **Portal tokens**, not old OSSRH credentials
- Check that secrets are correctly set in GitHub

### "Signature verification failed"

```bash
# Verify your GPG key is working
echo "test" | gpg --clearsign

# Check if your public key is on keyservers
gpg --keyserver keyserver.ubuntu.com --recv-keys YOUR_KEY_ID
```

### "POM validation failed"

Maven Central requires these POM elements:
- `name`
- `description`
- `url`
- `licenses`
- `developers`
- `scm`

All are configured in `build.gradle`. Verify with:
```bash
./gradlew generatePomFileForMavenJavaPublication
cat build/publications/mavenJava/pom-default.xml
```

### "Deployment not visible in Central Portal"

If using the OSSRH Staging API, you must trigger the upload:
```bash
curl -X POST \
  "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/net.cykor" \
  -u "$CENTRAL_USERNAME:$CENTRAL_PASSWORD"
```

### Useful Links

- **Central Portal**: https://central.sonatype.com
- **Publishing Guide**: https://central.sonatype.org/publish/publish-portal-guide/
- **Requirements**: https://central.sonatype.org/publish/requirements/
- **Gradle Publishing**: https://central.sonatype.org/publish/publish-portal-gradle/
- **Support**: https://central.sonatype.org/support/
