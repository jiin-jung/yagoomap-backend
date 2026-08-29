# 야구맵 - 야구 틀어주는 술집 지도

<div align="center">
  <h1>⚾ 야구맵 - 오늘 야구, 어디서 볼까?</h1>
  <p>야구 경기를 함께 즐길 수 있는 장소를 찾는 지도 기반 웹 서비스</p>
</div>

<br/>

<div align="center">
  <img src="https://raw.githubusercontent.com/jiin-jung/yagoomap-backend/main/assets/yagoomap-main.jpg" alt="야구맵 메인 화면" width="100%"/>
</div>

<br/>

<div align="center">
  <a href="https://야구맵.kr">홈페이지</a>
</div>

---

## ✍️ 프로젝트 개요

- **프로젝트명:** 야구맵
- **프로젝트 형태:** WEB 서비스
- **서비스 상태:** 운영 중
- **목표:** 야구 경기를 시청할 수 있는 장소와 KBO 경기 정보를 한곳에서 제공하는 지도 기반 서비스 구축
- **주요 타겟 사용자:**
  - 외부에서 KBO 경기를 시청할 장소를 찾는 사용자
  - 응원하는 팀의 경기를 함께 볼 수 있는 장소를 찾는 사용자
  - 실제 방문 경험을 바탕으로 장소 정보를 확인하고 공유하고 싶은 사용자

---

## ✍️ 프로젝트 소개

### 프로젝트 배경

야구 팬이 외부에서 경기를 보고 싶을 때 특정 식당이나 술집에서 실제로 야구 중계를 틀어주는지 확인하기는 쉽지 않습니다.

지도 서비스에서 음식점이나 술집 자체를 찾는 것은 가능하지만, **야구 중계 여부**, **응원 가능한 팀**, **실제 방문자의 후기**와 같은 정보는 별도로 확인해야 합니다.

야구맵은 이러한 불편을 해결하기 위해 야구 경기를 시청할 수 있는 장소를 지도 기반으로 제공하고, KBO 경기 일정과 장소별 리뷰·제보 정보를 함께 확인할 수 있도록 구성한 서비스입니다.

### 사용자 니즈

⚾ **경기 정보**

- 오늘 어떤 KBO 경기가 열리는지 빠르게 확인하고 싶음
- 특정 날짜의 경기 일정을 한눈에 확인하고 싶음
- 응원하는 팀의 경기와 함께 장소 정보를 확인하고 싶음

📍 **야구 시청 장소 탐색**

- 실제로 야구를 틀어주는 식당이나 술집을 찾고 싶음
- 팀·지역·검색어를 기준으로 장소를 빠르게 필터링하고 싶음
- 지도와 함께 장소 정보를 직관적으로 확인하고 싶음

⭐ **신뢰할 수 있는 장소 정보**

- 실제 방문자의 리뷰를 확인하고 싶음
- 잘못되거나 새롭게 발견한 장소 정보를 직접 제보하고 싶음
- 사용자 제보와 외부 장소 데이터를 운영자가 검수한 뒤 제공받고 싶음

---

## 🚀 프로젝트 목표

1. **야구 경기를 시청할 수 있는 장소를 지도 기반으로 쉽게 탐색할 수 있는 환경 제공**

2. **KBO 경기 일정과 장소 정보를 연결하여 경기 관람 경험 개선**

3. **리뷰와 사용자 제보를 활용해 장소 정보의 정확성과 신뢰도 향상**

4. **외부 장소 API와 관리자 검수 구조를 활용한 장소 데이터 확장 및 운영 효율화**

5. **Redis 캐시와 모니터링 환경을 활용한 안정적인 서비스 운영**

---

## ✨ 주요 기능

### 1. 야구 시청 장소 탐색

- 야구 시청 가능 장소 목록 조회
- 장소 상세 정보 조회
- 팀별 장소 조회
- 팀·지역·검색어 기반 필터링
- 장소 등록·수정·삭제
- 서비스 내 장소 필터 정보 제공

<br/>

### 2. KBO 경기 일정 제공

- 특정 날짜의 KBO 경기 조회
- 오늘 경기 조회
- 외부 경기 일정 데이터 동기화
- 스케줄러 기반 경기 데이터 자동 갱신
- 관리자용 경기 데이터 수동 동기화

<br/>

### 3. 장소 리뷰

- 장소별 리뷰 등록
- 장소별 리뷰 목록 조회
- 리뷰 삭제
- 장소별 리뷰 요약 정보 제공

<br/>

### 4. 장소 제보 및 관리자 검수

- 사용자 장소 제보 등록
- 관리자 제보 목록 및 상세 조회
- 제보 승인·반려
- 크롤링 및 외부 수집 후보 장소 등록
- 후보 장소 일괄 등록
- 관리자 승인 후 실제 장소 데이터로 반영
- 관리자 장소 목록 및 대시보드 제공

<br/>

### 5. 외부 장소 및 이미지 데이터 연동

- Kakao Local API 기반 장소 검색
- Kakao 장소 검색 결과를 검수 후보 데이터로 수집
- Naver Image API 기반 장소 이미지 검색
- 여러 장소 이미지 병렬 조회
- Redis 캐시를 활용한 이미지 API 호출 최적화

<br/>

### 6. 실시간 접속자 집계

- 클라이언트 세션 기반 heartbeat 처리
- Redis를 활용한 활성 사용자 상태 관리
- 현재 서비스 접속자 수 실시간 집계

