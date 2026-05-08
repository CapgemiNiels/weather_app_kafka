# Startup

## Requirements

- Java 21 for the project build configuration
- Docker Desktop or a working Docker daemon for the local Kafka stack
- Maven Wrapper (`mvnw.cmd`) for local test runs

### start docker daemon
start a debian terminal and run\
`sudo dockerd`

### start docker-compose
start a new debian terminal and run\
`docker compose up -d`

### verify local services
- Kafka broker: `localhost:9092`
- Schema Registry: `http://localhost:8081`
- Kafka UI: `http://localhost:8085`

check if a kafka container was still running with\
`sudo docker container ls` or `sudo docker ps`

if there was already a kafka container running, stop it first with\
`docker container stop kafka`

check if a kafka container was present but not running with\
`docker container ls -a` or `docker ps -a`

and remove the entire container with\
`docker container rm kafka`

### start the app
run the WeatherAppKafkaApplication class in your IDE

### run the tests
from the project root run\
`./mvnw.cmd test`

### generate Avro classes
from the project root run\
`./mvnw.cmd generate-sources`

### open the kafka-ui in your browser
http://localhost:8085/ opens the UI
