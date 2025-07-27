FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN ./gradlew clean build -x test --no-daemon

FROM openjdk:17-jdk-slim

# netcat설치 (wait-for-db.sh에서 nc -z 명령어 사용위해)
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# wait-for-it.sh 복사 및 실행 권한 추가
COPY ./scripts/wait-for-db.sh /wait-for-db.sh
RUN chmod +x /wait-for-db.sh

# 빌드한 jar 복사
COPY --from=builder /app/build/libs/oauth2-0.0.1-SNAPSHOT.jar app.jar

# ENTRYPOINT에서 wait-for-it.sh를 사용해 meetnow-db가 준비될 때까지 대기 후 실행
ENTRYPOINT ["/wait-for-db.sh", "meetnow-db", "3306", "--", "java", "-jar", "/app.jar"]
