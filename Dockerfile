# Étape 1: Construire l'application avec Maven
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2: Créer une image légère pour l'exécution
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Démarrage dynamique sur le port fourni par Cloud Run
#ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
ENV PORT 8080
ENTRYPOINT ["java", "-jar", "app.jar"]