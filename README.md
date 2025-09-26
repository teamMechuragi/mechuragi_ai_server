# Mechuragi AI Service

AWS Bedrock 기반 AI 추천 서비스

## 기능
- AI 기반 개인화 추천
- 데이터 분석 및 인사이트
- RESTful API 제공

## 기술 스택
- **Framework**: Spring Boot 3.5.5
- **Java**: OpenJDK 17
- **AI Platform**: AWS Bedrock
- **Database**: MySQL 8.0
- **Cache**: Redis
- **Container**: Docker
- **CI/CD**: GitHub Actions

## API 엔드포인트
- `GET /api/ai/health` - 서비스 상태 확인
- `POST /api/ai/recommend` - AI 추천 요청
- `POST /api/ai/analyze` - 데이터 분석 요청

## 배포
무중단 배포를 위한 Docker 기반 Blue-Green 배포 지원

## 개발 환경
```bash
./gradlew bootRun
```

## 운영 환경
Docker 컨테이너로 배포되며, GitHub Actions를 통한 자동 배포 지원