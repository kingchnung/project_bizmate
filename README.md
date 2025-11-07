# 🚀 [BIZMATE : IT 계열 ERP 프로젝트]

[Spring Boot와 React 기반, IT 기업의 핵심 업무(인사, 프로젝트, 영업, 결재)를 통합 관리하는 ERP 시스템입니다.]
<br />
## 1. 📜 프로젝트 개요 (Overview)

### 1.1. 기획 의도 및 목적

IT 개발 회사는 특성상 '프로젝트' 단위로 '인력'이 투입되고, 그 결과로 '매출'이 발생하며, 
이 모든 과정은 '내부 결재'를 통해 이루어집니다. 
하지만 기존 ERP는 너무 무겁거나, 파편화된 솔루션(Jira, 그룹웨어, 회계 프로그램)을 별도로 사용해야 하는 비효율이 있었습니다.

본 프로젝트는 IT 기업의 핵심 업무 프로세스인
 **[인사 - 프로젝트 - 영업 - 결재]** 를 
하나로 통합(All-in-One)하여 업무 효율을 극대화하는 맞춤형 ERP 플랫폼을 목표로 기획되었습니다.

### 1.2. 제공하는 기능

- **영업 관리:** 
매출 목표, 현황 조회, 주문/판매, 수금, 거래처, 사업자등록증 관리

- **인사 관리:** 
인사카드, 인사발령, 인원 현황, 재직증명서 발급, 사용자 계정 및 권한 관리

- **프로젝트 관리:**
프로젝트 등록/관리, 상세 페이지, 팀원 배정, 업무 일정, 검색/분류, 진행 상태 관리

- **공통 모듈:** 
전자결재(문서, 결재선, 조회/추적), 사내게시판(공지/익명), JWT 기반의 안전한 로그인, 
관리자 페이지를 통한 사용자 및 콘텐츠 관리

### 1.4. 벤치마킹

**이카운트 ERP(Ecount ERP)** <br />
<img width="225" height="225" alt="image" src="https://github.com/user-attachments/assets/2413729c-f4cb-4bcf-8a76-b30b6fe2b3b6" />


장점: 다양한 기능 통합, 언제 어디서나 사용 가능한 웹 기반 접근성
<br />

## 👥 프로젝트 소개 (Introduction)

### 2.1. 팀원 소개
본 프로젝트는 4인 팀으로 진행되었으며, 저는 **팀장(Team Lead)** 역할과 **핵심 공통 모듈(전자결재, 사내게시판) 개발**을 함께 담당했습니다.


<img width="700" height="221" alt="image" src="https://github.com/user-attachments/assets/92d629a8-295a-4430-85f7-1fafc2be3afa" />

<br />


### 2.2. 기술 스택

**개발 OS :** Windows 10, 11

**DBMS :** Oracle Database XE, Hibernate / JPA 
<br />
<img width="363" height="103" alt="image" src="https://github.com/user-attachments/assets/62df1799-b1a0-4a95-9bf5-193abff6fc88" />
<br />

**개발도구 :** IntelliJ IDEA, SQL Developer, Visual Studio Code, Postman 
<br />
<img width="606" height="112" alt="image" src="https://github.com/user-attachments/assets/19592257-4f34-41f5-ab97-ba9b70bd39b6" />
<br />

**웹서버 :** Tomcat, Vite, React
<br />
<img width="377" height="112" alt="image" src="https://github.com/user-attachments/assets/54852ef6-a83d-4dd8-8564-36885b049187" />
<br />

**데이터베이스 툴 및 모델링 :** ERD Cloud, Draw.io
<br />
<img width="285" height="104" alt="image" src="https://github.com/user-attachments/assets/e72bcd8d-dbe6-4e6c-8ff2-c4e7e8c1f4b8" />
<br />

**백엔드 :** Java 21, Spring Boot 3.x, Spring Data JPA, Lombok, Spring Security + JWT, Spring Web MVC, Jackson
<br />
<img width="729" height="98" alt="image" src="https://github.com/user-attachments/assets/375f91be-82bc-414e-84c4-3f1f3c00c79c" />
<br />

