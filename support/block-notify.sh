#!/bin/sh

# block-notify.sh
#
# Part of the moneropool project.
#
# Usage: block-notify.sh NETWORK BLOCK_HASH
#
# Environment variables:
#  - REDIS_HOST: Hostname of Redis server (default: 127.0.0.1)
#  - REDIS_PORT: Port of Redis server (default: 6379)

REDIS_HOST=${REDIS_HOST:-127.0.0.1}
REDIS_PORT=${REDIS_PORT:-6379}

echo "PUBLISH monero.${1}.blocks ${2}" | nc -q0 "${REDIS_HOST}" "${REDIS_PORT}"
