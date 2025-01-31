# 1. Java 기반의 가벼운 이미지를 선택
FROM openjdk:17-jdk-slim

# 2. JAR 파일을 컨테이너 내부로 복사
WORKDIR /app
COPY build/libs/hangangactivity-0.0.1-SNAPSHOT.jar app.jar

# 3. 실행 명령어
CMD ["java", "-jar", "app.jar"]