**프론트엔드 :** React 18, Vite 빌드 환경, JavaScript, React Router, Axios, Redux Toolkit
<br />
<img width="614" height="87" alt="image" src="https://github.com/user-attachments/assets/3720bf72-398b-4da6-8ef5-45cbd4ba783b" />
<br />

**라이브러리 :** Ant Design (antd), Tailwind CSS, Recharts, Spring PDF, iTextPDF, React-PDF, pdfjs-dist
<br />
<img width="602" height="194" alt="image" src="https://github.com/user-attachments/assets/ecb321d6-77fd-4a9a-8776-33c8731bfbac" />
<img width="246" height="107" alt="image" src="https://github.com/user-attachments/assets/71c0a3b8-d10d-43aa-b310-a34dcd60e794" />
<br />

**협업도구 :** Git/ GitHub, SourceTree, Google Drive
<br />
<img width="399" height="105" alt="image" src="https://github.com/user-attachments/assets/e057bf0a-80b2-45f3-ba1b-5bfdeb298d5b" />
<br />


### 2.3. DB 모델링
<br />
<img width="5170" height="2603" alt="비즈메이트_ERD" src="https://github.com/user-attachments/assets/4b61a59e-4caa-435b-a0fa-11c2ef9190f1" />
<br />

- 총 25개의 테이블로 구성되어 있습니다.
- Bizmate ERD의 핵심 설계 목표는 **모듈의 독립성** 과 <br /> **데이터의 통합성** 을 동시에 달성하는 것이었습니다.
- 이를 위해 `사원(EMPLOYEES)` 엔티티를 모든 비즈니스 로직(인사, 프로젝트, 영업, 결재)의 <br /> 중심(Hub)으로 삼아 데이터 일관성을 확보했습니다.
- 마지막으로, 직급/문서유형 등은 **공통_코드(COMMON_CODE)** 로 분리하여 향후 기능 확장에 유연하게 대응할 수 있도록 설계했습니다.


## 3. 💻 프로젝트 주요 기능 (Features)

### 3.1. [전자결재 주요 기능]

**핵심 기능 1: 결재선 및 결재 관리 (`결재선/결재 관리`)** <br />
    * 기안서 종류에 따라 결재자를 조직도에서 동적으로 추가/삭제할 수 있는 UI를 구현했습니다. <br />
    * 선택된 결재선 정보는 `APPROVAL_POLICY` 테이블, 결재 순서는(`APPROVAL_POLICY_STEP`) 테이블에 저장하여 결재 순서를 보장했습니다. <br />
    
<br />
<img width="556" height="319" alt="image" src="https://github.com/user-attachments/assets/eaec6b3c-3fe8-450b-a4e3-f9ade5f6d867" />
<br />

- 문서유형별 결재선(결재자 순서) 등록 및 수정 <br />
- 다단계 결재 설정 <br />
- 결재 정책 활성화/비활성화 제어 <br />
- 실시간 정책 변경 반영(전자결재문서 작성시 자동 적용) <br />
- 결재선 정책이 없는 문서유형의 경우 수동으로 결재선 지정 가능 <br />

**핵심 기능 2: 전자결재문서 작성 (`결재문서 작성`)** <br />
    * 기안서 종류에 따라 입력폼 동적 렌더링, 입력 필드가 자동으로 변경되게 하였습니다. <br />
    * 입력된 데이터는 `APPROVAL_DOCUMENTS` 테이블에 JSON 형태를 DB에 저장하여 하나의 테이블을 이용하여 유연한 방식을 활용했습니다. <br />

<br />
<img width="633" height="367" alt="image" src="https://github.com/user-attachments/assets/247e61ee-cc80-4705-91ab-609d3a4d6cbf" />
<br />

- 문서 유형별 입력폼 동적 렌더링 <br />
- 문서 유형에 따라 입력 필드가 자동으로 변경 <br />
- 결재선 자동 적용(결재선 정책 기반) <br />
- 별도 페이지 이동 없이, 모달 내에서 작성, 저장, 상신까지 단일 플로우 <br />

**핵심 기능 3: 첨부파일 다중 등록 및 메일 발송 (`결재문서 작성`)** <br />
    * 결재 문서 작성 시 여러 개의 파일 다중 업로드가 가능하게 하였습니다. <br />
    * 결재 상신 시 결재자에게 이메일 알림이 자동 전송되게 하였습니다. <br />

