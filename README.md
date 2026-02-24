# Mechuragi AI Service

AWS Bedrock(Claude 3) 기반 개인화 음식 추천 마이크로서비스입니다. 날씨·시간·재료·감정·대화 등 5가지 컨텍스트에 따라 맞춤 음식을 추천하고, 결과를 메인 서버 DB에 비동기로 저장합니다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Framework | Spring Boot 3.5.5 (Java 17) |
| AI Platform | AWS Bedrock — Claude 3 Haiku |
| Cache | Redis (추천 결과 캐싱) |
| HTTP Client | Spring WebFlux WebClient |
| Container | Docker (멀티스테이지 빌드) |
| CI/CD | GitHub Actions → DockerHub → EC2 |
| Logging | CloudWatch Logs (`/aws/mechuragi/dev/ai-service`) |
| API Docs | SpringDoc OpenAPI (운영환경 비활성) |

> MySQL은 이 서버에서 직접 접근하지 않습니다. 추천 결과 저장은 메인 서버 API 호출로 위임합니다.

## 아키텍처 위치

```
CloudFront → OpenResty Gateway (Lua JWT 검증)
                      ↓
              AI 서버 :8082  ←────→  AWS Bedrock
                      ↓  (비동기)
              메인 서버 :8080/8081  ←───→  MySQL
```

## 추천 타입

| 타입 | 설명 | `context` 예시 |
|------|------|----------------|
| `WEATHER` | 현재 날씨 기반 | `["맑음", "따뜻함"]` |
| `TIME` | 식사 시간대 기반 | `["아침", "점심"]` |
| `INGREDIENTS` | 보유 재료 기반 | `["감자", "양파", "계란"]` |
| `FEELING` | 현재 감정/상태 기반 | `["피곤함", "스트레스"]` |
| `CONVERSATION` | 자유 대화형 | `["매콤하고 든든한 것"]` |

## API 엔드포인트

```
GET  /recommend/health
     → { "status": "UP", "service": "mechuragi-ai-service", "version": "1.0.2" }

POST /recommend/food
     Headers: Authorization: Bearer <JWT>
     Body:    FoodRecommendationRequest
     → FoodRecommendationResponse (추천 4건 + 사용 모델)
```

### 요청 예시

```json
{
  "type": "WEATHER",
  "context": ["맑음", "따뜻함"],
  "dietStatus": "normal",
  "veganOption": "no",
  "spiceLevel": "medium",
  "foodTypes": ["korean", "fusion"],
  "tastes": ["spicy", "savory"],
  "dislikedFoods": ["mushroom"]
}
```

### 응답 예시

```json
{
  "message": "날씨에 어울리는 맛있는 음식들을 추천해드렸습니다!",
  "recommendations": [
    {
      "recommendationType": "WEATHER",
      "name": "김치찌개",
      "description": "얼큰하고 칼칼한 한국 전통 찌개",
      "reason": "따뜻한 날씨에 어울리는 매콤한 음식",
      "ingredients": "김치, 두부, 돼지고기, 고춧가루",
      "cookingTime": "20분",
      "difficulty": "쉬움"
    }
  ],
  "model": "anthropic.claude-3-haiku-20240307-v1:0"
}
```

## 프로젝트 구조

```
src/main/java/com/mechuragi/ai/
├── controller/
│   └── RecommendationController.java   # GET /health, POST /food
├── service/
│   ├── RecommendationService.java      # 요청 조율, 비동기 저장
│   ├── BedrockService.java             # Bedrock 호출, Redis 캐싱
│   └── PromptTemplateService.java      # 타입별 프롬프트 생성
├── client/
│   └── MainServiceClient.java          # 메인 서버 저장 API 호출
├── config/
│   ├── AwsConfig.java                  # BedrockRuntimeClient 빈
│   ├── WebClientConfig.java            # WebClient, CORS 설정
│   ├── AsyncConfig.java                # @Async 활성화
│   └── SwaggerConfig.java             # OpenAPI (운영 비활성)
├── dto/
│   ├── frontend/                       # 프론트엔드 요청/응답 DTO
│   ├── bedrock/                        # Bedrock 프롬프트/응답 DTO
│   └── main/                           # 메인 서버 저장 요청 DTO
└── type/
    └── RecommendationType.java         # WEATHER, TIME, INGREDIENTS, FEELING, CONVERSATION
```

## 로컬 개발 환경

```bash
# .env 파일 설정 (spring-dotenv로 자동 로드)
cp .env.example .env
# .env에 AWS credentials, Redis, Nginx Gateway IP 등 입력

./gradlew bootRun
```

| 환경 변수 | 설명 | 기본값 |
|-----------|------|--------|
| `REDIS_HOST` | Redis 호스트 | `localhost` |
| `REDIS_PORT` | Redis 포트 | `6379` |
| `AWS_REGION` | AWS 리전 | `ap-northeast-2` |
| `BEDROCK_CHAT_MODEL` | Bedrock 모델 ID | `anthropic.claude-3-haiku-20240307-v1:0` |
| `NGINX_GATEWAY_PRIVATE_IP` | 메인 서버 라우팅용 Nginx IP | `localhost` |
| `CORS_ALLOWED_ORIGINS` | CORS 허용 Origin | `http://localhost:3000` |
| `LOG_LEVEL` | 로그 레벨 | `INFO` |

## 배포

GitHub Actions가 `main` / `dev` 브랜치 push 시 자동 실행됩니다.

```
[push] → 테스트 & 빌드 → DockerHub push → SSH(Bastion 경유) → EC2 배포
                                                                ↓
                                                   /recommend/health 헬스체크 (5회)
                                                                ↓
                                                   Discord 성공/실패 알림
```

**컨테이너 실행 방식**
- 포트: `8082`
- 로그 드라이버: `awslogs` → CloudWatch `/aws/mechuragi/dev/ai-service`
- 재시작 정책: `unless-stopped`
- 네트워크: `mechuragi-network` (메인 서버와 동일)

**수동 배포 (긴급 시)**
```bash
# Bastion(OpenResty)을 통해 AI 서버에 접속
ssh -J ubuntu@<GATEWAY_IP> ubuntu@<AI_SERVER_IP>

docker pull <DOCKERHUB_USER>/mechuragi-ai-service:latest
docker stop $(docker ps -q --filter "publish=8082")
docker run -d --restart unless-stopped -p 8082:8082 \
  --env-file /etc/mechuragi/ai.env \
  <DOCKERHUB_USER>/mechuragi-ai-service:latest
```

## 프로파일

| 프로파일 | 로그 레벨 | Swagger | 비고 |
|----------|----------|---------|------|
| `local` | DEBUG | 활성 | localhost 직접 접속 |
| `dev` | DEBUG | 활성 | EC2 개발 환경 |
| `production` | INFO | **비활성** | CloudWatch 통합 |
