FROM gcr.io/distroless/java21-debian13:nonroot

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY pensjon-opptjening-gcp-maskinporten-client-api/build/libs/pensjon-opptjening-gcp-maskinporten-client-api.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
