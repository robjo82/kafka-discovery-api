#!/bin/bash

set -e

host="$1"
port="$2"

until PGPASSWORD="$DB_PASSWORD" pg_isready -h "$host" -p "$port" -U "$DB_USER" ; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - continuing..."
