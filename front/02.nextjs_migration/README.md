---\
Index

1. atomic design pattern\
2. rendering pattern\
3. app router uri



# A. atomic design pattern

## a. theme 정하기

### 1. theme color

- Q. theme primary color combination for nike ecommerce?
  1. 흰색
  2. 검정색
  3. 회색

### 2. text-style

1. font
2. font-color
3. font-size, in proportion to other component's font-size(rem)
4. bold

### b. atomic design을 나누는 기준 정하기

1. atom
   - 가장 작은 단위의 component
   - ex. input, label, box, etc
2. molecule
   1. atomic component의 조합
   2. domain logic은 들어가면 안된다.
   3. ex. form = label + input + button
3. oraganism
   1. molecule component의 조합
   2. domain logic이 들어갈 수 있다.
   3. ex. ProductCard = box + image + label
4. template
   1. organism component들의 조합
   2. domain logic이 들어갈 수 있다.
   3. ex. ProductCardList = ProductCard + grid
5. page
   1. 1~4의 조합
   2. ex. home page = header + top-menu-bar + image + footer

## c. Q. what atoms & modules from shadcn-ui to build this [wireframe](https://github.com/Doohwancho/ecommerce?tab=readme-ov-file#a-wireframe)?

### c-1. atoms

- theme
- button
- radio
- input
- checkbox
- label
- box
- icon
- link
- image
- spinner

### c-2.molecules

atomic의 조합, domain 포함 X

- list
  - list of text, vertically
- form - username, password, etc
  1. label
  2. input
- modal
  - list \* n
  - data function
- filter
  - text - bold
  - radio
- highlight
  - text - bold
  - text - grey (goal: explain bold text)
- card
  - box
  - ?image
  - ?text | ?input
  - ?button
- counter
  - input
  - button - increase
  - button -decrease
- rating
  - icon - stars
  - text - rating value
- slider - import from library

### c-3. organisms

molecule의 조합, domain과 섞임

- common
  - header
    1. link - register
    2. link - hyperlink to login
  - top-menu-bar
    1. icon
    2. modal
    3. data 뿌려주는 component
  - left menu-bar
    1. filter \* n
  - footer
    1. list
- home page
  - 가능성은 지금부터
    1. highlight
       1. text - bold-font, middle align
       2. text - grey-font, middle align
    2. button - 미션 참여하기
    3. button - 컬렉션 구매하기
  - Top 10 Rated
    1. highlight
       1. text - bold-font, middle align
       2. text - grey-font, middle align
    2. slider
       1. productCard
- productCard
  - card
    - box
    - image
    - text - title
    - text - explanation
    - text - price

### c-4. templates

organism의 조합, domain과 섞임

- product list page
  - product list
    - products in grid
- product page
  - productInfo
    1. productTitle
    2. productInfo
    3. productSize
    4. Button - add to cart
    5. counter - quantity

### c-5. pages

1. home
   1. header
   2. top-menu-bar
   3. image
   4. highlight - "가능성은 지금부터"
   5. Top 10 Rated
   6. footer
2. productList
   1. header
   2. top-menu-bar
   3. left-menu-bar
   4. productItems
   5. footer
3. product
   1. header
   2. top-menu-bar
   3. image
   4. productInfo
   5. footer
4. register
   1. header
   2. highlight - "Welcome-to-nike"
   3. form - username
   4. form - password
   5. button
   6. footer
5. login
   1. header
   2. highlight - "Welcome to Nike"
   3. form - username
   4. form - password
   5. button
   6. footer

# B. Rendering Pattern

## a. redering pattern의 종류

1. SSR (server side rendering)
   - server generates html contents and sent fully rendered-page to client
   - pros:
     1. SEO optimization
     2. faster content delivery
   - cons:
     1. 서버 요청이 많음(ex. 새로고침) -> 서버 부하가 커짐
     2. blinking page -> bad UX compared to SPA
2. SSG (static site generater)
   - static html을 build time때 만들어두고 캐싱해서 뿌리는 것
   - pros
     1. 미리 캐싱해둔걸 뿌리니까 빠름
   - cons
     1. html 내용 수정이 있을 때마다 매번 빌드 + 배포해야 하기 때문에, 내용수정이 있는 페이지는 SSG로 만들면 안됨.
3. CSR (client side rendering)
   - client generates html contents
   - pros:
     1. 첫 로딩만 넘기면 스무스하게 화면 넘어가서 UX가 좋다.(X blinking page)
   - cons:
     1. 초기 로딩 속도가 느리다. -> 유저이탈 가능성
     2. SEO 문제점
4. ISR (incremental static regeneration)
   - SSG같이 캐싱된 html을 뿌리는건데, 매 시간마다 주기적으로 미리 업데이트 + 빌드해놓음 (페이지마다 invalidate time이 존재)

## b. wireframe에 어느 페이지가 있고, 어느 rendering pattern에 적합한가?

1. home page(ISR)
   - 첫 로딩이 빠름(유저이탈 가능성 방지)
   - SEO 문제점 해결(by not using CSR)
   - top 10 recommendation이 주기적으로 바뀌기 때문에 SSG보다는 ISR 선택
   - 그 외, 프로모션 자주해서 메인페이지 업데이트가 잦은 경우에도 ISR을 쓰는게 유리
2. login page (SSG)
   - 로그인 페이지는 모든 웹사이트에서 가장 트래픽이 많은 페이지라고 한다.
   - 따라서 서버 부하를 줄이기 위해 정적 페이지를 캐싱해 놓고 뿌리는 SSG를 선택한다.
3. register page (SSG)
   - 정적 페이지라 굳이 매번 서버에서 html을 만들지 않아도 된다.
   - 어짜피 회원가입 성공하면 로그인 페이지로 리다이렉트 해야하는데, 이걸 굳이 CSR로 만들 필요는 없어보인다.
4. category navbar component(ISG)
   - 카테고리 네비게이션바는 아~주 가끔 변경되니, 미리 캐싱해두고 뿌리는 식으로 하자
5. product list page (SSR over ISR)
   - 쿠팡처럼 상품이 자주 업데이트 되어, 올리는 순간 즉각 반영되야 한다 -> SSR
   - 개인 쇼핑몰인데 상품이 자주 업데이트 되지는 않거나, n일에 한번씩 주기적으로 업데이트 된다 -> ISR
6. product page (SSR)
   - 상품설명을 바뀌면 바로 반영되어야 함 == 높은 데이터 무결성을 요구함 = SSR
   - SEO friendly

# C. App Router URI

1. /
  - home
2. /category/${category_id}
  - product_list
3. /product/${product_id}
  - product
4. /register
  - register
5. /login
  - login