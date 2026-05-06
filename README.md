# Weather App Kafka

weather app based off youtube series on Kafka from the Quix channel https://www.youtube.com/watch?v=D2NYvGlbK0M

# Startup

### start docker-compose
start a new linux terminal and run\
`sudo docker compose up`

### start the app
run the WeatherAppKafkaApplication class in your IDE
you can open a terminal to http://localhost:3040 to get a UI for kafka\
from there look up the topic 'weather_input' and see the messages coming in.

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

sometimes kafka will not start up properly because some of the resources are already in use.\
what helps me is just do a 
`docker system prune` or even `docker system prune -a`

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