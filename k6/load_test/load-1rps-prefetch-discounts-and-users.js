import http from 'k6/http';
import { sleep } from 'k6';

// how to run?
//docker run --rm -i --net=host grafana/k6 run - <./k6/load_test/load-1rps-prefetch-data-for-POST-order-request.js

export let options = {
  stages: [
    // { duration: '0m', target: 1 }, // Ramp up to 100 users over 1 minute
    { duration: '1m', target: 1 }, // Stay at 100 users for 3 minutes
    { duration: '0m', target: 0 }, // Ramp down to 0 users over 1 minute
  ],
};

const BASE_URL = 'http://host.docker.internal:8080'; // Replace with your API base URL
const BULK_INSERT_BASE_AMOUNT = 1000;


export function setup() {
  let discounts = [];
  let usernames = [];

  // Pre-fetch discounts data
  // const response = http.get(`${BASE_URL}/discounts`, {timeout: 30000});
  const response = http.get(`${BASE_URL}/discounts`, {timeout: 30000});

  if (response.status === 200) {
    try {
      if (response.body && response.body.length > 0) {
        discounts = JSON.parse(response.body);
        // __VU.discounts = discounts; // Store discounts in the __VU object
      } else {
        console.error('Empty response body');
      }
    } catch (error) {
      console.error('Error parsing JSON:', error);
    }
  } else {
    console.error(`Failed to fetch products. Status: ${response.status}`);
    console.error('Response body:', response.body); // Log the response in case it's HTML or an error page
  }

    // Pre-fetch usernames data
    const usersResponse = http.get(`${BASE_URL}/users`, { timeout: 30000 });
    if (usersResponse.status === 200) {
      try {
        if (usersResponse.body && usersResponse.body.length > 0) {
          const users = JSON.parse(usersResponse.body);
          usernames = users.map(user => user.name);
        } else {
          console.error('Empty users response body');
        }
      } catch (error) {
        console.error('Error parsing users JSON:', error);
      }
    } else {
      console.error(`Failed to fetch users. Status: ${usersResponse.status}`);
      console.error('Users response body:', usersResponse.body);
    }

  sleep(3);
  return {discounts, usernames};
}

export default function ({ discounts, usernames }) {
  // Generate random user ID between 1 and BULK_INSERT_BASE_AMOUNT
  const userId = Math.floor(Math.random() * BULK_INSERT_BASE_AMOUNT) + 1;
  const discountId = discounts[userId - 1]?.discountId;
  const productItemId = userId;
  const productOptionVariationId = userId;

  // Find the discount object based on the discountId
  const discount = discounts.find((d) => d.discountId === discountId);

  if(discount){
    // Prepare the request payload
    const payload = [{
      memberId: userId,
      productItemId: productItemId,
      order_quantity: 1,
      discountId: discountId,
      discountType: discount.discountType,
      discountValue: discount.discountValue,
      startDate: discount.startDate,
      endDate: discount.endDate,
      productOptionVariationId: productOptionVariationId,
    }];

    // Send the POST request to create an order
    const response = http.post(`${BASE_URL}/orders`, JSON.stringify(payload), {
      headers: { 'Content-Type': 'application/json' },
    });
    // console.log(response);

    // Check the response status
    if (response.status !== 201) {
      console.error('Request failed with status:', response.status);
    }
  } else {
    console.warn(`Discount not found for discountId: ${discountId}`);
  }

  const username = usernames[userId - 1];
  if (username) {
    const userResponse = http.get(`${BASE_URL}/users/${username}`);

    if (userResponse.status !== 200) {
      console.error('User retrieval failed with status:', userResponse.status);
    }
  } else {
    console.warn(`Username not found for userId: ${userId}`);
  }

  // Wait for 1 second before the next iteration
  sleep(1);
}