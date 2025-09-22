# my-postgres-db

이 프로젝트는 Docker와 PostgreSQL을 사용하여 개발 환경에서 간편하게 데이터베이스를 구축할 수 있도록 구성되어 있습니다.

## 구성 파일

- `docker-compose.yml` : PostgreSQL 컨테이너 실행 설정 파일
- `init.sql` : 컨테이너 최초 실행 시 자동으로 실행되는 테이블 생성 및 초기 데이터 입력 SQL

## Java(Spring Boot) 프로젝트 터미널 빌드 방법

1. **프로젝트 루트 폴더로 이동**
   ```
   cd c:\hangangactivity
   ```

2. **Gradle 빌드 (권장)**
   ```
   ./gradlew build -x test
   ```
   

4. **실행**
   빌드가 완료되면 아래 명령어로 실행할 수 있습니다.
   ```
   java -jar build/libs/프로젝트이름-버전.jar
   ```
   (실제 jar 파일명은 `build/libs` 폴더에서 확인)


## 실행 방법

1. **Docker Desktop 설치**
   - [Docker 공식 사이트](https://www.docker.com/products/docker-desktop/)에서 설치 

2. **프로젝트 폴더로 이동**
   ```
   cd c:\hangangactivity\my-postgres-db
   ```

3. **컨테이너 실행**
   ```
   docker compose up -d
   ```

4. **DB 접속 정보**
   - 호스트: `localhost`
   - 포트: `5432`
   - 데이터베이스: `hangangdb`
   - 사용자: `hanganguser`
   - 비밀번호: `hangangpass`

5. **컨테이너 중지**
   ```
   docker compose down
   ```

## 데이터베이스 구조

- **users**: 사용자 정보 테이블
- **posts**: 게시글 정보 테이블

자세한 테이블 구조와 초기 데이터는 `init.sql