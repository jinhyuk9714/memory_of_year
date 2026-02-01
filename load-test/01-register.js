/**
 * 부하 테스트 1: 회원가입 API
 * 실행: k6 run load-test/01-register.js
 * 옵션: k6 run --vus 10 --duration 30s load-test/01-register.js
 *
 * 환경변수:
 *   BASE_URL - API 서버 주소 (기본: http://localhost:8080)
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: 10,           // 동시 가상 사용자 10명
  duration: '30s',   // 30초간 유지
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% 요청이 500ms 이내
    http_req_failed: ['rate<0.01'],   // 실패율 1% 미만
  },
};

export default function () {
  const uid = `u${__VU}_${__ITER}_${Date.now()}_${Math.random().toString(36).slice(2, 10)}`;
  const payload = JSON.stringify({
    username: uid,
    password: 'password123',
    nickname: `닉네임_${uid}`,
    email: `${uid}@loadtest.local`,
  });

  const res = http.post(`${BASE_URL}/api/auth/register`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    '회원가입 성공 (201)': (r) => r.status === 201,
    '응답에 success true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true && body.data != null;
      } catch {
        return false;
      }
    },
  });

  sleep(0.5);
}
