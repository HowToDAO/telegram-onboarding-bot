FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY onboarding-bot.jar /app

COPY database/demodb.mv.db database/

COPY frequently_asked_questions.json /app

COPY telegram.conf /app

COPY logback.xml /app

ENTRYPOINT java -jar onboarding-bot.jar