# Weather App Kafka

weather app based off youtube series on Kafka from the Quix channel https://www.youtube.com/watch?v=D2NYvGlbK0M

# Startup

### start docker-compose
Run from the project root:
`docker compose up -d`

Services and ports:
- Kafka broker: `localhost:9092`
- Schema Registry: `http://localhost:8081`
- Kafka UI: `http://localhost:8085`

### start the app
Run `WeatherAppKafkaApplication` in your IDE.

The producer publishes Avro records to topic `weather_input` using:
- `KafkaAvroSerializer`
- subject naming `TopicNameStrategy` (`weather_input-value`)
- schema registry URL `http://127.0.0.1:8081`

### generate Avro sources manually (optional)
`./mvnw.cmd generate-sources`

<br>
<br>
<br>
<br>
<br>

#### troubleshooting

when starting the docker compose up from the weather reader kafka project, 
the settings need to be a bit different in application.properties  
I added a few commented lines, uncomment them and comment out  
`spring.kafka.bootstrap-servers=127.0.0.1:9092`  
and then just start the app as normal

If Schema Registry is unreachable, verify compose is running and check:
- `docker compose ps`
- `docker compose logs schema-registry`

##### start docker daemon if it is not already running
start a linux terminal and run\
`sudo dockerd`

##### stop running containers
check if a kafka container was still running with\
`sudo docker container ls` or `sudo docker ps`

if there was already a kafka container running, stop it first with\
`docker container stop kafka`

check if a kafka container was present but not running with\
`docker container ls -a` or `docker ps -a`

and remove the entire container with\
`docker container rm kafka`