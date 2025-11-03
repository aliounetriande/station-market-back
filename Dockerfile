FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/station-market-api-0.0.1-SNAPSHOT.jar backend-app.jar
EXPOSE 5000
CMD ["java", "-jar", "backend-app.jar"]