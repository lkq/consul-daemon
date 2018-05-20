FROM java:8u111-jre-alpine

ENV LANG C.UTF-8

COPY ${artifactName} /app/${artifactName}
WORKDIR /app
CMD ["java", "-jar", "/app/${artifactName}", "${registerURL}"]
