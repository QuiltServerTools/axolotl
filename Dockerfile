FROM openjdk:16-jdk-slim

COPY build/libs/bot-*-all.jar /usr/local/lib/bot.jar

RUN mkdir /bot
WORKDIR /bot

COPY tags /bot/tags

ENTRYPOINT [ "java", "-Xms1G", "-Xmx1G", "-jar", "/usr/local/lib/bot.jar" ]
