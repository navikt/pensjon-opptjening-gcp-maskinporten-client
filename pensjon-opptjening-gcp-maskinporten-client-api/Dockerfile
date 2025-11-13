FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y \
  curl \
  dumb-init \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY pensjon-opptjening-gcp-maskinporten-client-api/java-opts.sh /app
RUN chmod +x /app/java-opts.sh

COPY pensjon-opptjening-gcp-maskinporten-client-api/build/libs/pensjon-opptjening-gcp-maskinporten-client-api.jar /app/app.jar

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["bash", "-c", "source java-opts.sh && exec java ${DEFAULT_JVM_OPTS} ${JAVA_OPTS} -jar app.jar $@"]
