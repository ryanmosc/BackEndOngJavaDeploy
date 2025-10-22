# Etapa 1: Build (compilar o projeto)
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia arquivos principais
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Dá permissão de execução ao wrapper Maven
RUN chmod +x mvnw

# Baixa dependências (cache)
RUN ./mvnw dependency:go-offline

# Copia o código fonte
COPY src src

# Compila e gera o .jar
RUN ./mvnw clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o .jar gerado da etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
