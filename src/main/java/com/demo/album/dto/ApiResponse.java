package com.demo.album.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 공통 응답 래퍼
 * - 모든 API가 { success, data, error } 형태로 통일
 * - success: true면 data에 결과, false면 error에 메시지
 * - error가 null이면 JSON에서 생략(JsonInclude.NON_NULL)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;

    /** 성공 시 사용. data만 넣고 success=true, error=null */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /** 실패 시 사용. error 메시지 넣고 success=false, data=null */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