<br />
<img width="631" height="378" alt="image" src="https://github.com/user-attachments/assets/7e383cc9-7ed1-47c3-a3c0-5356ed7db36e" />
<br />

- 각 문서 조회 시 첨부파일 다운로드 및 미리보기 가능 <br />
- 결재 상신 시 다음 결재자에게 자동으로 승인 요청 이메일 발송 <br />
- 결재 최종 승인 시 상신자에게 자동으로 승인 완료 이메일 발송 <br />
- 결재 반려 시 상신자에게 자동으로 반려 이메일 발송 <br />

**핵심 기능 4: PDF 자동 변환 (`결재문서 상세조회`)** <br />
    * 결재 상신 시 문서 상세 내용을 자동으로 PDF 변환하게 하였습니다. <br />
    * 상신,반려 상태의 결재문서를 상세 조회시 PDF 미리보기 및 다운로드가 가능하게 하였습니다. <br />

<br />
<img width="631" height="395" alt="image" src="https://github.com/user-attachments/assets/1b7d4477-eeb1-45f2-893a-bb89fe2a302c" />
<br />

- 결재문서를 인쇄 및 보관용으로 PDF 변환 <br />
- 결재 승인 시 서명 이미지 자동 삽입 <br />
- 서명 영역은 결재 단계 순서에 맞게 자동 배치 <br />

**핵심 기능 5: 프로젝트 자동 생성 (`프로젝트 생성`)** <br />
    * 문서 유형이 '프로젝트 기획안'인 결재문서가 최종 승인 상태가 되면 자동 트리거 실행하게 하였습니다. <br />
    * 결재문서 내 입력 데이터(프로젝트 명, 기간, 예산, 담당자)를 프로젝트 테이블에 자동 등록되게 하였습니다. <br />

<br />
<img width="637" height="386" alt="image" src="https://github.com/user-attachments/assets/bc810d58-2aef-4721-90a7-79ea8b4dd8fa" />
<br />

- 결재 승인만으로 프로젝트가 자동 등록되어 사용자가 별도로 입력할 필요 없음 <br />
- 전자결재와 프로젝트 등록 과정을 하나로 통합해 이중 업무 제거 <br />

### 3.2. [사내게시판 주요 기능]

**핵심 기능 1: 공지사항 상단 고정 (`공지사항 게시글`)** <br />
    * 공지사항 게시글은 상단 고정으로 구분 표시하게 하였습니다. <br />
    * 공지사항 게시글은 관리자만 등록할 수 있게 게시글 작성 시 게시판 구분을 동적 렌더링 하였습니다. <br />
    
<br />
<img width="628" height="363" alt="image" src="https://github.com/user-attachments/assets/cdb48152-16c9-40ed-bfc2-d3f0c29e004f" />
<br />

- 여러 유형의 게시판을 하나의 페이지에서 확인 가능 <br />
- 공지사항은 시각적으로 구분, 상단 고정으로 중요도 부각 <br />

**핵심 기능 2: 익명건의게시판 (`익명 게시글`)** <br />
    * 게시글 작성자 및 댓글 작성자 모두 '익명'으로 표시되며, 관리자만 식별하게 하였습니다. <br />
    * 사용자는 모든 게시글을 열람할 수 있으나, 게시글 수정 및 삭제는 작성자 또는 관리자만 가능하게 하였습니다. <br />
    * 댓글 작성자 역시 자동으로 익명 처리되게 하였습니다. <br />
    
<br />
<img width="627" height="387" alt="image" src="https://github.com/user-attachments/assets/17fd35eb-e94b-45f6-b83c-bc9e59658eaf" />
<br />

- 작성자 정보 노출 없이도 관리자가 부적절한 내용 제어 가능 <br />
- 자유로운 제안과 건전한 토론 문화 조성 <br />

### 3.3. [영업관리 주요 기능]

**핵심 기능 : 판매 관리 (`주문`)** <br />
    * 주문 데이터 연동으로 판매(매출) 정보 자동 등록되게 하였습니다. <br />
    * 거래명세서 PDF 생성 및 다운로드가 가능하게 하였습니다. <br />
    * 단가, 수량 입력 시 부가세와 합계 자동 입력되게 하였습니다. <br />

