# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/#build-image)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/3.3.1/reference/htmlsingle/index.html#messaging.kafka)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.1/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## kafka in docker
start up docker
`sudo service docker start`

run de docker-compose file
`sudo docker-compose up`

#### start the app
in your IntelliJ terminal run
'mvn clean install'

and after that
`mvn spring-boot:run`

#### start a new Ubuntu terminal
list the containers currently running
`sudo docker container ls`

get the kafka container name (in this example "kafka" and run
`sudo docker container exec -it kafka /bin/bash`

to get into that containers shell, then run the topic (in this example "order-created")

`kafka-console-producer --broker-list localhost:9092 --topic order-created`

to get into the producer console there add new Json OrderCreated messages

`> {"orderId":"19f50a01-29dc-4a5f-b5f7-f585c9b518fa","item":"item-1"}`

in your app-log check to see that the message has been received.