<br/>

### 7. 사용자 행동 이벤트 및 운영 통계

- 사용자 행동 이벤트 비동기 수집
- 이벤트 요청 즉시 `202 Accepted` 반환
- IPv4 마지막 옥텟 및 IPv6 마지막 그룹 마스킹
- 최근 1~90일 기준 관리자 운영 통계 조회

<br/>

### 8. 서비스 공지 및 공공 데이터 연동

- 사용자용 공지 조회
- 관리자용 공지 내용 및 활성 상태 변경
- 인천 버스 도착 정보 API 프록시
- 기상청 단기예보 API 프록시

---

## ⚙️ 기술 스택

<table>
  <thead>
    <tr>
      <th>분류</th>
      <th>기술 스택</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Backend</td>
      <td>
        <img src="https://img.shields.io/badge/Java_21-007396?style=flat&logo=openjdk&logoColor=white"/>
        <img src="https://img.shields.io/badge/Spring_Boot_4.0.6-6DB33F?style=flat&logo=springboot&logoColor=white"/>
        <img src="https://img.shields.io/badge/Spring_MVC-6DB33F?style=flat&logo=spring&logoColor=white"/>
        <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring&logoColor=white"/>
      </td>
    </tr>
    <tr>
      <td>Database & Cache</td>
      <td>
        <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/>
        <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>
        <img src="https://img.shields.io/badge/H2-09476B?style=flat"/>
      </td>
    </tr>
    <tr>
      <td>External API</td>
      <td>
        <img src="https://img.shields.io/badge/Kakao_Local_API-FFCD00?style=flat&logo=kakao&logoColor=black"/>
        <img src="https://img.shields.io/badge/Naver_Image_API-03C75A?style=flat&logo=naver&logoColor=white"/>
        <img src="https://img.shields.io/badge/KBO_Data-1E3269?style=flat"/>
        <img src="https://img.shields.io/badge/공공데이터_API-005BAC?style=flat"/>
      </td>
    </tr>
    <tr>
      <td>Monitoring</td>
      <td>
        <img src="https://img.shields.io/badge/Spring_Actuator-6DB33F?style=flat&logo=springboot&logoColor=white"/>
        <img src="https://img.shields.io/badge/Micrometer-000000?style=flat"/>
        <img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white"/>
      </td>
    </tr>
    <tr>
      <td>Infra & CI/CD</td>
      <td>
        <img src="https://img.shields.io/badge/AWS_EC2-FF9900?style=flat&logo=amazonec2&logoColor=white"/>
        <img src="https://img.shields.io/badge/AWS_S3-569A31?style=flat&logo=amazons3&logoColor=white"/>
        <img src="https://img.shields.io/badge/AWS_SSM-FF9900?style=flat&logo=amazonaws&logoColor=white"/>
        <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white"/>
      </td>
    </tr>
    <tr>
      <td>Documentation & Test</td>
      <td>
        <img src="https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white"/>
        <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white"/>
      </td>
    </tr>
  </tbody>
</table>

---

## 📐 시스템 아키텍처

```text
Client
  │
  ▼
Spring Boot API
  ├── MySQL                 # 장소·리뷰·경기·이벤트 데이터
  ├── Redis                 # 이미지 캐시·활성 사용자 관리
  ├── Kakao Local API       # 장소 검색 및 후보 수집
  ├── Naver Image API       # 장소 이미지 검색
  ├── KBO Schedule Source   # 경기 일정 동기화
  └── Public Data API       # 버스 도착정보·기상청 단기예보

GitHub Actions
  │
  ├── Gradle Build
  ├── AWS S3 Artifact Upload
  └── AWS SSM → EC2 Deploy

Spring Boot Actuator
  └── Prometheus Metrics
```

---

## 🗄️ 주요 도메인 구조

```text
Game
 └── KBO 경기 일정 및 상태

Place
 ├── 야구 시청 장소
 ├── Review
 ├── Report
 └── CrawlCandidate

Presence
 └── 실시간 활성 사용자

Event
 └── 사용자 행동 로그 및 운영 통계

Notice
 └── 서비스 공지
```

---

## 🔎 주요 API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/games` | 날짜별 KBO 경기 조회 |
| GET | `/api/games/today` | 오늘의 KBO 경기 조회 |
| GET | `/api/places` | 장소 목록 및 필터 검색 |
| GET | `/api/places/{placeId}` | 장소 상세 조회 |
| GET | `/api/teams/{team}/places` | 팀별 장소 조회 |
| POST | `/api/places/{placeId}/reviews` | 장소 리뷰 등록 |
| GET | `/api/places/{placeId}/reviews` | 장소 리뷰 조회 |
| POST | `/api/reports` | 장소 제보 등록 |
| POST | `/api/active-users` | 실시간 접속 heartbeat 및 접속자 수 조회 |
| POST | `/api/events` | 사용자 행동 이벤트 수집 |
| GET | `/api/notice` | 서비스 공지 조회 |
| GET | `/api/admin/dashboard` | 관리자 대시보드 조회 |
| GET | `/api/admin/stats` | 운영 통계 조회 |
| POST | `/api/admin/games/sync` | KBO 경기 데이터 수동 동기화 |

---

<div align="center">
  <h3>⚾ <strong>오늘 야구, 어디서 볼까?</strong> ⚾</h3>
  <p>야구맵은 팬들이 더 쉽고 즐겁게 함께 경기를 볼 수 있도록 돕습니다.</p>
</div>
