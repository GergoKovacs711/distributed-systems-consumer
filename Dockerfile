FROM openjdk:8
ADD target/restful-consumer.jar restful-consumer.jar
ENTRYPOINT ["java", "-jar", "restful-consumer.jar"]