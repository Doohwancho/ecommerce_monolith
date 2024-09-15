import http from 'k6/http';
import { sleep } from 'k6';

// how to run?
//docker run --rm -i grafana/k6 run - <./k6/load_test/load-1rps.js

export let options = {
    noConnectionReuse: false,
    stages: [
        { duration: '1m', target: 1}, 
    ],
    thresholds: {
        http_req_duration: ['p(99)<150'], // 99% of requests must complete below 150ms
    }
}

const API_BASE_URL = 'http://3.34.156.27:8080/products/category/';

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
    let randomCategoryId = getRandomInt(16, 74); 

    // Send a single HTTP request to the randomly selected category ID
    http.get(`${API_BASE_URL}${randomCategoryId}`);
    
    sleep(1);
}
