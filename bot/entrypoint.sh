#!/bin/sh
export APP_TELEGRAM_TOKEN=$(cat /run/secrets/tg_api_key) ; java -jar /bot.jar
