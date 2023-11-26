# index

1. [프로젝트 소개](#a-프로젝트-소개)
2. [사용 기술](#b-사용-기술)
3. [프로젝트 구조](#c-프로젝트-구조)
4. [aws architecture](#d-aws-architecture)
5. [erd diagram](#e-erd-diagram)
6. [sequence diagram](#f-sequence-diagram)
    1. [spring security + redis로 세션관리 하면서 이상행동 감지시 invalidate session + account lock](#a-spring-security--redis로-세션관리-하면서-이상행동-감지시-invalidate-session--account-lock)
7. [기술적 도전](#g-기술적-도전)
    1. [bulk insert](#a-bulk-insert)
    2. [api first design](#b-api-first-design)
    3. [spring batch](#c-spring-batch)
    4. [query tuning](#d-query-tuning)
    5. [defensive programming](#e-defensive-programming)
    6. [clean code](#f-clean-code)
8. [trouble shooting](#h-trouble-shooting)
    1. [queryDSL library와 openapi-codegen이 build.gradle에서 컴파일시 깨지는 문제 해결](#a-querydsl-library와-openapi-codegen이-컴파일시-깨지는-문제-해결)


# A. 프로젝트 소개

쇼핑몰 MVP

인증, 상품, 주문 관련 기능이 존재한다.

## a. how to start project?
```
1. git clone https://github.com/Doohwancho/ecommerce
2. docker compose up
```


# B. 사용 기술

- spring
    - jdk 1.8
    - spring security
    - spring data jpa
    - spring batch
- external java library
    - querydsl
    - openapi-codegen
    - jqwik
    - mapstruct
    - datafaker
- database
    - mysql 8
    - redis
- aws
- docker


# C. 프로젝트 구조

```
── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── cho
│   │   │           └── ecommerce
│   │   │               ├── Application.java
│   │   │               ├── domain
│   │   │               │   ├── member
|   │   │               │   ├── order
│   │   │               │   └── product
|   │   │               ├── global
│   │   │               │   ├── config
│   │   │               │   │   ├── batch
│   │   │               │   │   │   ├── config
│   │   │               │   │   │   ├── job
│   │   │               │   │   │   ├── listener
│   │   │               │   │   │   ├── scheduled
│   │   │               │   │   │   └── step
│   │   │               │   │   ├── database
│   │   │               │   │   ├── fakedata
│   │   │               │   │   ├── parser
│   │   │               │   │   ├── redis
│   │   │               │   │   └── security
│   │   │               │   │       ├── handler
│   │   │               │   │       └── session
│   │   │               │   ├── error
│   │   │               │   │   └── exception
│   │   │               │   │       ├── business
│   │   │               │   │       ├── common
│   │   │               │   │       └── member
│   │   │               │   └── util
│   │   │               └── infra
│   │   │                   ├── email
│   │   │                   └── sms
│   │   └── resources
│   │       ├── api
│   │       │   ├── config.json
│   │       │   └── openapi.yaml
│   │       ├── application-local.yml
│   │       ├── application-prod.yml
│   │       ├── application-test.yml
│   │       ├── application.yml
│   │       ├── log
│   │       │   ├── console-appender.xml
│   │       │   ├── file-error-appender.xml
│   │       │   ├── file-info-appender.xml
│   │       │   └── file-warn-appender.xml
│   │       ├── logback-spring.xml
│   │       └── templates
│   └── test
│       └── java
│           └── com
│               └── cho
│                   └── ecommerce
│                       ├── Integration_test
│                       ├── property_based_test
│                       ├── smoke_test
│                       └── unit_test
├── log
│   ├── error
│   ├── info
│   └── warn
```

# D. aws architecture
![](documentation/images/aws-architecture.png)


# E. erd diagram
![](documentation/images/erd.png)


# F. sequence diagram

## a. spring security + redis로 세션관리 하면서 이상행동 감지시 invalidate session + account lock
?




# G. 기술적 도전

## a. bulk insert

가데이터를 for-loop으로 넣던걸 spring batch + jpa bulk insert로 변경해서 14분 30초 -> 4분30초 로 10분 단축

```
...for inserting

3000 users
10 categories
30 options
90 option variations
3000 products
9000 product items
9000 product option variations
3000 orders
15000 order items
```


1. before (for-loop insert)
    - Total execution time: 14m 25s (864952 ms)
2. after (bulk insert)
    - 4m 30s 436ms
    - Job: [SimpleJob: [name=dataInitializationJob]] completed with the following parameters: [{run.id=1700922576191, numberOfFakeUsers=3000, numberOfFakeCategories=10, numberOfFakeOptionsPerCategory=3, numberOfFakeOptionVariationsPerOption=3, numberOfFakeProducts=3000, numberOfFakeProductItemsPerProduct=3, numberOfFakeOrderItemsPerOrder=5}] and the following status: [COMPLETED] in 4m30s436ms



## b. api first design
openapi-codgen + redoc 적용

[openapi 3.0.3 ver documentation link](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/main/resources/api/openapi.yaml)


## c. spring batch

![](documentation/images/inactive-user.png)

이상행동으로 잠긴 유저 계정을 매주 일요일 새벽 3시에 INACTIVE_USER로 옮기고, 기존 유저 테이블에서 제거하는 배치 생성


[batch code link](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/main/java/com/cho/ecommerce/global/config/batch/step/UserToInactiveMemberStepConfig.java)



## d. query tuning
?


## e. defensive programming

### 1. testing 전략
1. smoke test
    - springboot app이 잘 실행되는지
    - 유저 인증시 이상 현상이 일어나는지 확인
2. integration test
    - 도메인 별로 굵직한 서비스 레이어 위주로 테스트
3. property test
    - 절대 문제생기면 안되는 기능(ex. 돈 관련 코드 등..)을 PBT로 테스트. ([PBT code link](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/test/java/com/cho/ecommerce/property_based_test/ProductPriceDiscountTest.java))
4. unit test




### 2. exception 전략
Runtime Exception을 domain별로 나누어 일괄관리한다.
1. common
2. business
3. member

- 모든 business 관련 Exception들은 BusinessException을 상속받아 일괄관리하고,
- 모든 member 관련 Exception들 또한 MemberException을 상속받아 일괄관리한다.
- Exception에 들어가는 Error Code역시 도메인 별로 일괄관리한다.

[error code file link](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/main/java/com/cho/ecommerce/global/error/ErrorCode.java)


### 3. logging 전략
1. logging format을 가독성이 좋게 설정 (디테일한 정보 + log level별 색깔 다르게 설정)
2. profile 별(ex. test/local/prod) log level을 구분하여 log/ 디렉토리에 레벨별로 저장



## f. clean code

### 1. protocol 설정
1. commit-message protocol
2. error code protocol
3. common / business / member 용 exception 구분

### 2. linter intellij plugins 적용
1. sonarlint
2. checkstyle
3. code-style-formatter ([google style java format 적용](https://google.github.io/styleguide/javaguide.html))

# H. trouble shooting
```
Q. how to find all trouble shooting list?

1. git clone https://github.com/Doohwancho/ecommerce
2. git log --grep="fix"
```

## a. queryDSL library와 openapi-codegen이 컴파일시 깨지는 문제 해결

### 1. 문제
스프링 + openapi-codgen library도 잘 동작하고, 스프링 + queryDSL로 잘 동작하는데, 둘을 동시에 쓰면 빌드가 안되는 현상 발생

### 2. 문제 원인
queryDSL과 openapi-codegen 둘 다 빌드시 코드를 동적으로 생성하는데, compileQuerydsl시 아직 컴파일 안된 openapi-codegen 코드부분 때문에 에러 발생

### 3. 해결책
gradle 빌드 순서를 openapi-codegen 컴파일이 먼저 실행되고, queryDSL이 다음에 실행되고, 마지막으로 compileJava가 실행되도록 변경했다.

[해결한 커밋 link](https://github.com/Doohwancho/ecommerce/commit/ffb5355069d127ba1e6745626bbbcd6da4fbc4ac)
