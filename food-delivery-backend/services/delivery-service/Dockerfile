# Dockerfile mẫu áp dụng chung cho mọi Service (Identity, Delivery, Gateway...)
FROM eclipse-temurin:21-jre-alpine

# Cài đặt curl để hỗ trợ lệnh healthcheck của docker-compose
RUN apk add --no-cache curl

WORKDIR /app

# Chỉ copy file JAR đã được build từ máy local vào (vị trí target/)
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]