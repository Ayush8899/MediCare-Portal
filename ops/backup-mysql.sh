#!/bin/sh
set -eu
: "${MYSQL_HOST:=localhost}"
: "${MYSQL_PORT:=3306}"
: "${MYSQL_USER:=root}"
: "${MYSQL_DATABASE:=healthcare_db}"
: "${BACKUP_DIR:=./backups}"
mkdir -p "$BACKUP_DIR"
STAMP=$(date +%Y%m%d-%H%M%S)
if [ -z "${MYSQL_PASSWORD:-}" ]; then echo 'Set MYSQL_PASSWORD before running backup.' >&2; exit 1; fi
mysqldump -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" --single-transaction --routines --triggers "$MYSQL_DATABASE" > "$BACKUP_DIR/${MYSQL_DATABASE}-${STAMP}.sql"
echo "Backup created: $BACKUP_DIR/${MYSQL_DATABASE}-${STAMP}.sql"
