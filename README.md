# telegram-onboarding-bot

## Requirements:
 - JDK version 17 or higher

## Setup and run with Docker:
#### Requirements:
 - Docker
#### Requirements:
 - Copy files `frequently_asked_questions.json`, `telegram.conf`, `logback.xml` and `Dockerfile` in application directory
 - Copy build jar file from `build/libs/` to application directory, rename jar file to `telegram-onboarding-bot.jar`
 - Copy database directory with database file from `database/onboarding-bot.mv.db` to application directory
 - add **"bot_username"** and **"bot_token"** to `telegram.conf` file
 - build and run docker container: 
```sh
docker build -t bot-onboarding .
docker run bot-onboarding
```