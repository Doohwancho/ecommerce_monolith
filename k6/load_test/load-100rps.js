import http from 'k6/http';
import { sleep } from 'k6';

// how to run?
//docker run --rm -i grafana/k6 run - <./k6/load_test/load-100rps.js

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

const API_BASE_URL = 'http://my-cool-project-692040879.ap-northeast-2.elb.amazonaws.com:80/products/category/';

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
    let randomCategoryId = getRandomInt(16, 74); 

    // Send a single HTTP request to the randomly selected category ID
    http.get(`${API_BASE_URL}${randomCategoryId}`);
    
    sleep(1);
}
