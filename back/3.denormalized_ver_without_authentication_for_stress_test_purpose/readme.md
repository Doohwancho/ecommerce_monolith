# A. Goal of this app
table size가 100,000+ 일 때,\
정규화되어 쪼개진 테이블들을 join 할 때 생기는 cpu usage cost를 낮추기 위해,\
테이블들을 최대한 반정규화 하여 다시 만든 ecommerce app 

# B. what's different from back/1.ecommerce?
1. 반정규화되었음 (product, user, order 3 tables)
2. heavy-query를 cache할 때 caffeine이라는 외부 라이브러리 도입

# C. APIs

1. /products
    1. GET fetch all products that belongs to categoryId with pagination
    2. GET product_details by productId
    3. GET products with highest ratings (cached, reset every hour)
2. /users
3. /orders