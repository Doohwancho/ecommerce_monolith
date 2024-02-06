import http from 'k6/http';
import { sleep } from 'k6';

// how to run?
//docker run --rm -i grafana/k6 run - <./k6/load_test/load-1000rps.js

export let options = {

    noConnectionReuse: false,
    stages: [
        { duration: '5m', target: 1000}, //simulate ramp-up of traffic from 1 to 1000 users over 5 minutes
        { duration: '10m', target: 1000}, // stay at 1000 users for 10 minutes
        { duration: '5m', target: 0},  // ramp-down to 0 users
    ],
    thresholds: {
        http_req_duration: ['p(99)<150'], // 99% of requests must complete below 150ms
    }
}

// const API_BASE_URL = 'http://192.168.0.77:8080/api/greet';
const API_BASE_URL = 'http://my-cool-project-384023695.ap-northeast-2.elb.amazonaws.com:80/products/category/';

export default function () {
    http.batch([
        ['GET', `${API_BASE_URL}/16`],
        ['GET', `${API_BASE_URL}/17`],
        ['GET', `${API_BASE_URL}/18`],
    ])

  sleep(1);
}
