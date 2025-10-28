# Docker 네트워크 오류 해결 가이드

## 문제 상황

```
docker: Error response from daemon: failed to set up container networking: network mechuragi-network not found
```

배포 시 위와 같은 오류가 발생하여 Docker 컨테이너가 시작되지 않는 문제가 발생했습니다.

## 원인 분석

### 1. 문제 파일 확인

`.github/workflows/deploy.yml` 파일을 확인한 결과:

- **파일 위치**: `.github/workflows/deploy.yml:117`
- **문제 코드**:
  ```yaml
  sudo docker run -d \
    -p 8082:8082 \
    --name mechuragi-ai-service \
    --restart unless-stopped \
    --network mechuragi-network \  # 이 네트워크가 존재하지 않음
    -e "SPRING_PROFILES_ACTIVE=dev" \
    ...
  ```

### 2. 근본 원인

Docker 컨테이너 실행 시 `--network mechuragi-network` 옵션을 사용하고 있으나, EC2 서버에 해당 네트워크가 생성되지 않은 상태에서 컨테이너를 실행하려고 시도했기 때문입니다.

Docker 네트워크는 자동으로 생성되지 않으며, 명시적으로 생성하거나 docker-compose를 사용할 때 자동 생성됩니다.

## 해결 방법

### 방법 1: 배포 스크립트에 네트워크 생성 로직 추가 (권장)

`.github/workflows/deploy.yml` 파일의 deploy 스크립트에 네트워크 생성 로직을 추가했습니다.

**수정 전**:
```yaml
script: |
  # Pull latest image
  sudo docker pull ${{ secrets.DOCKERHUB_USERNAME }}/mechuragi-ai-service:latest
```

**수정 후**:
```yaml
script: |
  # Create Docker network if it doesn't exist
  echo "Ensuring Docker network exists..."
  sudo docker network inspect mechuragi-network >/dev/null 2>&1 || \
    sudo docker network create mechuragi-network

  # Pull latest image
  sudo docker pull ${{ secrets.DOCKERHUB_USERNAME }}/mechuragi-ai-service:latest
```

### 방법 2: 수동으로 EC2 서버에서 네트워크 생성

만약 즉시 해결이 필요한 경우, EC2 서버에 SSH로 접속하여 수동으로 네트워크를 생성할 수 있습니다:

```bash
# EC2 서버 접속 후
sudo docker network create mechuragi-network

# 네트워크 확인
sudo docker network ls

# 네트워크 상세 정보 확인
sudo docker network inspect mechuragi-network
```

## 적용 방법

### 1. 코드 변경사항 커밋 및 푸시

```bash
git add .github/workflows/deploy.yml
git commit -m "fix: Docker 네트워크 자동 생성 로직 추가"
git push origin <브랜치명>
```

### 2. 배포 확인

- GitHub Actions에서 배포가 정상적으로 완료되는지 확인
- Discord 알림을 통해 배포 성공 여부 확인

## 예방 조치

이 수정으로 인해:

1. **자동 복구**: 네트워크가 없으면 자동으로 생성되므로 수동 개입이 필요 없습니다.
2. **멱등성**: `docker network inspect`로 먼저 확인하고 없을 때만 생성하므로, 여러 번 실행해도 안전합니다.
3. **에러 방지**: 향후 새로운 서버에 배포하거나 네트워크가 삭제된 경우에도 자동으로 복구됩니다.

## 참고: Docker 네트워크란?

Docker 네트워크는 컨테이너 간 통신을 가능하게 하는 격리된 네트워크 환경입니다.

### 주요 사용 사례:

- 여러 컨테이너가 같은 네트워크에 속하면 컨테이너 이름으로 서로 통신 가능
- 예: AI 서비스 컨테이너와 데이터베이스 컨테이너를 같은 네트워크에 배치
- 네트워크 격리를 통한 보안 강화

### 유용한 Docker 네트워크 명령어:

```bash
# 네트워크 목록 조회
docker network ls

# 네트워크 생성
docker network create <네트워크명>

# 네트워크 상세 정보
docker network inspect <네트워크명>

# 네트워크 삭제
docker network rm <네트워크명>

# 사용하지 않는 네트워크 일괄 삭제
docker network prune
```

## 문제 해결 완료

이 문서에 기록된 방법으로 Docker 네트워크 오류를 해결했습니다. 향후 같은 문제가 발생하지 않도록 자동화되었습니다.
