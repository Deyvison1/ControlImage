# =========================
# Etapa 2 - Runtime
# =========================
FROM eclipse-temurin:21-jdk

WORKDIR /app

ENV TZ=America/Sao_Paulo
ENV SPRING_PROFILES_ACTIVE=docker

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]