<br />
<img width="629" height="367" alt="image" src="https://github.com/user-attachments/assets/f42c1b95-a2ad-448c-b5d3-793dce7c10ef" />
<br />

- 주문 -> 판매 데이터 연동으로 반복 입력 제거 및 오류 방지 <br />
- PDF 버튼 클릭 한 번으로 관련 증빙 문서 즉시 생성 <br />

### 3.3. [프로젝트 관리 주요 기능]

**핵심 기능 : 프로젝트 구성원 관리 (`프로젝트 구성원 추가`)** <br />
    * 부서별 직원 자동 조회 및 검색 기능을 제공하였습니다. <br />
    * 필요 시 구성원 개별 삭제가 가능하게 하였습니다. <br />

<br />
<img width="623" height="365" alt="image" src="https://github.com/user-attachments/assets/5a2417be-2e3e-4460-ad7e-28e3d6b0e5d3" />
<br />

- 모달 내에서 검색/추가/저장이 한번에 이루어지는 직관적인 흐름 <br />
- 중복 추가나 미입력 방지를 통해 오류를 최소화 <br />
- 페이지 이동 없이 처리되어 업무 흐름이 끊기지 않는 설계 <br />

### 3.3. [인사 관리 주요 기능]

**핵심 기능 : 부서/직급/권한 관리 (`시스템 관리`)** <br />
    * 신규 부서 생성 및 기존 부서의 부서장 임명/변경하게 하였습니다. <br />
    * 사원, 대리 등 직급과 팀원, 팀장 등 직위를 생성, 수정, 삭제하게 하였습니다. <br />
    * 관리자, 일반사용자 시스템 역할을 생성하고 역할별 메뉴 접근 권한을 설정하게 하였습니다. <br />

<br />
<img width="631" height="370" alt="image" src="https://github.com/user-attachments/assets/59c5fd4a-897a-4a07-8e8b-7c510975368a" />
<br />

- 핵심 기준 정보를 관리하는 페이지이므로 시스템관리자만 접근할 수 있도록 제한 <br />
- 데이터 무결성을 위해 삭제 기능은 모두 **논리적 삭제** 방식을 사용 <br />


## 4. 문제 원인 해결 (Troubleshooting)

### 4.1. Git 브랜치 전략 부재로 인한 잦은 충돌

🚨 문제 (Problem)

- 프로젝트 초기, 팀원 모두가 develop 브랜치에 직접 커밋 및 푸쉬(Push)를 하며 작업했습니다.
- 이로 인해 팀원 간의 코드가 자주 충돌했고, 심한 경우 다른 팀원의 작업 내역이 유실되거나 <br />develop 브랜치가 오염되어 프로젝트 전체가 멈추는 상황이 발생했습니다.

🔍 원인 (Cause)

- 팀원마다 Git 사용 숙련도가 달랐습니다.
- 명확한 브랜치 관리 규칙이 없었습니다.
- 코드 리뷰 및 병합을 관리한느 담당자가 없었습니다.

💡 해결 (Solution)

- 팀장으로서 Git-flow 브랜치 전략을 도입했습니다.
- develop 브랜치에는 직접적인 Push를 금지시켰습니다.
- 모든 개인 브랜치는 develop 브랜치로 Pull Request를 통해서만 병합 혹은 <br />팀장이 강제 병합할 수 있도록 규칙을 정하고, 팀장인 제가 직접 모든 코드를 코드리뷰하고 <br /> 승인 후 병합 했습니다.

✌️ 결과 (Result) 

- develop 브랜치의 안정성을 확보하여, 빌드가 깨지는 상황을 원천 차단했습니다.
- 버그를 사전에 발견할 수 있었습니다.
- 팀원 모두가 Git과 협업 프로세스에 대해 이해하게 되었습니다.

### 4.2. 최종 승인 시 여러 DB 작업의 트랜잭션 및 데이터 정합성 문제

🚨 문제 (Problem)

- 전자결재의 최종 승인은 단순한 UPDATE 작업이 아니었습니다. <br /> 1. 결재 문서의 상태를 APPROVED로 변경 <br /> 2. 문서가 '프로젝트 기획안'일 경우 projectService를 호출하여 프로젝트를 자동 생성 <br /> 3. 작성자에게 최종 승인 알림 메일 발송
- 만약 1번(결재 승인)은 성공했는데, 2번(프로젝트 생성)에서 오류가 발생할 경우, 데이터가 심각하게 꼬이는 <br /> 문제가 발생했습니다.

