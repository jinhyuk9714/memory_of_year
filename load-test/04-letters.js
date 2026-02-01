/**
 * 부하 테스트 4: 편지 목록 조회 API (N+1 vs 최적화 비교용)
 * - setup: 회원가입 → 로그인 → 앨범 생성 → 편지 30개 생성
 * - Before: APP_PERF_USE_N1_LETTERS=true 로 실행 (N+1 발생)
 * - After:  기본값 (최적화 버전)
 *
 * 실행 예:
 *   APP_PERF_USE_N1_LETTERS=true k6 run load-test/04-letters.js   # Before
 *   k6 run load-test/04-letters.js                                 # After
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LETTER_COUNT = 30;

export function setup() {
  if (__ENV.AUTH_TOKEN && __ENV.ALBUM_ID) {
    return { token: __ENV.AUTH_TOKEN, albumId: __ENV.ALBUM_ID };
  }

  const uid = `letters_${Date.now()}`;

  let res = http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
    username: uid,
    password: 'pass123',
    nickname: `닉네임_${uid}`,
    email: `${uid}@loadtest.local`,
  }), { headers: { 'Content-Type': 'application/json' } });

  if (res.status !== 201) throw new Error(`Setup(회원가입) 실패: ${res.status}`);

  res = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
    username: uid,
    password: 'pass123',
  }), { headers: { 'Content-Type': 'application/json' } });

  if (res.status !== 200) throw new Error(`Setup(로그인) 실패: ${res.status}`);

  const token = JSON.parse(res.body).data.token;
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };

  res = http.post(`${BASE_URL}/api/albums/create`, JSON.stringify({
    title: '부하테스트 앨범',
    albumColor: '#fff',
    visibility: true,
    stickerUrl: null,
  }), { headers });

  if (res.status !== 201) throw new Error(`Setup(앨범생성) 실패: ${res.status}`);

  const albumId = JSON.parse(res.body).data.albumId;

  for (let i = 0; i < LETTER_COUNT; i++) {
    res = http.post(`${BASE_URL}/api/albums/${albumId}/create`, JSON.stringify({
      letterTitle: `편지 ${i}`,
      author: `작성자${i}`,
      content: `내용 ${i}`,
      isAnonymous: false,
      letterColor: '#fff',
    }), { headers });

    if (res.status !== 201) throw new Error(`Setup(편지생성 ${i}) 실패: ${res.status}`);
  }

  return { token, albumId };
}

export const options = {
  vus: 20,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function (data) {
  const res = http.get(`${BASE_URL}/api/albums/${data.albumId}/letters`, {
    headers: { Authorization: `Bearer ${data.token}` },
  });

  check(res, {
    '편지 목록 200': (r) => r.status === 200,
    '편지 30개 반환': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success && Array.isArray(body.data) && body.data.length === LETTER_COUNT;
      } catch {
        return false;
      }
    },
  });

  sleep(0.2);
}
