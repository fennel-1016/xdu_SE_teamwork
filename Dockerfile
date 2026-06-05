# 饭电后端服务 Dockerfile
# 构建方式：
#   docker build -t fandian-backend:1.0.0 .
# 运行方式：
#   docker run -p 8080:8080 --env-file backend/.env.example fandian-backend:1.0.0

FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace/backend

# 先复制 pom.xml 以便 Docker 缓存 Maven 依赖层。
COPY docker/maven-settings.xml /root/.m2/settings.xml
COPY backend/pom.xml .
RUN mvn -DskipTests dependency:go-offline

COPY backend/src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS=""

COPY --from=build /workspace/backend/target/fandian-backend-1.0.0.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
