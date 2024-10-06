import http from 'k6/http';
import { sleep } from 'k6';

/**
  A. what is this?
  read:write 비율 9:1인 load-test script 

  B. how to run?
  1. on local
    - docker run --rm -i --net=host grafana/k6 run - <./k6/1.load_test_정규화된_backend_server/load-9to1_read_write_ratio_scenario_load_test.js
  2. on ec2 server 
    - sudo docker run --rm -i grafana/k6 run - <./k6/1.load_test_정규화된_backend_server/load-9to1_read_write_ratio_scenario_load_test.js

  C. how to configure?
  1. BASE_URL을 엔드포인트 url로 설정
  2. BULK_INSERT_BASE_AMOUNT는 ${endpoint}/bulkinsert/${value} 한 양 만큼 입력
  3. options에 target에 RPS값 설정
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
      http_req_failed: ['rate<0.1'], // 10% or less error rate allowed
      http_req_duration: ['p(95)<5000'], // 95%의 요청이 5초 이내에 완료되어야 함
    },
    setupTimeout: '370s', //setup() 때 GET /discounts, GET /users 시간이 오래걸려서 timeout 시간을 3분정도로 설정한다.
    // http_req_timeout: '30s',
}


// const BASE_URL = 'http://host.docker.internal:8080';
const BASE_URL = 'http://13.209.179.59:8080';
const BULK_INSERT_BASE_AMOUNT = 1000;

export function setup() {
  let discounts = [];
  let usernames = [];

  const discountsResponse = http.get(`${BASE_URL}/discounts`, { timeout: "120s"});
  if (discountsResponse.status === 200) {
    discounts = JSON.parse(discountsResponse.body);
  } else {
    console.error(`Failed to fetch discounts. Status: ${discountsResponse.status}`);
  }

  const usersResponse = http.get(`${BASE_URL}/users`, { timeout: "240s" });
  if (usersResponse.status === 200) {
    const users = JSON.parse(usersResponse.body);
    usernames = users.map(user => user.name);
  } else {
    console.error(`Failed to fetch users. Status: ${usersResponse.status}`);
  }

  // sleep(300);  //options 안에 setupTimeout이 있으면 이 설정 필요없다.
                  //bulkinsert/100000 기준, GET /users 만 response.size가 40만줄 나옴. /discounts 도 response 사이즈만 44.1Mb이고.
  return { discounts, usernames };
}

function getProductsByCategory() {
  const categoryId = Math.floor(Math.random() * (75 - 16 + 1)) + 16;
  const url = `${BASE_URL}/products/category/v2/${categoryId}`;
  const response = http.get(url);
  if (response.status !== 200) {
    console.error(`getProductsByCategory failed. Status: ${response.status}, Body: ${response.body}`);
  }
}

function getProductById() {
  const productId = Math.floor(Math.random() * BULK_INSERT_BASE_AMOUNT) + 1;
  const url = `${BASE_URL}/products/${productId}`;
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

function getOrderItemsByUsername(usernames) {
  const username = usernames[Math.floor(Math.random() * usernames.length)];
  const url = `${BASE_URL}/orders/orderItems/${username}`;
  const response = http.get(url);
  if (response.status !== 200) {
    console.error(`getOrderItemsByUsername failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function getSalesStatistics() {
  const months = Math.floor(Math.random() * 3) + 1; //1~3
  const url = `${BASE_URL}/orders/statistics/sales/${months}`;
  const response = http.get(url);
  if (response.status !== 200) {
    console.error(`getSalesStatistics failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function getUserByUsername(usernames) {
  const username = usernames[Math.floor(Math.random() * usernames.length)];
  const url = `${BASE_URL}/users/${username}`;
  const response = http.get(url);
  if (response.status !== 200) {
    console.error(`getUserByUsername failed with status: ${response.status}, Body: ${response.body}`);
  }
}

function createOrder(discounts) {
  const userId = Math.floor(Math.random() * BULK_INSERT_BASE_AMOUNT) + 1;
  const discountId = discounts[userId - 1]?.discountId;
  const productItemId = userId;
  const productOptionVariationId = userId;

  const discount = discounts.find((d) => d.discountId === discountId);

  if (discount) {
    const payload = [{
      memberId: userId,
      productItemId: productItemId,
      orderQuantity: 1,
      discountId: discountId,
      discountType: discount.discountType,
      discountValue: discount.discountValue,
      startDate: discount.startDate,
      endDate: discount.endDate,
      productOptionVariationId: productOptionVariationId,
    }];

    const response = http.post(`${BASE_URL}/orders`, JSON.stringify(payload), {
      headers: { 'Content-Type': 'application/json' },
    });

    if (response.status !== 201) {
      console.error('Order creation failed with status:', response.status);
    }
  } else {
    console.warn(`Discount not found for discountId: ${discountId}`);
  }
}

export default function ({ discounts, usernames }) {
  const readWeight = 0.9;
  const writeWeight = 0.1;

  const random = Math.random();
  if (random < readWeight) {
    // Read requests (90%)
    const readRandom = Math.random();
    if (readRandom < 0.1) {
      getProductsByCategory();
    } else if (readRandom < 0.4) {
      getProductById();
    } else if (readRandom < 0.7) {
      getHighestRatedProducts();
    } else if (readRandom < 0.9) {
      getOrderItemsByUsername(usernames);
    // } else if (readRandom < 0.8) {
    //   getSalesStatistics(); //실험의 공평성을 위해 statistics-query를 삭제. 반정규화 k6 load_script에서도 통계쿼리는 없다.
    } else {
      getUserByUsername(usernames);
    }
  } else {
    // Write requests (10%)
    createOrder(discounts);
  }

  sleep(1);
}