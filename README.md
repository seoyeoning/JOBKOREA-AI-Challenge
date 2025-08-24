# AI Challenge - 맞춤형 면접 질문 생성 및 학습 경로 추천 API

## 프로젝트 개요

구직자의 이력서 내용(경력, 직무, 기술 스킬)을 기반으로 생성형 AI가 맞춤형 면접 모의질문을 생성하고, 자기 개발 학습 경로를 제안하여 구직자의 합격률을 높이는 데 도움을 주는 백엔드 챗봇 API입니다.

## 주요 기능

### 1. 이력서 핵심 정보 입력
- 경력 요약, 수행 직무, 보유 기술 스킬 리스트 등 이력서의 핵심 정보를 텍스트 형태로 입력
- 예시: "3년차 백엔드 개발자, Spring Boot/MSA/Python 기반 커머스 서비스 개발, AWS EC2 운영 경험"

### 2. 생성형 AI 기반 맞춤 정보 제공
- **맞춤 면접 모의 질문 생성**: 입력된 경력, 직무, 기술 스킬에 기반하여 실제 면접에서 나올 법한 심층적인 질문 5개 생성
- **자기 개발 및 합격률 향상 학습 경로 추천**: 개인 맞춤형 학습 경로 제안 (기술 스택 심화, 프로젝트 경험 쌓기, 커뮤니케이션 스킬 강화 등)

## 기술 스택

- **Backend**: Spring Boot 3.5.5, Java 21
- **AI Service**: OpenAI GPT-3.5-turbo
- **Build Tool**: Gradle
- **Testing**: JUnit 5, MockMvc
- **Configuration**: YAML

## 설치 및 실행

### 1. 사전 요구사항
- Java 21 이상
- Gradle 8.0 이상
- OpenAI API 키

### 2. 환경 변수 설정
```bash
export OPENAI_API_KEY="your-openai-api-key-here"
```

### 3. 프로젝트 빌드 및 실행
```bash
# 프로젝트 클론
git clone <repository-url>
cd AI_Challenge

# 의존성 설치 및 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 4. 접속 확인
- 애플리케이션: http://localhost:8080
- API 헬스 체크: http://localhost:8080/api/v1/ai-challenge/health
- Actuator: http://localhost:8080/actuator/health

## API 사용법

### 1. 면접 질문 생성 API

**Endpoint**: `POST /api/v1/ai-challenge/interview-questions`

**Request Body**:
```json
{
  "careerSummary": "3년차 백엔드 개발자",
  "jobDescription": "Spring Boot/MSA/Python 기반 커머스 서비스 개발",
  "technicalSkills": "Java, Spring Boot, MSA, Python, AWS EC2",
  "additionalInfo": "AWS EC2 운영 경험 보유"
}
```

**Response**:
```json
{
  "questions": [
    {
      "question": "Spring Boot에서 가장 어려웠던 부분은 무엇이고, 어떻게 해결했나요?",
      "category": "기술적 역량",
      "difficulty": "중급",
      "expectedAnswer": "문제 상황, 해결 과정, 학습한 점을 구체적으로 설명",
      "tips": "STAR 방법론을 활용하여 상황, 태스크, 액션, 결과 순서로 답변하세요"
    }
  ],
  "analysis": "기술 역량에 대한 질문들을 생성했습니다."
}
```

### 2. 학습 경로 추천 API

**Endpoint**: `POST /api/v1/ai-challenge/learning-path`

**Request Body**: (면접 질문 생성과 동일)

**Response**:
```json
{
  "learningSteps": [
    {
      "step": "Spring Boot 심화 학습",
      "description": "Spring Boot의 고급 기능과 최신 버전의 새로운 기능들을 학습",
      "priority": "높음",
      "resources": "공식 문서, 온라인 강의, 실습 프로젝트"
    }
  ],
  "summary": "현재 백엔드 개발자로서 기본적인 역량은 갖추고 있습니다.",
  "estimatedDuration": "6-8개월"
}
```

### 3. 통합 분석 API

**Endpoint**: `POST /api/v1/ai-challenge/comprehensive`

면접 질문과 학습 경로를 한 번에 생성합니다.

**Response**:
```json
{
  "interviewQuestions": { /* 면접 질문 응답 */ },
  "learningPath": { /* 학습 경로 응답 */ }
}
```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/JOBKOREA/AI_Challenge/
│   │   ├── AiChallengeApplication.java          # 메인 애플리케이션
│   │   ├── config/
│   │   │   └── OpenAIConfig.java               # OpenAI 설정 (타임아웃 포함)
│   │   ├── controller/
│   │   │   └── AIChallengeController.java      # API 컨트롤러
│   │   ├── dto/
│   │   │   ├── ResumeRequestDto.java           # 이력서 요청 DTO
│   │   │   ├── InterviewQuestionsResponseDto.java  # 면접 질문 응답 DTO
│   │   │   └── LearningPathResponseDto.java    # 학습 경로 응답 DTO
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java     # 전역 예외 처리
│   │   └── service/
│   │       └── OpenAIService.java              # OpenAI 서비스
│   └── resources/
│       └── application.yaml                     # 애플리케이션 설정
└── test/
    └── java/JOBKOREA/AI_Challenge/
        └── AIChallengeControllerTest.java       # 컨트롤러 테스트
```

