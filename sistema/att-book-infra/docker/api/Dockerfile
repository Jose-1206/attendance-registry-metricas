FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY .mvn ./.mvn
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src ./src

RUN ./mvnw -B -DskipTests package \
    && cp target/att-book-api-*.jar /app/api.jar

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S app \
    && adduser -S app -G app

COPY --from=build --chown=app:app /app/api.jar ./api.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/api.jar"]