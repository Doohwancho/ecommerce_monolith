# index

- A. [프로젝트 소개](#a-프로젝트-소개)
- B. [사용 기술](#b-사용-기술)
- C. [AWS architecture](#c-aws-architecture)
- D. [ERD diagram](#d-erd-diagram)
- E. [기술적 도전 - Backend](#e-기술적-도전---backend)
    - a. [spring security - authentication](#a-spring-security---authentication)
    - b. [spring batch](#b-spring-batch)
    - c. [bulk insert](#c-bulk-insert)
	- d. [test 전략](#d-test-전략)
    - e. [defensive programming](#e-defensive-programming)
    - f. [clean code](#f-clean-code)
	- g. [DDD](#g-ddd)
- F. [기술적 도전 - Database](#f-기술적-도전---database)
    - a. [정규화](#a-정규화)
    - b. [통계 쿼리](#b-통계-쿼리)
    - c. [sql tuning](#c-sql-tuning)
- G. [기술적 도전 - Cloud](#g-기술적-도전---cloud)
	- a. [docker](#a-docker)
	- b. [aws](#b-aws)
	- c. [provisioning with terraform](#c-provisioning-with-terraform)
	- d. [prometheus and grafana](#d-prometheus-and-grafana)
	- e. [대용량 트래픽 처리](#e-대용량-트레픽-처리)
- H. [기술적 도전 - Frontend](#h-기술적-도전---frontend)
	- a. [프로젝트 구조](#a-프로젝트-구조)
	- b. [component 모듈화](#b-component-모듈화)
	- c. [state management](#c-state-managment)
    - d. [API first design](#d-api-first-design)
	- e. [filter 기능 구현](#e-filter-기능-구현)
	- f. [페이지 로드 속도 개선](#f-페이지-로드-속도-개선)
- I. [Trouble Shooting](#i-trouble-shooting)
    - a. [queryDSL library와 openapi-codegen이 build.gradle에서 컴파일시 깨지는 문제 해결](#a-querydsl-library와-openapi-codegen이-컴파일시-깨지는-문제-해결)
    - b. [그 외 trouble shooting 내역](#b-그-외-trouble-shooting-커밋-내역들)


# A. 프로젝트 소개

![](./documentation/images/main-page-demo.gif)

쇼핑몰 MVP\
인증, 상품, 주문 관련 기능이 존재한다.

## a. how to start project?
```
1. git clone https://github.com/Doohwancho/ecommerce
2. cd ecommerce
3. docker compose up --build
4. 1000 data insert 완료할 때까지 기다리기
5. http://localhost:80  or  http://127.0.0.1:80 로 접속
```



# B. 사용 기술

## a. 백엔드
| Category             | Tool/Library              | Version |
|----------------------|---------------------------|---------|
| Java                 | JDK                       | 1.8     |
| Spring               | Spring Boot Starter Web   | 2.5.6   |
|                      | Spring Security           | 2.5.6   |
|                      | Spring Data JPA           | 2.5.6   |
|                      | Spring Batch              | 2.5.6   |
|                      | Spring Quartz             | 2.5.6   |
|                      | Spring Boot Starter Cache | 2.5.6   |
| External Java Library| QueryDSL                  | 4.4.0   |
|                      | OpenAPI-Codegen           | 4.3.1   |
|                      | Jqwik                     | 1.8.1   |
|                      | MapStruct                 | 1.5.5   |
|                      | Datafaker                 | 1.9.0   |
| Database             | MySQL                     | 8.0.23  |
|                      | Redis                     | 7.0.9   |
| Test                 | junit                     | 5.9.2   |
|                      | hamcrest                  | 2.2     |
| Deploy               | AWS                       |         |
|                      | Docker                    |         |
| Development Tools    | IntelliJ                  |         |
|                      | MySQL Workbench           |         |
|                      | Postman                   |         |
|                      | Redoc                     |         |
|                      | VSC Plugin - Draw.io Integration |         |
|                      | VSC Plugin - ERD Editor|         |


## b. 프론트엔드
| Category             | Tool/Library           | Version |
|----------------------|------------------------|---------|
| Language             | Typescript             | ^5.2.2  |
| React                | React                  | ^18.2.0 |
|                      | React-Router-Dom       | ^6.20.0 |
| Style                | Styled-Component       | ^6.1.1  |
| State Management     | Recoil                 | ^0.7.7  |
|                      | React-Query            | ^3.39.3 |
| Build Tool           | Vite                   | ^5.0.0  |
| API                  | openapi-generator-cli  | ^2.7.0  |
| external library     | react-slick            | ^0.29.0 |
|                      | slick-carousel         | ^1.8.1  |
|                      | react-icons            | ^4.12.0 |



# C. AWS architecture
![](documentation/images/aws-architecture.png)


# D. ERD diagram
![](documentation/images/erd.png)

VSC plugin: ERD Editor를 다운받고, documentation/erd.vuerd.json 파일을 열 수 있다.




# E. 기술적 도전 - Backend

## a. spring security - authentication
![](documentation/sequence-diagram/authentication-sequence.png)

- 구현 기능
	1. spring security + redis(session clustering)로 세션관리 한다.
	2. 이상행동 감지시(로그인 5회 틀림) invalidate session + account lock 한다.
	3. 매주 일요일 새벽 3시에 cron + batch로 locked account를 MEMBER table에서 INACTIVE_MEMBER table로 이전한다.


## b. spring batch

![](documentation/images/inactive-user.png)
- 구현 기능
	1. 이상행동으로 잠긴 유저 계정을
	2. 매주 일요일 새벽 3시에
	3. INACTIVE_USER로 옮기고,
	4. 기존 유저 테이블에서 제거하는 배치 생성한다.

code example)
https://github.com/Doohwancho/ecommerce/blob/73ddd650c20ca7349cdbf3d992ca1fe357c67da4/back/ecommerce/src/main/java/com/cho/ecommerce/global/config/batch/step/UserToInactiveMemberStepConfig.java#L26-L146



## c. bulk insert

### 1. 문제

데이터베이스 테스트를 위한 fake 데이터 수천, 수만개를 for-loop으로 넣던게 너무 오래 걸렸다. (약 14분 30초 정도 소요)

### 2. 문제의 원인
for-loop으로 한개씩 1000번 insert하면, @transaction 을 1000번 거는데,\
lock 걸고 입력하고 풀고 하는걸 천번하니까 오래걸린 것이다.

### 3. 해결책
한 batch당 chunk size를 1000개로 해서 한 트랜젝션당 1000개씩 bulk insert 한다.

### 4. 결과
14분 30초 -> 4분 30초로 10분 단축되었다.

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

https://github.com/Doohwancho/ecommerce/blob/73ddd650c20ca7349cdbf3d992ca1fe357c67da4/back/ecommerce/src/main/java/com/cho/ecommerce/global/config/batch/step/InsertFakeUsersStepConfig.java#L28-L153




## d. test 전략

1. smoke test
    - springboot app이 RUNNING 상태인지 확인한다.
    - 유저 인증시 이상 현상이 일어나는지 확인한다.
2. integration test
    - 도메인 별로 굵직한 서비스 레이어 위주로 테스트한다.
    - mocking 보다는, 최대한 넓은 범위의 모듈을 커버하여 깨지는 부분이 있는지, 있다면 대략 어느 부분인지 확인한다.
3. property based test (PBT)
    - 절대 문제 생기면 안되는 기능(ex. 돈 관련 코드 등..)을 PBT로 테스트한다. ([PBT code link](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/test/java/com/cho/ecommerce/property_based_test/ProductPriceDiscountTest.java))
4. unit test
    - 이 외 작은 기능 단위는 unit test로 처리한다.




## e. defensive programming

### 1. exception 전략
1. Runtime Error가 날만한 부분에 throw CustomException 처리한다.
2. [custom Error Code Protocol](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/main/java/com/cho/ecommerce/global/error/ErrorCode.java) 에 맞추어 error code를 enum으로 선언한다.
3. Runtime Exception을 domain별로 나누어 일괄관리한다.
    - 모든 business 관련 Exception들은 BusinessException을 상속받아 일괄관리하고,
    - 모든 member 관련 Exception들 또한 MemberException을 상속받아 일괄관리한다.
    - Exception에 들어가는 Error Code역시 도메인 별로 일괄관리한다.

>

### 2. logging 전략
1. 에러가 날만한 부분에 log.error() 처리한다.
2. logging format을 가독성이 좋게 설정한다. (디테일한 정보 + log level별 색깔 다르게 설정)
3. profile 별(ex. local/docker/prod/test) log level을 구분하여 log/ 디렉토리에 레벨별로 저장한다.


### 3. Valid 전략
1. openapi에서 필드마다 validity 조건 걸어서, 컨트롤러 레이어에서 파라미터로 받을 때, 백엔드 시스템 안에 들어오는 필드값을 1차적으로 type check, null check한다.
2. backend Entity에 validity 조건을 걸어서 database에 값을 넣을 때, 올바른 값이 들어가는지 다시한번 필터링 한다.


### 4. rate limiting

1. backend server에 http request시,
2. 개별 ip address마다
3. 1초에 5 request 리밋을 건다.
4. 단, "burst"라고 초당 기본 5 request에 short spike of 10 request까지 queue에 담아 허용한다.
5. 그 이상 request가 오면 503 Service Temporarily Unavailable error 를 보낸다.

https://github.com/Doohwancho/ecommerce/blob/91f61cd43591f8d56b8925e9bb8ceac0cbe89d29/web-server/default.conf#L1-L5

https://github.com/Doohwancho/ecommerce/blob/dc963b102c65178fe7bd52960a344991272cfeab/web-server/default.conf#L29-L34



## f. clean code

### 1. protocol 설정
1. [error code protocol](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/main/java/com/cho/ecommerce/global/error/ErrorCode.java)
2. [common / business / member 용 exception 구분](https://github.com/Doohwancho/ecommerce/tree/main/back/ecommerce/src/main/java/com/cho/ecommerce/global/error/exception)
3. [commit-message protocol](https://github.com/Doohwancho/ecommerce/blob/main/documentation/protocols/commit-message.md)

### 2. linter 적용
1. sonarlint
2. checkstyle
3. code-style-formatter ([google style java format 적용](https://google.github.io/styleguide/javaguide.html))


## g. DDD
이 프로젝트는 layered architecture에서 DDD로 넘어가는 과정 속에 있다.\
차후 규모가 커지고 복잡해 지면, 점진적으로 DDD + MSA + CQRS 쪽으로 리펙토링할 예정이다.

디렉토리 구조는 다음과 같다.
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
│   │   │                   └── monitoring
│   │   └── resources
│   │       ├── api
│   │       │   ├── config.json
│   │       │   └── openapi.yaml
│   │       ├── application-local.yml
│   │       ├── application-prod.yml
│   │       ├── application-docker.yml
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



1. global/ 에는 도메인 전반적으로 적용되는 configuration, exception & error 정의, utility 파일이 들어가고,
2. infra/ 에는 email, message, monitoring 등 외부 API와 연결되는 파일들이 존재한다.
3. domain/ 은 layered architecture의 형태를 전반적으로 따라가되, 현 프로젝트에서는 필요에 의해 DDD에서 차용된 개념이 2가지가 있다.
	1. application layer에서 Entity <-> ResponseDTO 변환까지 포함되어, 너무 비대해지는 문제가 있어서, 타입 변환만 전문적으로 하는 adapter layer을 추가하여 service layer에서는 business logic 위주로 처리되도록 관심사를 분리하였다.
	2. entity와 domain을 분리하고, domain에 도메인 로직을 넣었다. service layer에서는 도메인 로직을 빼서 최대한 비즈니스 로직만 들어가게끔 관심사 분리를 하였다.


### 1. adapter layer 분리 예시

#### 1-가. 문제
```java
@Service
public class ProductService {

    private ProductService self;

	@Transactional
    public List<Product> getProductDetailDTOsById(Long productId) {
		//business logics
	}

	public List<ProductDetailResponseDTO> findProductDetailDTOsById(Long productId) {
        List<Product> productDetailDTOsList = self.getProductDetailDTOsById(
            productId); //fix: solution to "Methods should not call same-class methods with incompatible @Transactional"
        return productMapper.productsToProductDetailDTOs(productDetailDTOsList);
    }
}
```
- 문제
	1. getProductDetailDTOsById()를 작성할 때, 리턴타입을 `List<도메인>`으로 해야 다른 메서드에서도 사용 가능하니까 재사용성이 좋아진다.
	2. 그러나 controller에서 요구하는 반환타입은 `List<도메인>`이 아닌, `List<ResponseDTO>` 이기 때문에, mapper로 타입 변환을 해주어야 한다.
	3. 이 때, 같은 서비스 레이어에서 타입변환만 해주는 메서드를 만들면, 메서드 네이밍도 애매해지고,
	4. @Transactional 붙은 메서드 호출 시, `self.메서드()`로 호출해야 하는데, 그닥 좋은 패턴은 아닌 듯 하다.
	5. 서비스 레이어가 비즈니스 로직 메서드 + 타입 변환 메서드가 섞여서 비대해진다.

#### 1-나. 해결책
```java
@Component
public class ProductAdapter {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;

	public List<ProductDetailResponseDTO> getProductDetailDTOsById(Long id) {
        List<Product> productList = productService.getProductDetailDTOsById(id);
        return productMapper.productsToProductDetailDTOs(productList);
    }
}


@Service
public class ProductService {

    private ProductService self;

	@Transactional
    public List<Product> getProductDetailDTOsById(Long productId) {
		//business logics
	}
}
```

타입 변환해주는 메서드를 서비스 레이어에서 어답터 레이어로 분리함으로써, 서비스 레이어에서는 비즈니스 로직만 있도록 했다.



### 2. DDD를 고려한 요구사항 -> 비즈니스 로직 작성
요구사항: 클라이언트가 요청한 productItem 들을 discount price를 고려하여 주문을 등록한다.

#### 2-가. validation
1. validation check를 하되
2. 악성 request라면, invalidate session + lock user account

https://github.com/Doohwancho/ecommerce/blob/c595a8dd1f9932577be4a40f4e3c42d5b20b79d9/back/ecommerce/src/main/java/com/cho/ecommerce/domain/order/service/OrderService.java#L68-L88


#### 2-나. domain 종속 함수

product의 가격 계산시, discount를 적용하는 함수가 Product, Discount 도메인 객체에 들어있다.

https://github.com/Doohwancho/ecommerce/blob/c595a8dd1f9932577be4a40f4e3c42d5b20b79d9/back/ecommerce/src/main/java/com/cho/ecommerce/domain/product/domain/Product.java#L57-L66

https://github.com/Doohwancho/ecommerce/blob/c595a8dd1f9932577be4a40f4e3c42d5b20b79d9/back/ecommerce/src/main/java/com/cho/ecommerce/domain/product/domain/Discount.java#L20-L37


#### 2-다. domain 종속 함수는 unit test or PBT로 테스트

Discount 도메인 객체에 applyDiscount()는 돈이 걸린 아주 중요한 함수이므로,\
Property Based Testing을 한다.

https://github.com/Doohwancho/ecommerce/blob/c595a8dd1f9932577be4a40f4e3c42d5b20b79d9/back/ecommerce/src/test/java/com/cho/ecommerce/property_based_test/ProductPriceDiscountTest.java#L25-L100


#### 2-라. 전체 코드
https://github.com/Doohwancho/ecommerce/blob/c595a8dd1f9932577be4a40f4e3c42d5b20b79d9/back/ecommerce/src/main/java/com/cho/ecommerce/domain/order/service/OrderService.java#L68-L174



# F. 기술적 도전 - Database

## a. 정규화

### 가. case 1. product를 비정규화 한 방식
![](documentation/images/정규화-1.png)

#### case 1's pros
개별 제품 상세 페이지 쿼리는 빠르다.

---

#### case1's cons

1. 주문 목록 query가 느려진다.
	- 구매자가 주문목록 query하려면, 모든 상품 테이블들 다 돌면서 product_id 찾아야 하니까 엄청 느리다.
	- 이걸 완화하기 위해, 모든 상품테이블에 들어았는 product_id를 인덱스 거는게 최선인 것 같지는 않다.
2. 상품 카테고리별로 테이블 만들어줘야 해서 테이블 갯수가 수십~수백개로 늘어난다.
	- 의외로 테이블 갯수 자체가 늘어나는건 별 문제가 아니라고 한다.
	- 다만, 그보다 비정규화 했을 때, 상품 끼리 통일된 구조가 아닌게 더 문제라고 한다.
	- 통일된 구조가 아니면 나중에 확장할 때 merge, 변형 등이 힘들어지기 때문이다.
	- erd 설계 한번하면 쭉 가는줄 알았는데, 의외로 서비스 초기 때에도 스키마 변경을 자주 할 수 있다고 한다. 유연한 설계를 하자.

---

### 나. case 2. order_item 테이블에 모든 비정규화한 상품테이블 리스트의 FK를 받는 방식
![](documentation/images/정규화-2.png)

#### case 2's pros
case 1과 같이, 개별 상품 페이지 쿼리는 빠르다.

---

#### case 2's cons

1. 필드 갯수가 100개 이상인 테이블이 생길 수 있다.
	- 상품 종류가 100가지라 상품 테이블이 100가지면, order_item가 받는 상품들의 fk가 100개+가 될 것이기 때문이다.
2. 불필요한 null check 코드가 많아지고, 이는 휴먼에러날 확률을 높힌다.
	- 주문목록 query하려면, null check 먼저 하고,해당 아이템의 fk 가지고 아이템 찾는 식 일텐데,
	- 100개 컬럼 중 99개 컬럼이 Null인데 하나씩 Null비교해서 값을 꺼내는 방식은 안좋은 방식 같다.
	- 왜냐하면 Null처리 잘못할 수 있어서 에러날 가능성이 있는 코드구조가 될 수 있기 때문이다.




### 다. case 3. 상품별 옵션을 정규화 해서 쪼개놓은 경우
![](documentation/images/정규화-3.png)

#### case 3's pros

정규화가 잘 되있어서 변경에 유용하고 확장성이 좋은 설계이다.


#### case 3's cons

1. 정규화를 할 수록 쿼리할 떄 join & subquery 많이 해야 해서 느리다.
	- ex. 상품 등록/업데이트/삭제 시, product/product_item/category/option/option_variation/product_option_variation 이 6개 테이블에 트랜잭션/lock 걸릴텐데, 너무 느릴 것 같다.


### 라. 결론
case 3을 택한다. 이유는 다음과 같다.


#### 1. 서비스 초기에는 성능보다 확장성 우선

비정규화는 일종의 최적화이고 되돌리기 힘든 과정이다.\
서비스 초기 단계라면 구현된 기능 자체가 수정&삭제가 빈번한데 이럴 경우 정규화된 구조를 사용하여 기능의 수정 & 삭제같은 유지보수를 저렴한 비용으로 유연하게 할 수 있도록 하는 것이 맞다.

서비스가 더 커진다 해도 캐싱, 인덱싱, 분산처리(가용영역 추가, 비쌈)같은 테크닉을 쓸 수 있고,\
나중에 서비스가 커져서 비정규화나 MSA같이 RDBMS가 보장해주는 것 일부를 포기하고 더 최적화를 해야할 경우가 오면, 이 때 해당 프로젝트 진행하면 된다.

결론: 정규화하고 최적화는 나중에 병목이 생기면 그 때 반정규화 한다.


---
#### 2. 데이터베이스 규모별 정규화 & join 전략

join 성능은 데이터 사이즈가 커질수록 안좋아진다.

이유는 다음과 같다.

여러 테이블 join시, primary key 기준으로 join한다고 해도, 데이터 사이즈가 작으면 primary key를 index한 테이블을 몇번 안타는데,\
데이터 사이즈가 커지면, 여러 테이블들의 primary key index table 여러번 타기 때문에 join 성능이 떨어진다.

예를들어, 5개정도 테이블을 left outer join 하는 경우, 약 10개의 rows씩 5개 테이블이니까 50개 rows가 쿼리 1번당 lock되는건데, 멀티쓰레드 환경에서는 반정규화로 row 1개만 락걸고 가져오는 것 대비 성능이 좋지 않다.

따라서 서비스 초창기 때 데이터 수가 적을 땐 join 효율이 괜찮으니 정규화로 확장성을 잡다가,\
유저수가 많아지고 데이터 쌓인게 엄청 많아져 join 효율이 떨어지는 시기가 오면,
다음과 같은 행동을 취할 수 있다.

1. 사용하던 RDB에서 정규화된 테이블을 비정규화 테이블로 마이그레이션을 한다.
2. 사용하던 RDB에서 정규화된 테이블을 놔두고, 따로 비정규화된 테이블을 만들어서 write-through성 으로 따로 만든다.(대신 데이터 정합성이 떨어지는 것 고려해야 함)
3. 별개의 nosql(ex. mongodb)에 기존 RDB 테이블들(aggregates)을 비정규화한 스키마를 하나 만든다.
4. 샤딩
5. 파티셔닝
6. MSA로 쪼개서 도메인별로 해당 도메인에 맞는 데이터를 해당 서비스 전용 디비에 넣어 붙인다.
7. 자주 사용하는 쿼리는 캐싱처리한다. (ex. main page)







## b. 통계 쿼리
### 1. 요구사항
1. 23년 6월 ~ 23년 12월 사이에
2. 카테고리 별 상품 갯수
3. 해당 카테고리의 상품들의 평균 평점
4. 해당 카테고리의 총 상품 판매액
5. 해당 카테고리에서 가장 많이 팔린 상품의 productId
6. 해당 카테고리에서 가장 많이 팔린 상품의 이름
7. 해당 카테고리에서 가장 많이 팔린 상품의 총 판매액

...을 query 한다.

### 2. sql query 문

![](documentation/images/통계쿼리.png)

```sql
SELECT
	tmp1.CategoryId,
    tmp1.CategoryName,
    tmp1.NumberOfProductsPerCategory,
    tmp1.AverageRating,
    tmp1.TotalSalesPerCategory,
    tmp2.ProductId,
    tmp2.ProductName AS TopSalesProduct,
    tmp2.TopSalesOfProduct
FROM (
	SELECT
		c.CATEGORY_ID AS CategoryId,
		c.NAME AS CategoryName,
		COUNT(DISTINCT p.PRODUCT_ID) AS NumberOfProductsPerCategory,
		ROUND(AVG(p.RATING), 1) AS AverageRating,
		ROUND(SUM(pi.Quantity * pi.PRICE), 1) AS TotalSalesPerCategory
	FROM CATEGORY c
	INNER JOIN PRODUCT p ON c.CATEGORY_ID = p.CATEGORY_ID
	INNER JOIN PRODUCT_ITEM pi ON p.PRODUCT_ID = pi.PRODUCT_ID
	INNER JOIN product_option_variation pov ON pi.PRODUCT_ITEM_ID = pov.PRODUCT_ITEM_ID
	INNER JOIN ORDER_ITEM oi ON pov.PRODUCT_OPTION_VARIATION_ID = oi.PRODUCT_OPTION_VARIATION_ID
	INNER JOIN `ORDER` o ON oi.ORDER_ID = o.ORDER_ID
	WHERE o.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'
	GROUP BY c.CATEGORY_ID
) AS tmp1
INNER JOIN
	(
	SELECT
		a.CategoryId AS CategoryId,
		b.ProductId As ProductId,
		b.ProductName As ProductName,
		a.TopSalesOfProduct AS TopSalesOfProduct
	FROM
		(SELECT
			Sub.CategoryId,
			Sub.CategoryName,
			MAX(Sub.TotalSalesPerProduct) as TopSalesOfProduct
		FROM
			(SELECT
				c.CATEGORY_ID as CategoryId,
				c.name as CategoryName,
				p2.PRODUCT_ID,
				ROUND(SUM(pi2.Quantity * pi2.PRICE), 1) as TotalSalesPerProduct
			FROM CATEGORY c
			INNER JOIN PRODUCT p2 ON c.CATEGORY_ID = p2.CATEGORY_ID
			INNER JOIN PRODUCT_ITEM pi2 ON p2.PRODUCT_ID = pi2.PRODUCT_ID
			INNER JOIN PRODUCT_OPTION_VARIATION pov2 ON pi2.PRODUCT_ITEM_ID = pov2.PRODUCT_ITEM_ID
			INNER JOIN ORDER_ITEM oi2 ON pov2.PRODUCT_OPTION_VARIATION_ID = oi2.PRODUCT_OPTION_VARIATION_ID
			INNER JOIN `ORDER` o2 ON oi2.ORDER_ID = o2.ORDER_ID
			WHERE o2.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'
			GROUP BY c.CATEGORY_ID, p2.PRODUCT_ID
			) as Sub
		GROUP BY Sub.CategoryId
		) a
	INNER JOIN
		(SELECT
			c.CATEGORY_ID as CategoryId,
			c.name as CategoryName,
			p2.PRODUCT_ID as ProductId,
			p2.name as ProductName,
			ROUND(SUM(pi2.Quantity * pi2.PRICE), 1) as TopSalesOfProduct
		FROM CATEGORY c
		INNER JOIN PRODUCT p2 ON c.CATEGORY_ID = p2.CATEGORY_ID
		INNER JOIN PRODUCT_ITEM pi2 ON p2.PRODUCT_ID = pi2.PRODUCT_ID
		INNER JOIN PRODUCT_OPTION_VARIATION pov2 ON pi2.PRODUCT_ITEM_ID = pov2.PRODUCT_ITEM_ID
		INNER JOIN ORDER_ITEM oi2 ON pov2.PRODUCT_OPTION_VARIATION_ID = oi2.PRODUCT_OPTION_VARIATION_ID
		INNER JOIN `ORDER` o2 ON oi2.ORDER_ID = o2.ORDER_ID
		WHERE o2.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'
		GROUP BY c.CATEGORY_ID, p2.PRODUCT_ID
			) b
		ON a.CategoryId = b.CategoryId AND a.TopSalesOfProduct = b.TopSalesOfProduct
	) AS tmp2
ON tmp1.CategoryId = tmp2.CategoryId
ORDER BY tmp1.CategoryId
```
https://github.com/Doohwancho/ecommerce/blob/4e978a6279c639991811bc4628dc5bfb0a2bbea4/back/ecommerce/src/main/java/com/cho/ecommerce/domain/order/repository/OrderRepository.java#L11-L97


## c. sql tuning
[b. 통계 쿼리](#b-통계-쿼리)를 튜닝해보자.

### 1. before tuning
[b. 통계 쿼리](#b-통계-쿼리)"는 크게 3덩이의 subquery로 나뉜다.
1. tmp1
2. a
3. b

#### 1-1. subquery 'a' 실행
![](documentation/images/sql-tuning-before-3.png)
이 부분은 가장 처음에 실행되는 쿼리로, 'a' subquery이다.

문제점: 1000개 row가 있는 order 테이블을 fullscan 하는걸 볼 수 있다.

#### 1-2. subquery 'tmp1' 실행
![](documentation/images/sql-tuning-before-4.png)

문제점: where절 조건이 인덱스를 타지 않아서 풀스캔 한다.


#### 1-3. subquery 'b' 실행
![](documentation/images/sql-tuning-before-5.png)
문제점: **where절 조건이 인덱스를 안타서 풀스캔을 한다.**


#### 1-4. query statistics
![](documentation/images/sql-tuning-before-1.png)

총 비용(mysql workbench의 cost 계산 툴 기준): 170,763

- 문제
    1. 풀 테이블 스캔을 5번이나 하고,
    2. index를 전혀 안탄다.

- 해결책
	- where절에 인덱스를 태워서 성능튜닝을 해보자..!

### 2. WHERE절 조건의 ORDER_DATE 컬럼에 인덱스 적용하기

#### 2-1. 인덱스 만들고 적용하기

1. 인덱스를 만들고,
```java
@Entity
@Table(
    name = "`ORDER`",
    indexes = {
        @Index(name = "idx_order_date", columnList = "ORDER_DATE")
    }
)
@Getter
@Setter
public class OrderEntity {
    //...
}
```

2. 그냥 실행시켰더니, optimizer가 index를 타지 않아서, 타게하도록 힌트를 준다.
```sql
INNER JOIN `ORDER` o2 USE INDEX (idx_order_date) ON oi2.ORDER_ID = o2.ORDER_ID
WHERE o2.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'
```

#### 2-2. 결과
![](documentation/images/sql-tuning-after-4.png)

여전히 subquery해서 나온 결과물을 담은 tmp table을 두번 full scan하긴 하지만,\
"idx_order_date" 인덱스를 index range scan을 3번 타는걸로 바뀌었다.

그런데 수상하게 full scan타는 rows 수가 1200개에서 2660개로 늘어났다???


![](documentation/images/sql-tuning-after-1.png)

인덱스 적용했더니, 맨 처음 order table(1000 rows)에서 where절에 date 인덱스 태웠기 때문에 242 rows만 읽는걸 확인할 수 있다.

여기까진 좋았다.

그런데, 문제는 이 이후부터인데,

첫 테이블만 5000 rows -> 242 rows로 줄었고, 이후에 join할 때마다 읽는 rows수가 1.2k rows -> 2.6k rows로 늘었다.\
그런데 join을 여러번 하니까, 결과적으로 총 읽은 rows수의 양이 122k rows -> 266.6k rows로 늘었다.

나머지 subquery들도 첫번째 subquery와 같은 현상이 일어났다.

![](documentation/images/sql-tuning-after-2.png)
![](documentation/images/sql-tuning-after-3.png)

**총 읽은 rows수가 튜닝 전에는 170,763 rows 이었는데, 튜닝 후에 오히려 266,600 rows로 오히려 늘었다??**

첫 테이블 읽는 rows수가 5000 rows(full scan) 에서 인덱스 태워서 242 rows 만 읽은건 이해가 가는데,

왜 nested join loop에서 read하는 rows가 늘어나서 결과적으로는 성능이 떨어졌을까?


#### 2-3. 실행계획 뜯어보기

##### 2-3-1. date 인덱스 타기 전
![](documentation/images/sql-tuning-before-1.png)
id6 부분이 subquery 'a' 부분이다.

- 실행순서
	1. order table(1000 rows)를 full scan하면서, where절에 date를 태워서 필터한다. (약 200 rows정도 나옴)
	2. 1의 결과로 나온 order table의(200 rows)를 order item table(5000 rows)와 inner nested join하는데, inner table은 order item table이 되고, order item table이 FK로 가지고 있던 Order table의 PK를 인덱스 삼아 조인한다.
		- 이 때, where절 조건인 6개월에 걸리는 order item table의 rows 수는 약 1.2k rows(out of 5k)가 된다.
		- 이 1.2k rows from order item table이, 튜닝 전, 5k rows full scan 이후 nested join 때 반복되는 1.2k 숫자가 나온 이유이다.
	3. 해당 1.2k rows는, 다른 테이블과 nested join with pk 시 반복된다.

![](documentation/images/sql-tuning-before-3.png)

이제 nested loop join시 1.21k rows가 나온 이유가 설명되었다.

```sql
explain analyze select count(*)
from `order_item` oi
INNER JOIN `ORDER` o IGNORE INDEX(idx_order_date) ON oi.ORDER_ID = o.ORDER_ID
INNER JOIN PRODUCT_OPTION_VARIATION pov ON oi.PRODUCT_OPTION_VARIATION_ID = pov.PRODUCT_OPTION_VARIATION_ID
WHERE o.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'
```

약식 쿼리로,\
order, order item, product option variation 테이블만 떼어내서 index 없이 조인하는 쿼리의 실행계획으로 뜯어보자.

```
-> Aggregate: count(0)  (cost=1082.54 rows=1) (actual time=19.867..19.867 rows=1 loops=1)
    -> Nested loop inner join  (cost=960.08 rows=1225) (actual time=2.657..19.746 rows=1235 loops=1)
        -> Nested loop inner join  (cost=531.49 rows=1225) (actual time=2.162..15.960 rows=1235 loops=1)
            -> Filter: (o.order_date between '2023-06-01' and '2023-12-31')  (cost=102.90 rows=111) (actual time=0.065..5.557 rows=247 loops=1)
                -> Table scan on o  (cost=102.90 rows=1002) (actual time=0.060..4.873 rows=1002 loops=1)
            -> Filter: (oi.product_option_variation_id is not null)  (cost=2.76 rows=11) (actual time=0.025..0.041 rows=5 loops=247)
                -> Index lookup on oi using FKs234mi6jususbx4b37k44cipy (order_id=o.order_id)  (cost=2.76 rows=11) (actual time=0.025..0.040 rows=5 loops=247)
        -> Single-row covering index lookup on pov using PRIMARY (product_option_variation_id=oi.product_option_variation_id)  (cost=0.25 rows=1) (actual time=0.003..0.003 rows=1 loops=1235)
```

- 실행순서
	1. Table scan on o  (cost=102.90 rows=1002) (actual time=0.060..4.873 rows=1002 loops=1)
		- order table(1000 rows)를 full scan하여
	2. Filter: (o.order_date between '2023-06-01' and '2023-12-31')  (cost=102.90 rows=111) (actual time=0.065..5.557 rows=247 loops=1)
		- where절 조건에 맞는 247 rows를 추출한다.
	3. Index lookup on oi using FKs234mi6jususbx4b37k44cipy (order_id=o.order_id)  (cost=2.76 rows=11) (actual time=0.025..0.040 rows=5 loops=247)
		- 이제 order item table을 nested loop inner join하는데, inner table 삼아, order item table에 order table의 PK를 FK 인덱스로 가지고 있던걸 한번 join당 5번씩 index tree를 읽는걸, 총 247번(outer table인 order table)만큼 하여 ...
	4. Nested loop inner join  (cost=531.49 rows=1225) (actual time=2.162..15.960 rows=1235 loops=1)
		- 총 1235 rows(5 rows * 247 loops)를 읽어 order table과 order item table을 조인한다.
	5. Single-row covering index lookup on pov using PRIMARY (product_option_variation_id=oi.product_option_variation_id)  (cost=0.25 rows=1) (actual time=0.003..0.003 rows=1 loops=1235)
		- product option variation table과는 pk를 인덱스 삼아 1 rows(pk니까 유니크하다) * 1235rows (step 4까지 order + order item table 조인한 rows 수) 만큼 rows를 읽는다



##### 2-3-2. date 인덱스 태운 이후
![](documentation/images/sql-tuning-after-4.png)
이번에도 id6가 subquery 'a'에 해당한다.

저 2.6k rows read는 대체 어디서 나온걸까?

![](documentation/images/sql-tuning-after-1.png)

nested loop join 할 때마다 2.6k rows를 읽는다는데,\
저래서 총 rows read 비용이 1.5배 이상 늘었는데, 저 2.6k rows라는 숫자는 어디서 튀어나온걸까?

```sql
select count(*)
from `order_item` oi
INNER JOIN `ORDER` o USE INDEX(idx_order_date) ON oi.ORDER_ID = o.ORDER_ID
INNER JOIN PRODUCT_OPTION_VARIATION pov ON oi.PRODUCT_OPTION_VARIATION_ID = pov.PRODUCT_OPTION_VARIATION_ID
WHERE o.ORDER_DATE BETWEEN '2023-06-01' AND '2023-12-31'
```
약식 쿼리를 만들어 실행계획을 뜯어보자!


```
-> Aggregate: count(0)  (cost=2238.76 rows=1) (actual time=21.364..21.367 rows=1 loops=1)
    -> Nested loop inner join  (cost=1972.56 rows=2662) (actual time=9.620..21.243 rows=1235 loops=1)
        -> Nested loop inner join  (cost=1040.86 rows=2662) (actual time=8.423..17.201 rows=1235 loops=1)
            -> Index range scan on o using idx_order_date over ('2023-06-01 00:00:00' <= order_date <= '2023-12-31 00:00:00'), with index condition: (o.order_date between '2023-06-01' and '2023-12-31')  (cost=109.16 rows=242) (actual time=0.734..3.069 rows=247 loops=1)
            -> Filter: (oi.product_option_variation_id is not null)  (cost=2.75 rows=11) (actual time=0.047..0.056 rows=5 loops=247)
                -> Index lookup on oi using FKs234mi6jususbx4b37k44cipy (order_id=o.order_id)  (cost=2.75 rows=11) (actual time=0.047..0.056 rows=5 loops=247)
        -> Single-row covering index lookup on pov using PRIMARY (product_option_variation_id=oi.product_option_variation_id)  (cost=0.25 rows=1) (actual time=0.003..0.003 rows=1 loops=1235)
```

- 실행순서
	1. Index range scan on o using idx_order_date over ('2023-06-01 00:00:00' <= order_date <= '2023-12-31 00:00:00'), with index condition: (o.order_date between '2023-06-01' and '2023-12-31')  (cost=109.16 rows=242) (actual time=0.734..3.069 rows=247 loops=1)
		- order table(1000 rows)를 where절의 조건으로 index scan해서 247 rows만 읽는다.
	2. Index lookup on oi using FKs234mi6jususbx4b37k44cipy (order_id=o.order_id)  (cost=2.75 rows=11) (actual time=0.047..0.056 rows=5 loops=247)
		- order item table과 Order table을 join하기 위해, order item table에서 보관하던 fk를 11 rows 읽고, nested loop inner join시, inner table인 order item table(5000 rows)를 평균 5 rows씩 247번 loop하여 조인한다.
	3. Nested loop inner join  **(cost=1040.86 rows=2662)  (actual time=8.423..17.201 rows=1235 loops=1)**
		- 1235 rows는 step2에서 nested loop join시 fk index를 평균 5rows 씩 247번 loop하여 조인한 것의 결과이다.
		- **오해했던 점은, mysql workbench에 explain visualize에서 나오던 2.6k rows를 읽는다는건, 그저 optimizer의 추정치였을 뿐, 실제 읽은 rows는 1235 rows였다!**
	4. Single-row covering index lookup on pov using PRIMARY (product_option_variation_id=oi.product_option_variation_id)  (cost=0.25 rows=1) (actual time=0.003..0.003 rows=1 loops=1235)
		- order + order item table이 조인됬는데, 다음으로 조인할 product_option_variation table은 pk로 조인하므로, 1조인 당 1개 rows씩 총 1235 loop하여 inner nested loop join을 한다.


- 결론
	1. **mysql workbench에 visual explain에서 나오는 rows read는 추정치일 뿐이라 그대로 믿으면 안된다.**
	2. 실제 실행계획 수치는 mysql console에서 commandline인 'explain analyze'을 쳐서 실측치를 봐야한다.

#### 2-4. 검증
[b. 통계 쿼리](#b-통계-쿼리)를 다시 돌리되,\
데이터 사이즈를 키워서 index 타는 쿼리와 타지 않는 쿼리가 시간차가 얼마나 나는지 보자.

```
테이블 사이즈

user: 10000 rows
order: 10000 rows
orderItem: 50000 rows
product: 10000 rows
productItem: 30000 rows
productOptionVariation: 30000 rows
```
##### case1) where절에 index를 안태운 쿼리: 1027ms
![](documentation/images/sql-tuning-after-5.png)


##### case2) where절에 인덱스를 태운 쿼리: 572ms
![](documentation/images/sql-tuning-after-6.png)


하나의 컬럼에 index를 태웠는지 여부가 약 455ms latency 차이를 보여준다.




# G. 기술적 도전 - Cloud

## a. docker
- 문제
	- 단일 repository의 monolith app 개발 협업 시, 개발자 컴퓨터의 개발환경이 다 다르다.
- 해결책
	- docker로 원하는 개발환경에 원하는 버전의 앱을 가져와 설치할 수 있다.

example) docker-compose.yml
https://github.com/Doohwancho/ecommerce/blob/eb5bd55849a3e3d9460860a6836ce5d94deb2528/docker-compose.yml#L1-L137


## b. aws
- 사용 기술 리스트
	1. load balancer + ec2 + rds - 3 tier architecture
	2. elastic cache - authentication 목적
	3. prometheus + grafana가 설치된 ec2 - WAS 서버 모니터링 목적
	4. MFA 인증 로그인
	5. aws-cli
	6. aws-nuke

## c. provisioning with terraform

### 1. 문제
1. aws 서버 구성하고 한달동안 쓰지도 않았는데 10만원이 청부되었다.
2. 그래서 서버를 다 껐는데 ec2를 꺼도 elastic ip나 ebs같은 서비스가 남아서 요금이 청구되었다.
3. 다시 aws 서버를 구축하려니 처음에 메모한거 한땀한땀 다시 따라하기가 번거로웠다.

### 2. 해결책
1. 명령어 한번에 클라우드가 구성되고, 전부 제거되는 툴을 찾다보니 terraform이라는 provisioning tool을 찾아 적용하게 되었다.
2. terraform 파일 작성시 막혔을 땐, 직접 수동으로 aws를 구성하고 terraforming이라는 오픈소스를 이용해 terraform 파일로 import해서 참조했다.
3. 대규모 트래픽 테스트에서 인스턴스의 스펙별로 스트레스 테스트를 할 때, provisioning tool이 유용했다.
	- 원래였다면 테스트 케이스마다 수동으로 ec2 내리고 다른 스펙으로 만들고 security group, elastic ip, ebs 등 매번 재설정 해야 했다.
	- 그런데 terraform을 쓰면, ec2.tf 파일에 instance = "t2.micro" -> "m2.small"로 바꾸고 terraform apply하면 끝난다.
4. 테라폼도 코드 형식이기 때문에, git에 버전관리가 된다는 이점도 존재한다.


## d. prometheus and grafana
![](documentation/images/prometheus-grafana.png)

spring WAS 서버의 APM을 prometheus + grafana로 한다.

## e. 대용량 트래픽 처리

# H. 기술적 도전 - Frontend

## a. 프로젝트 구조

```
public
├── images
models
├── src
│   └── model
src
├── assets
│   └── styles
├── components
│   ├── Footer
│   │   └── styles
│   ├── Header
│   │   ├── hooks
│   │   └── styles
│   └── TopNav
│       ├── hooks
│       ├── modal
│       │   ├── styles
│       │   ├── type
│       │   └── util
│       ├── styles
│       └── util
├── hooks
├── pages
│   ├── authentication
│   │   ├── login
│   │   │   ├── constants
│   │   │   ├── hooks
│   │   │   ├── styles
│   │   │   └── types
│   │   └── register
│   │       ├── constants
│   │       ├── hooks
│   │       └── styles
│   └── product
│       ├── Category
│       │   ├── hooks
│       │   ├── service
│       │   ├── styles
│       │   ├── types
│       │   └── ui
│       ├── Home
│       │   ├── component
│       │   │   └── carousel
│       │   │       ├── service
│       │   │       ├── styles
│       │   │       └── types
│       │   └── styles
│       └── Product
│           ├── component
│           │   ├── discount
│           │   ├── productDetails
│           │   └── productImages
│           ├── hooks
│           ├── service
│           ├── styles
│           ├── types
│           └── util
└── store
```

1. 도메인 컴포넌트들을 도메인 별로 구분하여 page/에서 관리한다.
2. Header, Footer과 같은 공통 컴포넌트들은 component/에서 관리한다.
3. 각 컴포넌트마다 style, types, service, custom hooks, ui, component, util, constant를 폴더로 나눠 관리한다.
4. 공통 custom hooks들은 hooks/에서 관리한다.
5. openapi-codgen으로 생성된 모델 DTO들은 models/src/model/에 생성된다.




## b. component 모듈화

ex. `<Product />`를 모듈화 한 방법

```
├── Product.tsx
├── component
│   ├── discount
│   │   └── Discount.tsx
│   ├── productDetails
│   │   └── ProductDetail.tsx
│   └── productImages
│       ├── ProductImages.styles.ts
│       └── ProductImages.tsx
├── hooks
│   └── useProductData.ts
├── service
│   └── ProductService.ts
├── styles
│   └── Product.styles.ts
├── types
│   └── Product.types.ts
└── util
    └── Product.util.ts
```

## c. state managment

1. react query
	- server state를 관리한다.
	- custom hooks에 react query의 fetch 함수와 더불어, 각 페이지에 맞게 가공하여 전달하는 함수까지 포함한다.
2. recoil
	- client state를 관리한다.
	- global state에 담아 관리해야할 것을(ex. user authentication status) recoil로 관리한다.
3. props
	- 가능한 depth 1 정도만 props를 내려준다. 그 이상 depth는 recoil 사용을 고려한다. (props drilling problem)
	- ex. `<ProductCard />`같이 loop 돌면서 값을 내려줘야 하는 경우


## d. API first design

### 1. 문제
1. 기존 프론트/백 협업 방식은 프론트 개발자와 백엔드 개발자 사이의 결합도가 높아진다는 문제점이 있다.
	- 기존에 frontend, backend 협업 시, 코드를 각자 짜면서 슬랙으로 프론트가 백 한테 필요 데이터를 매번 요청하는 식으로 일했다.
	- 프론트 개발자의 요구사항이 수시로 바뀌는 경우, 백엔드 개발자도 그에 맞춰서 엔드포인트를 계속 수정해야 하는데, 이는 일의 효율을 저해한다.
2. API endpoint 변경시, 누가 언제 어느 목적으로 추가/변경/삭제했는지 버전관리 하기 힘들다.
3. API endpoint를 정의하는 사내 프로토콜의 부재

### 2. 문제의 원인
- API 공통 프로토콜의 부재


### 3. 해결책
1. API 공통 프로토콜인 openapi을 사용한다.
2. API first approach을 사용해 프론트/백이 코드 작성 전에, 서버에 요청되는 request/response를 미리 합의해 정해두고, openapi 문서를 작성한다.
3. openapi spec에 맞추어 작성된 문서를 코드로 변환해주는 SDK(openapi-codegen)을 사용하여 프론트는 request, response에 필요한 모델을, 백엔드는 컨트롤러 코드를 자동으로 생성해 사용한다.
4. API를 읽는 문서는 redoc이라는 오픈소스 툴을 사용한다.




#### 3-1. openapi codegen

![](documentation/images/swagger.png)

openapi3 spec으로 작성된 코드를 swagger로 변환해준 모습

- Q. how to see oepnapi docs online?
    1. https://editor.swagger.io/
    2. [openapi-docs code](https://github.com/Doohwancho/ecommerce/blob/main/back/ecommerce/src/main/resources/api/openapi.yaml) 붙여넣기


#### 3-2. redoc
![](documentation/images/redoc.png)

```
Q. how to install redoc and run?

npm i -g @redocly/cli
git clone https://github.com/Doohwancho/ecommerce
cd ecommerce
redocly preview-docs back/ecommerce/src/main/resources/api/openapi.yaml
```




## e. filter 기능 구현

![filter-demo](documentation/images/filter-demo.gif)




## f. 페이지 로드 속도 개선

### 1. 불필요한 랜더링을 React.memo() 으로 최적화

- 문제
	- 페이지 이동할 때 마다 `<Header />, <Footer />, < TopNav />`가 불필요하게 다시 랜더링 되던 문제가 있었다.
- 해결책
	1. React.memo()로 감싸서 props가 바뀌지 않는한, 다시 랜더링 되지 않도록 하고,
	2. Router에서 템플릿화 시켰다.

```tsx
  const Layout = ({ children }) => (
    <>
      <Header />
      <TopNav />
      <ScrollToTop />
      {children}
      <Footer />
    </>
  );

<Routes>
	<Route path="/" element={<Layout><Home /></Layout>} />
	<Route path="/products/category/:lowCategoryId" element={<Layout><Category /></Layout>} />
	<Route path="/products/:productId" element={<Layout><Product /></Layout>} />
</Routes>
  );
```

---

### 2. useMemo()로 memoization 활용

1. API fetch받은 products들을 재정리 하는 함수의 결과값을 memoization 한다.

https://github.com/Doohwancho/ecommerce/blob/ee47f915de501e7142f4fc17b7abd46549ac750e/front/ecommerce/src/pages/product/Category/hooks/useCategoryData.ts#L23-L56

option/price filter에서 product list를 호출할 때마다, 재정리를 요구하는데,
이 함수를 useMemo()로 최적화 했다.


---
...하지만 option들을 묶는 함수에 적용한 useMemo()는 이른 최적화 같다.

2. option들을 optionId를 기준으로 묶는 함수
https://github.com/Doohwancho/ecommerce/blob/ee47f915de501e7142f4fc17b7abd46549ac750e/front/ecommerce/src/pages/product/Category/hooks/useCategoryData.ts#L9-L21

- Q. 왜 useMemo()를 여기에 쓰는게 좋은 선택이 아닌가?
	1. 무거운 연산이 아니다.
	2. 파라미터가 자주 바뀌는 편이라, 한번 연산해놓고 두고두고 쓰는 함수가 아니다.




### 3. code splitting

```tsx
import React, { Suspense } from 'react';

const Home: React.FC = () => {
  const CarouselComponent = React.lazy(() => import ('./component/carousel/CarouselComponent'));

  return (
    <>
	<MainElement /> //------------------- 1

	<Suspense fallback={<div>Loading...</div>}>
          <CarouselComponent /> //----------------- 2
        </Suspense>
    </>
  );
};

export default Home;
```
1. 메인페이지 최상단 이미지 + 텍스트는 그대로 랜더링
2. 화면 하단부 top 10 rated products fetch는 lazy하게 랜더링


### 4. main page caching

![top-ten-rated-products](documentation/images/top-ten-rated-products.gif)

main page에서 요구하는 top 10 rated products를 redis cache에 매 시간 갱신하여 뿌려준다.

https://github.com/Doohwancho/ecommerce/blob/ee47f915de501e7142f4fc17b7abd46549ac750e/back/ecommerce/src/main/java/com/cho/ecommerce/global/config/redis/RedisConfig.java#L51-L61

https://github.com/Doohwancho/ecommerce/blob/ee47f915de501e7142f4fc17b7abd46549ac750e/back/ecommerce/src/main/java/com/cho/ecommerce/domain/product/repository/ProductRepositoryCustomImpl.java#L116-L126



### 5. .png -> .webp로 변경
이미지 용량이 약 60%로 축소됨으로 인해, 페이지 로드 속도가 빨라졌다.




# I. Trouble Shooting

## a. queryDSL library와 openapi-codegen이 컴파일시 깨지는 문제 해결

### 1. 문제
1. 스프링 + openapi-codgen library도 잘 동작하고,
2. 스프링 + queryDSL로 잘 동작하는데,
3. 둘을 동시에 쓰면 빌드가 안되는 현상 발생

### 2. 문제 원인
queryDSL과 openapi-codegen 둘 다 빌드시 코드를 동적으로 생성하는데,\
compileQuerydsl시 아직 컴파일 안된 openapi-codegen 코드부분 때문에 에러 발생

### 3. 해결책
1. gradle 빌드 순서를 openapi-codegen 컴파일이 먼저 실행되고,
2. queryDSL이 다음에 실행되고,
3. 마지막으로 compileJava가 실행되도록 변경했다.

https://github.com/Doohwancho/ecommerce/blob/73ddd650c20ca7349cdbf3d992ca1fe357c67da4/back/ecommerce/build.gradle#L143-L145


### 4. 이 트러블 슈팅이 기억에 남는 이유
기능 만들고 싶은데 어떻게 만드는지 모르거나, 프레임워크에서 에러나는건 \
배워서 만들거나, googling, issue 탭 찾아보면 되는 일인데,

외부 라이브러리들 끼리 서로 궁합이 안맞아서 빌드 깨지는건,\
구글링 해도 안나오고, 이슈 탭에서도 없고,\
라이브러리를 까봐야 하나? 라는 생각이 들어도 querydsl은 22만줄, openapi-generator-cli는 2만 1천줄인데,\
querydsl나 openapi-codegen에서 문제가 생긴게 아니라,\
얘네들이 의존하는 다른 라이브러리에서 오류난 걸 수도 있으니까

막연한 절망감(?) 속에서 gradle 문서 보면서 이런 저런 시도를 하는데,\
논리상 되야되는데 안될 때마다 '이 라이브러리 쓰고싶은데 안써야 하나?'\
하다가 삽질 끝에 되게 해서 기억에 남습니다..


## b. 그 외 trouble shooting 커밋 내역들
```
Q. how to find all trouble shooting list?

1. git clone https://github.com/Doohwancho/ecommerce
2. cd ecommerce
3. git log --grep="fix"
```
