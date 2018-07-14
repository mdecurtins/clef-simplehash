FROM openjdk:8-jdk-alpine

RUN apk update && apk add libstdc++ sqlite

RUN mkdir -p /usr/local/simplehash /usr/local/data /usr/local/tmp

COPY ./simplehash/target/simplehash-0.1.jar /usr/local/simplehash/simplehash-0.1.jar
COPY ./xml2hum /usr/local/simplehash/xml2hum
COPY ./data /usr/local/data

RUN touch /usr/local/tmp/log.txt
RUN touch /usr/local/data/ngrams.csv

ENV DB_PATH="/usr/local/data/simplehash.db"
ENV QUERY_SIZE_MIN="3"
ENV QUERY_SIZE_MAX="15"

WORKDIR /usr/local/simplehash

CMD java -Djava.security.egd=file:/dev/./urandom -jar ./simplehash-0.1.jar