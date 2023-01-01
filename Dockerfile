FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY telegram-onboarding-bot.jar /app

COPY database/onboarding-bot.mv.db database/

COPY frequently_asked_questions.json /app

COPY telegram.conf /app

COPY logback.xml /app

ENTRYPOINT java -jar telegram-onboarding-bot.jar