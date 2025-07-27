#!/usr/bin/env bash
# wait-for-it.sh: Wait until a TCP host:port is available

set -e

HOST="$1"
PORT="$2"
shift 2
CMD="$@"

echo "Waiting for $HOST:$PORT..."

while ! nc -z "$HOST" "$PORT"; do
  echo "Waiting for $HOST:$PORT to be available..."
  sleep 2
done

echo "$HOST:$PORT is available, executing command: $CMD"
exec "$@"
