FROM ghcr.io/navikt/baseimages/temurin:21

COPY pensjon-opptjening-gcp-maskinporten-client-api/build/libs/pensjon-opptjening-gcp-maskinporten-client-api.jar /app/app.jar

ENV JAVA_OPTS=-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/
