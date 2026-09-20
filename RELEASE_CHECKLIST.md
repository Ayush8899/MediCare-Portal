# Release checklist

Before pushing this repository to GitHub:

- [x] Removed local IDE folders and generated build output
- [x] Removed the old Maven backup file
- [x] Removed hard-coded mail and Razorpay credentials
- [x] Added environment-based configuration
- [x] Added root `.gitignore`
- [x] Added security notes
- [x] Kept one root Docker Compose setup
- [x] Updated the README to match the current project
- [x] Added a test profile for CI
- [x] Kept demo credentials clearly marked as local-only

One local limitation: this workspace cannot reach Maven Central, so the Maven test suite could not be executed here. The repository contains the Maven wrapper and GitHub Actions workflow; run `backend\\mvnw.cmd test` locally before the first push.
