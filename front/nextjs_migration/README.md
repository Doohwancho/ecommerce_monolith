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
    - list * n
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
        1. filter * n
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

