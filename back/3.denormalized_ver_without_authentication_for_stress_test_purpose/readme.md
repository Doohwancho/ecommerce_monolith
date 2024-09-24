# A. Goal of this app
table size가 100,000+ 일 때,\
정규화되어 쪼개진 테이블들을 join 할 때 생기는 cpu usage cost를 낮추기 위해,\
테이블들을 최대한 반정규화 하여 다시 만든 ecommerce app 

# B. what's different from back/1.ecommerce?
1. 반정규화되었음 (product, user, order 3 tables)
2. heavy-query를 cache할 때 caffeine이라는 외부 라이브러리 도입

# C. APIs

1. /products
    1. GET `/products/category/${category_id}`
       - fetch all products that belongs to categoryId with pagination
    2. GET `/products/${product_id}`
       - product_details by productId
    3. GET `/products/highestRatings` 
       - products with highest ratings (cached, reset every hour)
2. /users
   1. GET `/users`
      - get all users
   2. GET `/users/${username}`
      - get specific user_info
3. /orders
   1. GET `/orders/orderItems/${username}`
      - 해당 유저의 주문 리스트 보여주는 쿼리
   2. GET `/orders/statistics/sales/${month}`
      - 최근 n번째 달 주문 통계 쿼리 (n is maximum 3)
   3. POST `/orders/`
      - 주문 접수
      - validation checks
         1. memberName이 Member 테이블에 존재해야 한다.
         2. orderItems에 productName이 products에 존재해야 하고, basePrice와 discountedPrice가 맞아야 한다.
         3. order.quantity가 현재 product table에 quantity를 초과하면 안된다.
      - example)
      ```json
      curl -X POST http://localhost:8080/orders \
        -H "Content-Type: application/json" \
        -d '{
          "memberId": 1,
          "memberName": "John Doe",
          "memberEmail": "john.doe@example.com",
          "orderItems": [
            {
              "productName": "VTzxhFBpsU",
              "quantity": 2,
              "basePrice": 14617.096149058603,
              "discountedPrice": 14456.308091418958
            }
          ],
          "street": "123 Main St",
          "city": "Anytown",
          "state": "CA",
          "country": "USA",
          "zipCode": "12345"
        }'
      ```