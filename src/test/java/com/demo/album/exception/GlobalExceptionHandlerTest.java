package com.demo.album.exception;

import com.demo.album.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GlobalExceptionHandler 단위 테스트
 * - 각 예외 타입별로 404/409/400/401/500 등 적절한 상태 코드와 ApiResponse 반환 검증
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("ResourceNotFoundException")
    class ResourceNotFound {

        @Test
        @DisplayName("404 + error 메시지")
        void handle() {
            ResourceNotFoundException e = new ResourceNotFoundException("앨범을 찾을 수 없습니다.");

            ResponseEntity<ApiResponse<Void>> res = handler.handleResourceNotFound(e);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(res.getBody()).isNotNull();
            assertThat(res.getBody().isSuccess()).isFalse();
            assertThat(res.getBody().getError()).isEqualTo("앨범을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("DuplicateResourceException")
    class Duplicate {

        @Test
        @DisplayName("409 + error 메시지")
        void handle() {
            DuplicateResourceException e = new DuplicateResourceException("이미 존재하는 사용자입니다.");

            ResponseEntity<ApiResponse<Void>> res = handler.handleDuplicate(e);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(res.getBody()).isNotNull();
            assertThat(res.getBody().getError()).isEqualTo("이미 존재하는 사용자입니다.");
        }
    }

    @Nested
    @DisplayName("InvalidRequestException")
    class InvalidRequest {

        @Test
        @DisplayName("400 + error 메시지")
        void handle() {
            InvalidRequestException e = new InvalidRequestException("유효하지 않은 요청입니다.");

            ResponseEntity<ApiResponse<Void>> res = handler.handleInvalidRequest(e);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(res.getBody().getError()).isEqualTo("유효하지 않은 요청입니다.");
        }
    }

    @Nested
    @DisplayName("UsernameNotFoundException")
    class UsernameNotFound {

        @Test
        @DisplayName("401 + error 메시지")
        void handle() {
            UsernameNotFoundException e = new UsernameNotFoundException("사용자를 찾을 수 없습니다.");

            ResponseEntity<ApiResponse<Void>> res = handler.handleUsernameNotFound(e);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("ResponseStatusException")
    class ResponseStatus {

        @Test
        @DisplayName("예외의 statusCode와 reason 반환")
        void handle() {
            ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, "사진을 찾을 수 없습니다.");

            ResponseEntity<ApiResponse<Void>> res = handler.handleResponseStatus(e);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(res.getBody().getError()).isEqualTo("사진을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("Exception (일반)")
    class Generic {

        @Test
        @DisplayName("500 + 서버 오류 메시지")
        void handle() {
            ResponseEntity<ApiResponse<Void>> res = handler.handleGeneric(new RuntimeException("알 수 없는 오류"));

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(res.getBody().getError()).isEqualTo("서버 오류가 발생했습니다.");
        }
    }
}
