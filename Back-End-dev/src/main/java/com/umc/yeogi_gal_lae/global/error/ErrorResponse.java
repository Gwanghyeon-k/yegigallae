package com.umc.yeogi_gal_lae.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@JsonPropertyOrder({"timestamp", "httpStatus", "code", "message", "errors", "path"})
public class ErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus httpStatus;
    private String code;
    private String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldError> errors;

    private String path; // 요청 경로 추가

    // 생성자
    private ErrorResponse(ErrorCode errorCode, List<FieldError> fieldErrors, String path) {
        this.timestamp = LocalDateTime.now();
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
        this.errors = fieldErrors.isEmpty() ? null : fieldErrors;
        this.path = path;
    }

    private ErrorResponse(ErrorCode errorCode, String errorMessage, String field, String path) {
        this.timestamp = LocalDateTime.now();
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.errorMessage = field == null ? errorMessage : null; // 필드 오류가 없을 때만 errorMessage 설정
        this.errors = field == null ? null : FieldError.of(field, "", errorMessage); // 필드 오류가 있으면 errors에 설정
        this.path = path;
    }

    /**
     * 일반 오류 응답 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> fieldErrors, String path) {
        return new ErrorResponse(errorCode, fieldErrors, path);
    }

    /**
     * 일반 오류 응답 생성 (필드 오류 없음)
     */
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode, new ArrayList<>(), path);
    }

    /**
     * ConstraintViolationException 기반 오류 응답 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, Set<ConstraintViolation<?>> violations, String path) {
        List<FieldError> fieldErrors = FieldError.of(violations);
        return new ErrorResponse(errorCode, fieldErrors, path);
    }

    /**
     * MethodArgumentNotValidException 기반 오류 응답 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult, String path) {
        List<FieldError> fieldErrors = FieldError.of(bindingResult);
        return new ErrorResponse(errorCode, fieldErrors, path);
    }

    /**
     * 단일 필드 오류 기반 오류 응답 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, String errorMessage, String field, String path) {
        if (field == null) {
            return new ErrorResponse(errorCode, errorMessage, null, path);
        } else {
            return new ErrorResponse(errorCode, null, field, path);
        }
    }

    @Getter
    public static class FieldError {
        private String field;
        private String rejectedValue;
        private String reason;

        public FieldError(String field, String rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        /**
         * BindingResult에서 필드 오류 추출
         */
        public static List<FieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }

        /**
         * ConstraintViolation에서 필드 오류 추출
         */
        public static List<FieldError> of(Set<ConstraintViolation<?>> violations) {
            return violations.stream()
                    .map(violation -> {
                        String fieldPath = violation.getPropertyPath().toString();
                        return new FieldError(
                                fieldPath,
                                violation.getInvalidValue() == null ? "" : violation.getInvalidValue().toString(),
                                violation.getMessage());
                    })
                    .collect(Collectors.toList());
        }

        /**
         * 단일 필드 오류 리스트 생성
         */
        public static List<FieldError> of(String field, String rejectedValue, String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, rejectedValue, reason));
            return fieldErrors;
        }
    }
}
