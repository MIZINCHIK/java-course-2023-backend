FROM openjdk:21-jdk
MAINTAINER github.com/MIZINCHIK
COPY target/bot.jar bot.jar
COPY entrypoint.sh .
ENTRYPOINT ["java","-jar","/bot.jar"]
