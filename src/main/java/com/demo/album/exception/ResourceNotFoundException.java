package com.demo.album.exception;

/**
 * 리소스를 찾을 수 없을 때 던지는 예외
 * - GlobalExceptionHandler에서 404로 매핑
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /** 리소스 이름 + id로 메시지 생성 (예: "앨범을(를) 찾을 수 없습니다. id: 1") */
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + "을(를) 찾을 수 없습니다. id: " + id);
    }
}
