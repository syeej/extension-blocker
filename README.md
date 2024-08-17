# 파일 확장자 차단
어떤 파일들은 첨부 시 보안에 문제가 될 수 있습니다.. 특히 exe, sh 등의 실행파일이 존재할 경우 서버에 올려서 실행이 될 수 있는 위험이 있어 파일 확장자 차단을 하게 되었습니다.

## ⚙️ 사용 기술
Java17, Spring Boot 3.3.2, MySQL 8.0.39, Redis, HTML5, jQuery, Thymeleaf

## ⚙️ ERD
<img width="500" alt="image" src="https://github.com/user-attachments/assets/52ee6b8a-0e65-49db-a15b-b2458270639f">

## ⚙️ 기능
- 고정 확장자
  - 고정 확장자는 차단을 자주하는 확장자를 리스트이며, default는 unCheck되어져 있습니다.
  - 고정 확장자를 check or uncheck를 할 경우 db에 저장됩니다. - 새로고침시 유지되어야합니다.
- 커스텀 확장자
  - 추가버튼 클릭시 db 저장되며, 아래쪽 영역에 표현됩니다.(최대 200개까지 추가 가능)
  - 확장자 옆 X를 클릭시 db에서 삭제
- 고려사항
  - 커스텀 확장자 입력 시, 고정 및 커스텀 확장자와 중복 검사
  - 고정 확장자 check or uncheck할 경우, 빈번한 조회 작업으로 인한 DB 성능 저하를 고려
    - Java ConcurrentMap을 활용하여 인메모리 캐싱 구현 후 1시간 마다 DB 동기화  
    - 확장성 및 회사 인프라고려하여 Redis로 변경
  
## 🖥 화면
<img width="500" alt="image" src="https://github.com/user-attachments/assets/d19a07cf-3997-44cf-bdd3-d725ba32edc2">

