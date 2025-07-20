# Docker Commands for Kafka Setup and Testing

## Start Kafka Cluster
```bash
# Create a docker network for Kafka
docker network create kafka-net

# Start Zookeeper
docker run -d --name zookeeper --network kafka-net -p 2181:2181 -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:latest

# Start Kafka
docker run -d --name kafkaclust-kafka-1 --network kafka-net -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafkaclust-kafka-1:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:latest
```

## Create Kafka Topics
```bash
# Create the clicks topic
docker exec kafkaclust-kafka-1 kafka-topics --create --topic clicks --bootstrap-server kafkaclust-kafka-1:9092 --partitions 3 --replication-factor 1

# Create the click-counts topic
docker exec kafkaclust-kafka-1 kafka-topics --create --topic click-counts --bootstrap-server kafkaclust-kafka-1:9092 --partitions 3 --replication-factor 1

# List topics to verify
docker exec kafkaclust-kafka-1 kafka-topics --list --bootstrap-server kafkaclust-kafka-1:9092
```

## Test with Kafka Console Producer and Consumer

### Test Producer
```bash
# Start a console producer for the clicks topic
docker exec -it kafkaclust-kafka-1 kafka-console-producer --topic clicks --bootstrap-server kafkaclust-kafka-1:9092 --property "parse.key=true" --property "key.separator=:"

# Enter messages in the format: userId:click
# Example:
# user1:click
# user2:click
# user1:click
```

### Test Consumer for Clicks Topic
```bash
# Start a console consumer for the clicks topic
docker exec -it kafkaclust-kafka-1 kafka-console-consumer --topic clicks --bootstrap-server kafkaclust-kafka-1:9092 --from-beginning --property "print.key=true" --property "key.separator=:"
```

### Test Consumer for Click-Counts Topic
```bash
# Start a console consumer for the click-counts topic
docker exec -it kafkaclust-kafka-1 kafka-console-consumer --topic click-counts --bootstrap-server kafkaclust-kafka-1:9092 --from-beginning --property "print.key=true" --property "key.separator=:"
```

## Running the Spring Boot Application
```bash
# Make sure to update application.yml with the correct Kafka bootstrap server: kafkaclust-kafka-1:9092

# Build the application
./mvnw clean package

# Run the application
java -jar target/click-counter-app-0.0.1-SNAPSHOT.jar
```

## Testing the Application
1. Open a web browser and navigate to http://localhost:9090
2. Click the "Cliquez ici" button multiple times
3. Check the click count displayed on the page
4. You can also check the REST API at http://localhost:9090/clicks/count

## Cleanup
```bash
# Stop and remove containers
docker stop kafkaclust-kafka-1 zookeeper
docker rm kafkaclust-kafka-1 zookeeper

# Remove network
docker network rm kafka-net
```