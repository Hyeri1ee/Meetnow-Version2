#!/bin/bash

# 8080 포트를 사용하는 프로세스를 찾아서 종료하는 스크립트

PORT=8080

echo "🔍 8080 포트를 사용하는 프로세스 확인 중..."

# 포트를 사용하는 프로세스 PID 찾기
PID=$(lsof -ti :$PORT)

if [ -z "$PID" ]; then
    echo "✅ 포트 $PORT는 사용 가능합니다."
    exit 0
fi

echo "⚠️  포트 $PORT를 사용하는 프로세스 발견: PID $PID"

# 프로세스 정보 확인
PROCESS_INFO=$(ps -p $PID -o pid,comm,args 2>/dev/null | tail -1)

if [ -z "$PROCESS_INFO" ]; then
    echo "❌ 프로세스 정보를 가져올 수 없습니다. 이미 종료되었을 수 있습니다."
    exit 0
fi

echo "프로세스 정보: $PROCESS_INFO"

# Java 프로세스인지 확인 (Spring Boot 애플리케이션)
if echo "$PROCESS_INFO" | grep -q "java"; then
    echo "🔧 Spring Boot 애플리케이션을 종료합니다..."
    kill $PID 2>/dev/null
    
    # 3초 대기 후 여전히 실행 중이면 강제 종료
    sleep 3
    if kill -0 $PID 2>/dev/null; then
        echo "⚠️  정상 종료되지 않아 강제 종료합니다..."
        kill -9 $PID 2>/dev/null
    fi
    
    echo "✅ 프로세스 종료 완료"
else
    echo "⚠️  Java 프로세스가 아닙니다: $PROCESS_INFO"
    echo "❓ 이 프로세스를 종료할까요? (y/n)"
    read -t 3 answer || answer="n"
    
    if [ "$answer" = "y" ]; then
        kill $PID 2>/dev/null || kill -9 $PID 2>/dev/null
        echo "✅ 프로세스 종료 완료"
    else
        echo "⏭️  프로세스를 종료하지 않았습니다."
        exit 1
    fi
fi

# 포트 해제 확인
sleep 1
if lsof -ti :$PORT > /dev/null 2>&1; then
    echo "❌ 포트 $PORT가 여전히 사용 중입니다."
    exit 1
else
    echo "✅ 포트 $PORT가 해제되었습니다."
    exit 0
fi

