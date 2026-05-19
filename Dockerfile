# Etapa de construcción (Build stage)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias
# Esto ayuda a cachear las dependencias si no hay cambios en el pom.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente y compilamos el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución (Run stage)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto de la aplicación
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
