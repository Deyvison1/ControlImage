#!/bin/bash

set -e

echo "================================="
echo "🚀 Deploy ImageApi"
echo "================================="

echo "🐳 Rebuild e restart..."

docker compose up -d --build

echo "✅ Deploy concluído!"

docker ps --filter "name=image-api"