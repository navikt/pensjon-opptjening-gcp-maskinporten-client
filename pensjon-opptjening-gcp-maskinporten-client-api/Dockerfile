FROM ghcr.io/navikt/baseimages/temurin:21

COPY build/libs/pensjon-opptjening-gcp-maskinporten-client-api.jar /app/app.jar

ENV JAVA_OPTS=-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/
