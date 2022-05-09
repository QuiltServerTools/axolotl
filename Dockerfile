FROM openjdk:16-jdk-slim

COPY build/libs/axolotl-*-all.jar /usr/local/lib/bot.jar

RUN mkdir /bot
WORKDIR /bot

ENTRYPOINT [ "java", "-Xms1G", "-Xmx1G", "-jar", "/usr/local/lib/bot.jar" ]
