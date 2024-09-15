import http from 'k6/http';
import { sleep } from 'k6';

/**
  A. what is this?
  read:write 비율 9:1인 load-test script 

  B. how to run?
  docker run --rm -i --net=host grafana/k6 run - <./k6/load_test/load-9to1_read_write_ratio_scenario_load_test.js

  C. how to configure?
  1. BASE_URL을 엔드포인트 url로 설정
  2. BULK_INSERT_BASE_AMOUNT는 ${endpoint}/bulkinsert/${value} 한 양 만큼 입력
  3. options에 target에 RPS값 설정
 */

export let options = {
    noConnectionReuse: false,
    stages: [
        { duration: '5m', target: 100}, //simulate ramp-up of traffic from 1 to 100 users over 5 minutes
        { duration: '10m', target: 100}, // stay at 100 users for 10 minutes
        { duration: '5m', target: 0},  // ramp-down to 0 users
    ],
    thresholds: {
        http_req_duration: ['p(99)<150'], // 99% of requests must complete below 150ms
    }
}

const BASE_URL = 'http://host.docker.internal:8080';
const BULK_INSERT_BASE_AMOUNT = 1000;

export function setup() {
  let discounts = [];
  let usernames = [];

  const discountsResponse = http.get(`${BASE_URL}/discounts`, { timeout: 30000 });
  if (discountsResponse.status === 200) {
    discounts = JSON.parse(discountsResponse.body);
  } else {
    console.error(`Failed to fetch discounts. Status: ${discountsResponse.status}`);
  }

  const usersResponse = http.get(`${BASE_URL}/users`, { timeout: 30000 });
  if (usersResponse.status === 200) {
    const users = JSON.parse(usersResponse.body);
    usernames = users.map(user => user.name);
  } else {
    console.error(`Failed to fetch users. Status: ${usersResponse.status}`);
  }

  sleep(3);
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
  const months = Math.floor(Math.random() * 3) + 1;
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
    } else if (readRandom < 0.6) {
      getHighestRatedProducts();
    } else if (readRandom < 0.7) {
      getOrderItemsByUsername(usernames);
    } else if (readRandom < 0.8) {
      getSalesStatistics();
    } else {
      getUserByUsername(usernames);
    }
  } else {
    // Write requests (10%)
    createOrder(discounts);
  }

  sleep(1);
}