#!/bin/bash

set -e

echo "⏳ Waiting for Kafka Connect..."
sleep 10

echo "🚀 Registering Debezium connector..."
curl -X POST \
  -H "Content-Type: application/json" \
  --data @debezium/connectors/events.json \
  http://localhost:8083/connectors

curl -X POST -H "Content-Type: application/json" \
--data @debezium/connectors/inventory.json \
http://localhost:8083/connectors

echo "✅ Debezium connector registered"

echo "🚀 Creating Kafka topics..."

docker exec kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --topic orders.events \
  --partitions 3 \
  --replication-factor 1 || true

docker exec kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --topic validated.orders.events \
  --partitions 3 \
  --replication-factor 1 || true

echo "🎉 Kafka topics ready"
