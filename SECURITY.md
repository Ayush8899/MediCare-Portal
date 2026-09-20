# Security Notes

Please do not commit passwords, API secrets, database credentials, JWT signing keys, or real patient information.

If a credential is accidentally committed:

1. Revoke or rotate it immediately.
2. Remove it from the working tree.
3. If it was pushed to Git history, remove it from history as well.
4. Check CI logs and repository history for copies.

For production use, keep secrets in a dedicated secret manager and use HTTPS, strong authentication policies, audit logging, backups, monitoring and a formal security/privacy review.
