FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd --system --gid 10001 appgroup \
    && useradd --system \
       --uid 10001 \
       --gid appgroup \
       --home-dir /app \
       --shell /usr/sbin/nologin \
       appuser

COPY --from=build \
     --chown=10001:10001 \
     /workspace/target/demo-policy-service-1.0.0.jar \
     app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=dev
ENV APP_NAME=demo-policy-service

USER 10001:10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]