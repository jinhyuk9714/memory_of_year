/**
 * 스모크 테스트: 전체 플로우 1회 (회원가입 → 로그인 → 앨범 생성 → 앨범 조회)
 * 실행: k6 run load-test/00-smoke.js
 *
 * 서버 기동 후 API 동작 확인용. 부하 없이 1 VU, 1회 실행.
 */
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: 1,
  iterations: 1,
};

export default function () {
  const uid = `smoke_${Date.now()}`;

  // 1. 회원가입 (username, nickname, email 모두 unique)
  let res = http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
    username: uid,
    password: 'pass123',
    nickname: `닉네임_${uid}`,
    email: `${uid}@test.com`,
  }), { headers: { 'Content-Type': 'application/json' } });

  check(res, { '회원가입 201': (r) => r.status === 201 });

  // 2. 로그인
  res = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
    username: uid,
    password: 'pass123',
  }), { headers: { 'Content-Type': 'application/json' } });

  const loginOk = check(res, { '로그인 200': (r) => r.status === 200 });
  if (!loginOk) {
    console.error('로그인 실패:', res.body);
    return;
  }

  const token = JSON.parse(res.body).data.token;
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };

  // 3. 스티커 목록 조회 (인증 필요, 있으면 사용 없으면 null - 로컬에서는 S3 미설정 시 비어있음)
  const stickersRes = http.get(`${BASE_URL}/api/stickers`, { headers });
  let stickerUrl = null;
  if (stickersRes.status === 200 && stickersRes.body) {
    const list = JSON.parse(stickersRes.body).data;
    if (list && list.length > 0) stickerUrl = list[0];
  }

  // 4. 앨범 생성 (stickerUrl 없어도 생성 가능 - 로컬/S3 미설정 대응)
  res = http.post(`${BASE_URL}/api/albums/create`, JSON.stringify({
    title: '스모크 앨범',
    albumColor: '#fff',
    visibility: true,
    stickerUrl: stickerUrl,
  }), { headers });

  if (res.status !== 201) {
    console.error('앨범 생성 실패:', res.status, res.body);
  }
  check(res, { '앨범 생성 201': (r) => r.status === 201 });

  const albumId = JSON.parse(res.body || '{}').data?.albumId;
  if (!albumId) return;

  // 5. 앨범 조회
  res = http.get(`${BASE_URL}/api/albums/${albumId}`, { headers });
  check(res, { '앨범 조회 200': (r) => r.status === 200 });

  console.log('스모크 테스트 통과');
}
