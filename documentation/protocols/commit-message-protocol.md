---\
Idea

mono repo에 FE/BE/DBA/DevOps/QA가 직군별, 도메인별로 나누어서 커밋을 관리한다.

---\
구조

either,
1. {직군}-{domain}-{요구사항}-{actions per domain}-{prev+1}: {content}
  - ex. back-product-상품조회-feat-11: add product api
2. {직군}-{Etc}-{prev+1}: {content}
  - ex. back-build-3: add build script
  - ex. all-docs-1: add protocols

Q. 직군-domain 순서가 유리할까? 아니면 domain-직군 순서가 유리할까?

A. 
1. 직군-domain 순서는, 팀장1, 프론트1, 백1인 소규모 팀이라면 더 유리하고, 
2. Domain-직군 순서는 MSA가 가능한 도메인 별로 부사가 나눠진 경우가 더 유리하다


---\
직군

1. front
2. back
3. devops
4. all

---\
Domain

1. front
  1. authentication
  2. product
2. backend
  1. authentication
  2. product
  3. order
3. devops
  1. terraform
  2. monitoring
  3. logging
  4. stress
  5. ci
  6. cd


---\
Actions per Domain
: 도메인에 종속된 행동들

1. design : 설계에 대한 커밋
   1. db : 디비 대한 설계
   2. api : API에 대한 설계
2. feat : 새로운 기능에 대한 커밋
3. test : 테스트 코드 수정에 대한 커밋
4. perf : 성능향상에 대한 커밋
5. style : 코드 스타일 혹은 포맷 등에 관한 커밋
6. refactor : 코드 리팩토링에 대한 커밋
7. fix : error 수정에 대한 커밋
8. defensive : defensive programming에 대한 커밋
9. security: security 관련 커밋
10. docs : 문서 수정에 대한 커밋
11. build : changes that affect the build system or external dependencies

---\
Etc
: 도메인 무관, 해당 직군 전체에 해당되는 행동

1. docs : 문서 수정에 대한 커밋
2. build : changes that affect the build system or external dependencies
3. ci : CI 관련 설정 수정에 대한 커밋
4. chore : 그 외 자잘한 수정에 대한 커밋(ex. 툴링, 오타수정)
5. revert : reverts the previous commit
6. bump: 버전업에 대한 커밋
7. monitor: 모니터링 관련 커밋