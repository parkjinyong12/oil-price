#!/bin/bash

# Git 변경사항 커밋 및 푸시
git add .
git commit -m "$1"
git push origin master

# 커밋이 성공적으로 됐을 때만 jib 태스크 실행
if [ $? -eq 0 ]; then
    echo "Git push 성공, Docker 이미지 빌드 및 푸시 시작..."
    ./gradlew jib
else
    echo "Git push 실패, Docker 이미지 빌드 건너뜀"
    exit 1
fi 