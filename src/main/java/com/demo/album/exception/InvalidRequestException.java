package com.demo.album.exception;

/**
 * 잘못된 요청(예: 유효하지 않은 스티커 URL) 예외
 * - GlobalExceptionHandler에서 400 Bad Request로 매핑
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
