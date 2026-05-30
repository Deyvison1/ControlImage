# =========================
# Etapa 1 - Build
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B


# =========================
# Etapa 2 - Runtime
# =========================
FROM eclipse-temurin:21-jdk

WORKDIR /app

ENV TZ=America/Sao_Paulo

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]