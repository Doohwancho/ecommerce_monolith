import http from 'k6/http';
import { sleep } from 'k6';

// how to run?
//docker run --rm -i grafana/k6 run --vus 1000 --duration 10s - <./k6/stress.js
//docker run --rm -i grafana/k6 run - <./k6/stress.js

export let options = {

    noConnectionReuse: false,
    stages: [
        { duration: '2m', target: 50}, //below normal load
        { duration: '3m', target: 50},
        { duration: '2m', target: 100}, //normal load
        { duration: '3m', target: 100},
        { duration: '2m', target: 150}, //around the breaking point
        { duration: '3m', target: 150},
        { duration: '2m', target: 200}, //beyong the breaking point
        { duration: '3m', target: 200},
        { duration: '10m', target: 0}, //scale down. recovery stage
    ]
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
