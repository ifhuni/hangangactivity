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
- **클린 빌드**  
  ```sh
  ./gradlew clean build --refresh-dependencies 
### (2) Docker 명령어어
도커(DOcker)가 실행중인지 확인합니다.
- **실행중인지 확인**  
  ```sh
  docker ps -a
- **이미지(images) 및 컨테이너(Containers)생성**  
  ```sh
  docker-compose up -d
- **컨테이너(Containers) 삭제**  
  ```sh
  docker rm [컨테이너 ID 또는 이름]
- **이미지(Images) 삭제**  
  ```sh
  docker rmi [컨테이너 ID 또는 이름] 