## 주요 특징

### 1. **데이터베이스 없는 설계**
- 이력서 정보를 DB에 저장하지 않고 OpenAI API로만 처리
- 단순하고 효율적인 구조로 유지보수성 향상
- 불필요한 복잡성 제거

### 2. **강화된 예외 처리**
- `GlobalExceptionHandler`를 통한 중앙화된 예외 처리
- 사용자 친화적인 에러 메시지 제공
- 타임아웃, JSON 파싱 오류 등 구체적인 예외 상황 대응

### 3. **최적화된 OpenAI 통합**
- 명시적 타임아웃 설정 (120초)으로 안정성 향상
- 프롬프트 엔지니어링을 통한 일관된 JSON 응답 생성
- 응답 길이 제한 (3000자)으로 API 비용 최적화

### 4. **현대적인 Spring Boot 3.5.5**
- Java 21 기반으로 최신 언어 기능 활용
- JUnit 5 기반 테스트 환경
- YAML 기반 설정 관리

## 평가 포인트

### 1. 생성형 AI 활용의 창의성 및 정교함
- **LLM 프롬프트 엔지니어링 전략**: 구조화된 프롬프트를 통한 일관된 응답 생성
- **개인 맞춤형 특징**: 경력과 기술 스킬에 맞는 구체적이고 심층적인 질문 생성
- **응답 품질 관리**: JSON 형식 검증, 응답 길이 제한 등으로 안정성 확보

### 2. 백엔드 아키텍처 및 구현
- **API 설계의 견고함과 확장성**: RESTful API 설계, 적절한 HTTP 상태 코드 사용
- **효율적인 데이터 처리**: DTO를 통한 데이터 검증, 예외 처리
- **코드 품질**: 모듈화된 구조, 테스트 코드 작성, 불필요한 의존성 제거

### 3. 기능의 유용성 및 실용성
- **실제 면접 도움**: 카테고리별, 난이도별 질문과 답변 팁 제공
- **구체적이고 현실적인 가이드**: 우선순위, 예상 소요 시간, 구체적 행동 방안 제시
- **안정적인 서비스**: 타임아웃 처리, 에러 복구 등으로 사용자 경험 향상

## 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests AIChallengeControllerTest

# 빌드 및 테스트
./gradlew clean build
```

## 설정

### OpenAI API 설정
`application.yaml`에서 OpenAI API 키와 모델 설정을 관리합니다:

```yaml
openai:
  api:
    key: your-api-key-here
    model: gpt-3.5-turbo
    max-tokens: 3000
    temperature: 0.7
```

### 타임아웃 설정
OpenAI API 호출에 대한 타임아웃을 120초로 설정하여 안정성을 확보했습니다.

## 주의사항

1. **OpenAI API 키**: 실제 사용 시 환경 변수로 API 키를 설정해야 합니다.
2. **API 사용량**: OpenAI API 사용량과 비용을 고려하여 사용하세요.
3. **데이터 보안**: 실제 운영 환경에서는 민감한 정보가 포함되지 않도록 주의하세요.
4. **응답 길이**: API 응답은 3000자 이하로 제한되어 있습니다.

## 문제 해결

### 자주 발생하는 문제들

1. **타임아웃 오류**: OpenAI API 응답 시간이 길어질 수 있습니다. 잠시 후 재시도해주세요.
2. **JSON 파싱 오류**: AI 응답이 불완전할 수 있습니다. 재시도해주세요.
3. **포트 충돌**: 8080 포트가 이미 사용 중인 경우 `pkill -f "gradle.*bootRun"`으로 해결할 수 있습니다.

## 라이선스

이 프로젝트는 JOBKOREA AI Challenge를 위해 개발되었습니다.
