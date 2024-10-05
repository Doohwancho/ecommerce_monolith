import http from 'k6/http';
import { sleep } from 'k6';


/**
  A. what is this?
  back/3.반정규화 프로젝트에 스트레스 테스트 하는 스크립트 
  read:write 비율 9:1인 load-test-script

  B. how to run?
  1. on local
    - docker run --rm -i --net=host grafana/k6 run - <./k6/2.load_test_비정규화된_backend_server/load-9to1_read_write_ratio_scenario_load_test.js
  2. on ec2 server 
    - sudo docker run --rm -i grafana/k6 run - <./k6/2.load_test_비정규화된_backend_server/load-9to1_read_write_ratio_scenario_load_test.js
    - sudo docker run --rm -i grafana/k6 run - <./load-9to1_read_write_ratio_scenario_load_test.js

  C. how to configure?
  1. BASE_URL을 엔드포인트 url로 설정
  2. options에 target에 RPS값 설정

  D. what endpoint does it test?
    1. GET `/products/category/${category_id}`
       - fetch all products that belongs to categoryId with pagination
       - categoryId는 16 ~ 75 사이의 값
    2. GET `/products/${product_id}`
       - product_details by productId
       - product_id는 1 ~ ${bulkinsert_base_amount} 사이의 값 
    3. GET `/products/highestRatings` 
       - products with highest ratings (cached, reset every hour)
    4. GET `/users/${username}`
      - get specific user_info
    5. GET `/orders/orderItems/${username}`
      - 해당 유저의 주문 리스트 보여주는 쿼리
    6. POST `/orders/`
      - 주문 신청
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
  E. setup 단계에서 필요한 데이터 fetch 
    1. GET /users
    ```json 
    [
      {
          "memberId": 1,
          "userId": "yuZiCCfTcX",
          "email": "yuZiCCfTcX",
          "name": "yuZiCCfTcX",
          "role": "ROLE_USER",
          "enabled": true,
          "failedAttempt": 0,
          "street": "yuZiCCfTcX",
          "city": "yuZiCCfTcX",
          "state": "yuZiCCfTcX",
          "country": "yuZiCCfTcX",
          "zipCode": "yuZiCCfTcX",
          "created": "2024-09-26T16:57:51.235+09:00",
          "updated": "2024-09-26T16:57:51.235+09:00",
          "accountNonExpired": true,
          "accountNonLocked": true,
          "credentialsNonExpired": true
      },
      ...
    ]
    ```
    2. GET /products
    ```json
    [
      {
          "productId": 1,
          "name": "yuZiCCfTcX",
          "description": "yuZiCCfTcX",
          "rating": 0.0,
          "ratingCount": 100,
          "categoryId": 16,
          "categoryName": "yuZiCCfTcX",
          "totalQuantity": 100000,
          "options": [
              {
                  "name": "yuZiCCfTcX",
                  "values": [
                      "yuZiCCfTcX",
                      "yuZiCCfTcX",
                      "yuZiCCfTcX"
                  ]
              },
              {
                  "name": "yuZiCCfTcX",
                  "values": [
                      "yuZiCCfTcX",
                      "yuZiCCfTcX",
                      "yuZiCCfTcX"
                  ]
              }
          ],
          "discounts": [
              {
                  "type": "PERCENTAGE",
                  "value": 1.0,
                  "startDate": "2024-07-20T04:30:08Z",
                  "endDate": "2024-09-03T04:30:08Z"
              },
              {
                  "type": "PERCENTAGE",
                  "value": 1.0,
                  "startDate": "2024-07-20T04:30:08Z",
                  "endDate": "2024-09-03T04:30:08Z"
              }
          ],
          "basePrice": 524689.4522854341,
          "lowestPrice": 524689.4522854341,
          "highestPrice": 524689.4522854341
      },
      ...
    ]
    ```
 */

export let options = {
    noConnectionReuse: false,
    stages: [
      { duration: '5m', target: 50 },    // Ramp up to 50 RPS over 5 minutes
      { duration: '10m', target: 50 },   // Stay at 50 RPS for 10 minutes
      { duration: '5m', target: 100 },   // Ramp up to 100 RPS over 5 minutes
      { duration: '10m', target: 100 },  // Stay at 100 RPS for 10 minutes
      { duration: '5m', target: 200 },   // Ramp up to 200 RPS over 5 minutes
      { duration: '10m', target: 200 },  // Stay at 200 RPS for 10 minutes
      { duration: '5m', target: 300 },   // Ramp up to 300 RPS over 5 minutes
      { duration: '10m', target: 300 },  // Stay at 300 RPS for 10 minutes
      { duration: '5m', target: 400 },   // Ramp up to 400 RPS over 5 minutes
      { duration: '10m', target: 400 },  // Stay at 400 RPS for 10 minutes
      { duration: '5m', target: 500 },   // Ramp up to 500 RPS over 5 minutes
      { duration: '10m', target: 500 },  // Stay at 500 RPS for 10 minutes
      { duration: '5m', target: 600 },   // Ramp up to 600 RPS over 5 minutes
      { duration: '10m', target: 600 },  // Stay at 600 RPS for 10 minutes
      { duration: '5m', target: 700 },   // Ramp up to 700 RPS over 5 minutes
      { duration: '10m', target: 700 },  // Stay at 700 RPS for 10 minutes
      { duration: '5m', target: 800 },   // Ramp up to 800 RPS over 5 minutes
      { duration: '10m', target: 800 },  // Stay at 800 RPS for 10 minutes
      { duration: '5m', target: 900 },   // Ramp up to 900 RPS over 5 minutes
      { duration: '10m', target: 900 },  // Stay at 900 RPS for 10 minutes
      { duration: '5m', target: 1000 },  // Ramp up to 1000 RPS over 5 minutes
      { duration: '10m', target: 1000 }, // Stay at 1000 RPS for 10 minutes
      { duration: '5m', target: 0 },     // Ramp down to 0 RPS over 5 minutes
  ],
    thresholds: {
      errors: ['rate<0.1'], // 10% 이하의 에러율 허용
      http_req_duration: ['p(95)<5000'], // 95%의 요청이 5초 이내에 완료되어야 함
    },
    setupTimeout: '370s', //setup() 때 GET /discounts, GET /users 시간이 오래걸려서 timeout 시간을 3분정도로 설정한다.
    // http_req_timeout: '30s',
}

