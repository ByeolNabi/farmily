# Python 3.9 Slim 버전을 베이스 이미지로 사용
FROM python:3.9-slim

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 코드 복사
COPY . .

# 기본 실행 명령어 (docker-compose에서 덮어씌워짐)
CMD ["python", "-u", "jetson_lidar-pub.py"]
