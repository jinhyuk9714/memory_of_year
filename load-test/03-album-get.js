/**
 * 부하 테스트 3: 앨범 조회 API
 * 실행: k6 run load-test/03-album-get.js
 *
 * setup()에서 회원가입 → 로그인 → 앨범 생성 후, 해당 앨범 조회 부하 테스트
 * 환경변수 AUTH_TOKEN, ALBUM_ID가 있으면 setup 생략하고 해당 값 사용
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export function setup() {
  if (__ENV.AUTH_TOKEN && __ENV.ALBUM_ID) {
    return { token: __ENV.AUTH_TOKEN, albumId: __ENV.ALBUM_ID };
  }

  const uid = `album_${Date.now()}`;

  let res = http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
    username: uid,
    password: 'pass123',
    nickname: `닉네임_${uid}`,
    email: `${uid}@loadtest.local`,
  }), { headers: { 'Content-Type': 'application/json' } });

  if (res.status !== 201) throw new Error(`Setup(회원가입) 실패: ${res.status} ${res.body}`);

  res = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
    username: uid,
    password: 'pass123',
  }), { headers: { 'Content-Type': 'application/json' } });

  if (res.status !== 200) throw new Error(`Setup(로그인) 실패: ${res.status} ${res.body}`);

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

  if (res.status !== 201) throw new Error(`Setup(앨범생성) 실패: ${res.status} ${res.body}`);

  const albumId = JSON.parse(res.body).data.albumId;
  return { token, albumId };
}

export const options = {
  vus: 30,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<200'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function (data) {
  const res = http.get(`${BASE_URL}/api/albums/${data.albumId}`, {
    headers: { Authorization: `Bearer ${data.token}` },
  });

  check(res, {
    '앨범 조회 성공 (200)': (r) => r.status === 200,
    '응답에 albumId': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success && body.data && body.data.albumId != null;
      } catch {
        return false;
      }
    },
  });

  sleep(0.2);
}