🔍 원인 (Cause)

- 결재 승인과 프로젝트 생성이란느 두 개 이상의 DB 변경 로직이 하나의 비즈니스 트랜잭션으로 묶여있지 않았습니다.
- 이로 인해 부분 성공이 허용되어, 시스템의 상태가 불일치하게 될 위험이 있었습니다.

💡 해결 (Solution)

- Spring의 `@Transactional` 어노테이션을 `approve` 서비스 메소드 전체에 선언했습니다.
- 이를 통해, `approve` 메소드 내에서 발생하는 모든 DB 작업(결재선 업데이트, 문서 상태 변경, <br /> `projectService.createProjectByApproval 호출)이 **하나의 논리적인 작업 단위(Transaction)** 로 묶이도록 보장했습니다.  

✌️ 결과 (Result) 

- 전부 성공 또는 전부 실패 원칙을 적용하여, '승인'과 '프로젝트 생성'이 반드시 동시에 성공하거나 실패하도록 만들었습니다.
- 복잡하게 얽힌 여러 비즈니스 로직(결재, 프로젝트, 알림)에서도 데이터의 정합성과 일관성을 100% 보장하는 <br /> 안정적인 시스템을 구축할 수 있었습니다.

<br />
<img width="870" height="810" alt="image" src="https://github.com/user-attachments/assets/f4daa10a-c3f5-48ca-bc9b-aad604f881cd" />
<br />

### 4.3. 동적 폼 데이터(JSON) 저장을 위한 JPA 엔티티 설계 문제

🚨 문제 (Problem)

- '전자결재' 기능은 '휴가 신청서', '지출 결의서', '프로젝트 기획서' 등 **서로 다른 양식** 의 문서를 모두 처리해야 했습니다.
- RDBMS(Oracle)는 스키마가 고정되어 있어, 이 모든 폼의 개별 필드(휴가일, 지출액, 프로젝트명 등)를 <br /> `ApprovalDocuments` 테이블의 컬럼으로 만드는 것은 불가능했습니다.

🔍 원인 (Cause)

- RDBMS의 정형화된 스키마와, '결재'라는 도메인의 비정형/동적 데이터 요구사항 간의 불일치가 발생했습니다.

💡 해결 (Solution)

- **JPA의 `AttributeConverter`**를 사용하여, 동적인 폼 데이터를 JSON 문자열로 변환하여 <br /> DB의 CLOB (또는 TEXT) 타입 컬럼(doc_content)에 저장하는 방식을 채택했습니다. <br />
1. '휴가 신청서', '프로젝트 기획서' 등의 폼 데이터를 담을 수 있는 DTO(`ApprovalDocumentsDTO`)를 정의했습니다.
2. 이 DTO 객체를 JSON 문자열로 변환(직렬화)하고, 반대로 JSON 문자열을 DTO 객체로 변환(역직렬화)하는 <br /> **AttributeConverter**를 구현했습니다.
3. `ApprovalDocuments` 엔티티의 `docContent` 필드에 `@Convert(converter = JsonMapConverter.class)` 어노테이션을 적용했습니다.

✌️ 결과 (Result) 

- `AttributeConverter`가 DB와 엔티티 사이에서 자동 변환을 처리해주게 되었습니다.
- Service단에서 objectMapper를 사용하여, DB의 CLOB 컬럼에 저장된 JSON 데이터를 `ProjectRequestDTO` <br /> 특정 DTO로 유연하게 변환하여 '프로젝트 자동 생성' 로직에 전달할 수 있었습니다.

<br />
<img width="789" height="436" alt="image" src="https://github.com/user-attachments/assets/0927b613-1075-4794-ac94-1a6ff8cf7e8e" />
<br />
<img width="817" height="564" alt="image" src="https://github.com/user-attachments/assets/8cef8808-3a16-4927-a216-d524db2da868" />
<br />
<img width="1005" height="577" alt="image" src="https://github.com/user-attachments/assets/76095a76-e5cd-4d76-8cb2-6f49e337c81b" />