// const BASE_URL = 'http://host.docker.internal:8080'; //for local 
const BASE_URL = 'http://3.35.113.61:8080';

export function setup() {
  let users = [];
  let products = [];

  const usersResponse = http.get(`${BASE_URL}/users`, { timeout: "240s" });
  if (usersResponse.status === 200) {
      users = JSON.parse(usersResponse.body);
  } else {
      console.error(`Failed to fetch users. Status: ${usersResponse.status}`);
  }

  const productsResponse = http.get(`${BASE_URL}/products`, { timeout: "240s" });
  if (productsResponse.status === 200) {
      products = JSON.parse(productsResponse.body);
  } else {
      console.error(`Failed to fetch products. Status: ${productsResponse.status}`);
  }

  return { users, products };
}

function getProductsByCategory() {
  const categoryId = Math.floor(Math.random() * (75 - 16 + 1)) + 16;
  const url = `${BASE_URL}/products/category/${categoryId}`;
  const response = http.get(url);
  if (response.status !== 200) {
      console.error(`getProductsByCategory failed. Status: ${response.status}, Body: ${response.body}`);
  }
}

function getProductById(products) {
  const product = products[Math.floor(Math.random() * products.length)];
  const url = `${BASE_URL}/products/${product.productId}`;
  const response = http.get(url);
  if (response.status !== 200) {
      console.error(`getProductById failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function getHighestRatedProducts() {
  const url = `${BASE_URL}/products/highestRatings`;
  const response = http.get(url);
  if (response.status !== 200) {
      console.error(`getHighestRatedProducts failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function getOrderItemsByUsername(users) {
  if (!users || users.length === 0) {
    console.error('No users available for getOrderItemsByUsername');
    return;
  }
  const user = users[Math.floor(Math.random() * users.length)];
  const url = `${BASE_URL}/orders/orderItems/${user.name}`;
  const response = http.get(url);
  if (response.status !== 200) {
      console.error(`getOrderItemsByUsername failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function getUserByUsername(users) {
  if (!users || users.length === 0) {
    console.error('No users available for getUserByUsername');
    return;
  }
  const user = users[Math.floor(Math.random() * users.length)];
  const url = `${BASE_URL}/users/${user.name}`;
  const response = http.get(url);
  if (response.status !== 200) {
      console.error(`getUserByUsername failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function createOrder(users, products) {
  if (!users || users.length === 0 || !products || products.length === 0) {
    console.error('No users or products available for createOrder');
    return;
  }
  const user = users[Math.floor(Math.random() * users.length)];
  const product = products[Math.floor(Math.random() * products.length)];
  
  const payload = {
      memberId: user.memberId,
      memberName: user.name,
      memberEmail: user.email,
      orderItems: [
          {
              productName: product.name,
              quantity: Math.floor(Math.random() * 5) + 1,
              basePrice: product.basePrice,
              discountedPrice: product.lowestPrice
          }
      ],
      street: user.street,
      city: user.city,
      state: user.state,
      country: user.country,
      zipCode: user.zipCode
  };

  // console.log("----------------------payload---------------------")
  // console.log(payload);
  // console.log("-----------------------user---------------------")
  // console.log(user);
  // console.log("----------------------product-----------------------")
  // console.log(product);
  // console.log("--------------------------------------------------")

  const response = http.post(`${BASE_URL}/orders`, JSON.stringify(payload), {
      headers: { 'Content-Type': 'application/json' },
  });

  if (response.status !== 200) {
      console.error('Order creation failed with status:', response.status);
  }
}

export default function (data) {
  const readWeight = 0.9;
  const writeWeight = 0.1;

  const random = Math.random();
  if (random < readWeight) {
      // Read requests (90%)
      const readRandom = Math.random();
      if (readRandom < 0.1) {
          getProductsByCategory();
      } else if (readRandom < 0.4) {
          getProductById(data.products);
      } else if (readRandom < 0.7) {
          getHighestRatedProducts();
      } else if (readRandom < 0.9) {
          getOrderItemsByUsername(data.users);
      } else {
          getUserByUsername(data.users);
      }
  } else {
      // Write requests (10%)
      createOrder(data.users, data.products);
  }

  sleep(1);
}