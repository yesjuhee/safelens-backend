## 프로젝트 개요

이 저장소는 SafeLens 백엔드 서비스 코드입니다.  
이미지 분석, 기록 관리, 회원 관리 기능을 제공하며, Docker 및 Docker Compose를 이용해 쉽게 배포할 수 있습니다.

---

## 배포 하기

### 1. 사전 준비

- **Docker 설치 필요**
    - Docker Desktop 또는 Docker Engine이 설치되어 있어야 합니다.
- **ARM 아키텍처 기반 환경**
    - 이 프로젝트는 **ARM 아키텍처 기반 Docker, Docker Compose** 환경을 기준으로 구성되어 있습니다.
- **.env 파일 작성**
    - 배포 환경을 위한 `.env` 파일을 작성해야 합니다.
    - `.env.example`을 참고해 작성할 수 있습니다.
    - `IMAGE_SERVER_URL`을 적절한 값으로 변경해주세요.
    - `JWT_SECRET`, `MYSQL_ROOT_PASSWORD`, `SPRING_DATASORUCE_PASSWORD`를 안전한 값으로 변경해주세요
    - `MYSQL_ROOT_PASSWORD`와 `SPRING_DATASOURCE_PASSWORD`는 같은 값으로 작성해주세요.

### 2. 배포 절차

1. **루트 디렉토리로 이동**

    ```bash
    cd backend
    ```

2. **컨테이너 실행**

    ```bash
    docker compose up -d
    ```

3. **컨테이너 및 서비스 확인**
   ```bash
    docker ps
    ```

   **컨테이너 확인**
    - `safelens-app`
    - `safelens-db`

   위 두 개의 컨테이너가 정상적으로 실행 중인지 확인합니다.

배포 후 `http://localhost:8080` 으로 접속 가능합니다.

---

## API 문서

- **간단한 API 개요**
    - 루트 디렉토리의 `API_QUICK.md` 파일을 통해 주요 API를 빠르게 확인할 수 있습니다.

- **Swagger UI를 통한 전체 API 문서**
    - 애플리케이션 배포 후 브라우저에서 다음 주소로 접속하면 Swagger UI 기반의 API 문서를 확인할 수 있습니다.
    - `http://localhost:8080/swagger-ui/index.html`
