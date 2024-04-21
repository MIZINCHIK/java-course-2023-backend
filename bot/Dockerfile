FROM openjdk:21-jdk
MAINTAINER github.com/MIZINCHIK
COPY target/bot.jar bot.jar
CMD ["/bin/sh", "-c", "export TEST=$$(cat /run/secrets/tg_api_key)"]
ENTRYPOINT ["java","-jar","/bot.jar"]
