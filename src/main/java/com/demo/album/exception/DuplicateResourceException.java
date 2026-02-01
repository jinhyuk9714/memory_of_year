package com.demo.album.exception;

/**
 * 중복 리소스(예: 이미 존재하는 username) 예외
 * - GlobalExceptionHandler에서 409 Conflict로 매핑
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
