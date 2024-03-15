#!/bin/bash
echo "Attente de Kafka pour démarrer..."
until $(kafka-topics --bootstrap-server kafka:9092 --list > /dev/null 2>&1); do
  sleep 1
done

# crée les topics
kafka-topics --bootstrap-server kafka:9092 --create --topic topic1 --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic topic2 --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic topic3 --partitions 1 --replication-factor 1

echo "Topics créés."
