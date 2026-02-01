/**
 * 부하 테스트 2: 로그인 API
 * 실행: k6 run load-test/02-login.js
 *
 * setup()에서 테스트 계정을 자동 생성합니다.
 * 환경변수로 기존 계정 사용: TEST_USER, TEST_PASS
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export function setup() {
  const username = __ENV.TEST_USER || `login_${Date.now()}`;
  const password = __ENV.TEST_PASS || 'pass123';

  if (__ENV.TEST_USER) {
    return { username, password };
  }

  const uid = `login_${Date.now()}`;
  const res = http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
    username: uid,
    password,
    nickname: `닉네임_${uid}`,
    email: `${uid}@loadtest.local`,
  }), { headers: { 'Content-Type': 'application/json' } });

  if (res.status !== 201) {
    throw new Error(`Setup(회원가입) 실패: ${res.status} ${res.body}`);
  }
  return { username: uid, password };
}

export const options = {
  vus: 20,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<300'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function (data) {
  const payload = JSON.stringify({
    username: data.username,
    password: data.password,
  });

  const res = http.post(`${BASE_URL}/api/auth/login`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  const ok = check(res, {
    '로그인 성공 (200)': (r) => r.status === 200,
    '토큰 반환': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success && body.data && body.data.token;
      } catch {
        return false;
      }
    },
  });

  sleep(0.3);
}
