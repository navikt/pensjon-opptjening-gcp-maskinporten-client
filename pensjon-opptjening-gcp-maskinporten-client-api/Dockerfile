FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY pensjon-opptjening-gcp-maskinporten-client-api/build/libs/pensjon-opptjening-gcp-maskinporten-client-api.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
