FROM openjdk:21-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré
COPY target/station-market-api-0.0.1-SNAPSHOT.jar backend-app.jar

# Exposer le port utilisé par l'application
EXPOSE 5000

# Commande pour exécuter l'application
CMD ["java", "-jar", "backend-app.jar"]