# AIAI (Spring Boot + MariaDB + Keycloak)

Dify/OpenWebUI 스타일의 핵심 기능(워크스페이스, 지식기반, 챗봇 생성, AI 연동)을 Spring Boot 기반으로 구현한 백엔드 예시입니다.

## 포함 기능
- Keycloak SSO 인증(JWT Resource Server)
- 워크스페이스 생성/조회
- 지식 문서 저장 + 임베딩 생성/저장(MariaDB)
- 챗봇 생성/조회(모델/시스템 프롬프트 별도 설정)
- RAG 유사도 검색 후 답변 생성

## 제외 사항 (요청 반영)
- Dify Workflow 기능
- 플러그인/외부 툴 연동 생태계

## 빠른 시작
1. 인프라 실행
```bash
docker compose up -d
```
2. Keycloak 설정
   - realm: `aiai`
   - client 생성 후 Access Token 발급
3. 애플리케이션 실행
```bash
export OPENAI_API_KEY=sk-...
./mvnw spring-boot:run
```

## API 요약
- `POST /api/workspaces`
- `GET /api/workspaces`
- `POST /api/workspaces/{workspaceId}/documents`
- `GET /api/workspaces/{workspaceId}/documents`
- `POST /api/workspaces/{workspaceId}/chatbots`
- `GET /api/workspaces/{workspaceId}/chatbots`
- `POST /api/chats`

모든 API는 Keycloak JWT Bearer Token 필요.

## 데이터 설계
- `workspaces`: 사용자 단위 작업공간
- `knowledge_documents`: 문서 원문
- `embedding_chunks`: 문서 청크 + 임베딩 벡터(JSON)
- `chatbots`: 워크스페이스 단위 챗봇
- `chat_sessions`, `chat_messages`: 대화 이력
