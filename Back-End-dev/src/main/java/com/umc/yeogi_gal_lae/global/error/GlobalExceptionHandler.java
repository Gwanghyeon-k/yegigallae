package com.umc.yeogi_gal_lae.global.error;

import com.umc.yeogi_gal_lae.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 일반 예외 처리기
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception occurred: ", e);
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "알 수 없는 오류가 발생했습니다.",
                "",
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    /**
     * 비즈니스 로직 예외 처리기
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        List<ErrorResponse.FieldError> fieldErrors = e.getErrors();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, fieldErrors, request.getRequestURI());

        // 상태 코드에 따라 로그 레벨 설정
        if (errorCode.getHttpStatus().is5xxServerError()) {
            log.error("BusinessException: {}", e.getMessage(), e);
        } else if (errorCode.getHttpStatus().is4xxClientError()) {
            log.warn("BusinessException: {}", e.getMessage(), e);
        } else {
            log.info("BusinessException: {}", e.getMessage(), e);
        }

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(AuthHandler.class)
    public ResponseEntity<BaseResponse<String>> handleAuthException(AuthHandler ex) {
        return new ResponseEntity<>(ex.toResponse(), ex.getErrorStatus().getHttpStatus());
    }

    /**
     * MethodArgumentNotValidException 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_REQUEST, bindingResult, request.getRequestURI());
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INVALID_REQUEST.getHttpStatus());
    }

    /**
     * MissingServletRequestParameterException 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INPUT_VALUE_INVALID,
                e.getMessage(),
                e.getParameterName(),
                request.getRequestURI()
        );
        log.warn("MissingServletRequestParameterException: {}", e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INPUT_VALUE_INVALID.getHttpStatus());
    }

    /**
     * HttpMessageNotReadableException 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.HTTP_MESSAGE_NOT_READABLE,
                e.getMessage(),
                "",
                request.getRequestURI()
        );
        log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);
        return new ResponseEntity<>(response, ErrorCode.HTTP_MESSAGE_NOT_READABLE.getHttpStatus());
    }

    /**
     * ConstraintViolationException 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INPUT_VALUE_INVALID,
                violations,
                request.getRequestURI()
        );
        log.warn("ConstraintViolationException: {}", e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INPUT_VALUE_INVALID.getHttpStatus());
    }

    /**
     * MissingServletRequestPartException 처리
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INPUT_VALUE_INVALID,
                e.getMessage(),
                e.getRequestPartName(),
                request.getRequestURI()
        );
        log.warn("MissingServletRequestPartException: {}", e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INPUT_VALUE_INVALID.getHttpStatus());
    }

    /**
     * MissingRequestCookieException 처리
     */
    @ExceptionHandler(MissingRequestCookieException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestCookieException(
            MissingRequestCookieException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INPUT_VALUE_INVALID,
                e.getMessage(),
                e.getCookieName(),
                request.getRequestURI()
        );
        log.warn("MissingRequestCookieException: {}", e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INPUT_VALUE_INVALID.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.BAD_REQUEST,
                e.getMessage(),
                null,
                request.getRequestURI()
        );
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.BAD_REQUEST.getHttpStatus());
    }

}
