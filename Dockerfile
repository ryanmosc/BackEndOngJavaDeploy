# Etapa 1: Build (compilar o projeto)
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas os arquivos de configuração primeiro (para cache eficiente)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Baixa dependências (cache das libs)
RUN ./mvnw dependency:go-offline

# Copia o código fonte
COPY src src

# Compila o projeto e gera o .jar
RUN ./mvnw clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o .jar gerado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
