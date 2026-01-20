# =========================
# Etapa 1 - Build
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


# =========================
# Etapa 2 - Runtime
# =========================
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 👉 copia o certificado do Keycloak para dentro da imagem
COPY keycloak.crt /tmp/keycloak.crt

# 👉 importa o certificado no truststore do Java
RUN keytool -importcert \
    -noprompt \
    -alias keycloak \
    -file /tmp/keycloak.crt \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit

# 👉 copia o JAR gerado
COPY --from=builder /app/target/control-image-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
