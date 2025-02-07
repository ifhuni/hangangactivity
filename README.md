# 개발 환경 안내

## 1. 기술 스택
- JAVA 17  
- PostgreSQL  
- Spring Boot  
- Git  
- Docker  

## 2. 개발 환경
- **VS Code** : 소스 코드 편집 및 개발  
- **DBeaver** : PostgreSQL 데이터베이스 관리  
- **Docker** : 컨테이너 실행 및 관리  

## 3. 빌드 방법 및 명령어
### (1) 프로젝트 빌드
OS별 명령어 차이를 고려하세요.  
- **Windows**  
  ```sh
  .\gradlew build
- **macOS/Linux**  
  ```sh
  ./gradlew build
### (2) Docker Containers 생성
```sh
  docker-compose up -d
```
 docker ps