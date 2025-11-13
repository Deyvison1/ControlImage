# Imagem base (Java 21)
FROM eclipse-temurin:21-jdk

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR da aplicação para dentro do container
COPY target/image-api.jar app.jar

# Expõe a porta 8080
EXPOSE 8082

# Comando para iniciar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
