# 使用 OpenJDK 作为基础镜像
FROM openjdk:8

# 设置工作目录
WORKDIR /cs1

# 将构建好的 JAR 文件复制到容器
COPY target/message-0.0.1-SNAPSHOT.jar /cs1/message.jar

# 暴露应用端口（默认 Spring Boot 使用 8080 端口）
EXPOSE 8084

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "message.jar"]