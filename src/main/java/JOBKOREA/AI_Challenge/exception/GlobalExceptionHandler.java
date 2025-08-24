package JOBKOREA.AI_Challenge.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.net.SocketTimeoutException;
import com.theokanning.openai.OpenAiHttpException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 유효성 검사 실패 시 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "입력 데이터 유효성 검사 실패");
        response.put("errors", errors);
        response.put("status", "BAD_REQUEST");
        
        log.error("유효성 검사 실패: {}", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "서버 내부 오류가 발생했습니다");
        response.put("error", ex.getMessage());
        response.put("status", "INTERNAL_SERVER_ERROR");
        
        log.error("서버 오류 발생", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * OpenAI API 관련 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        
        Map<String, Object> response = new HashMap<>();
        
        // OpenAI 서비스에서 발생하는 구체적인 에러 메시지 처리
        String errorMessage = ex.getMessage();
        
        // SocketTimeoutException 확인
        if (ex.getCause() instanceof java.net.SocketTimeoutException) {
            response.put("message", "AI 서비스 응답 시간이 초과되었습니다");
            response.put("error", "요청 처리 시간이 너무 오래 걸렸습니다");
            response.put("status", "REQUEST_TIMEOUT");
            response.put("retryable", true);
            response.put("suggestion", "잠시 후 다시 시도해주세요");
        } else if (errorMessage != null && errorMessage.contains("잠시 후 다시 시도해주세요")) {
            // 사용자 친화적인 에러 메시지
            response.put("message", "AI 서비스에 일시적인 문제가 발생했습니다");
            response.put("error", errorMessage);
            response.put("status", "SERVICE_UNAVAILABLE");
            response.put("retryable", true);
            response.put("suggestion", "잠시 후 다시 시도해주세요");
        } else {
            // 일반적인 런타임 에러
            response.put("message", "AI 서비스 처리 중 오류가 발생했습니다");
            response.put("error", errorMessage != null ? errorMessage : "알 수 없는 오류가 발생했습니다");
            response.put("status", "SERVICE_UNAVAILABLE");
            response.put("retryable", true);
            response.put("suggestion", "잠시 후 다시 시도해주세요");
        }
        
        log.error("AI 서비스 오류 발생: {}", errorMessage, ex);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * OpenAI API 타임아웃 예외 처리
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeoutException(TimeoutException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "AI 서비스 응답 시간이 초과되었습니다");
        response.put("error", "요청 처리 시간이 너무 오래 걸렸습니다");
        response.put("status", "REQUEST_TIMEOUT");
        response.put("retryable", true);
        response.put("suggestion", "잠시 후 다시 시도해주세요");
        
        log.error("AI 서비스 타임아웃 발생", ex);
        
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
    }
    
    /**
     * OpenAI API 소켓 타임아웃 예외 처리
     */
    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleSocketTimeoutException(SocketTimeoutException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "AI 서비스 연결 시간이 초과되었습니다");
        response.put("error", "OpenAI 서버와의 연결이 시간 초과되었습니다");
        response.put("status", "REQUEST_TIMEOUT");
        response.put("retryable", true);
        response.put("suggestion", "네트워크 상태를 확인하고 잠시 후 다시 시도해주세요");
        response.put("errorCode", "SOCKET_TIMEOUT");
        
        log.error("AI 서비스 소켓 타임아웃 발생", ex);
        
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
    }
    
    /**
     * OpenAI API 응답 파싱 오류 처리
     */
    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleJsonProcessingException(
            com.fasterxml.jackson.core.JsonProcessingException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "AI 서비스 응답 처리 중 오류가 발생했습니다");
        response.put("error", "AI가 생성한 응답을 처리할 수 없습니다");
        response.put("status", "UNPROCESSABLE_ENTITY");
        response.put("retryable", true);
        response.put("suggestion", "잠시 후 다시 시도해주세요");
        
        log.error("AI 서비스 응답 파싱 오류 발생", ex);
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
    
    /**
     * OpenAI API HTTP 예외 처리 (인증 실패, 권한 없음 등)
     */
    @ExceptionHandler(OpenAiHttpException.class)
    public ResponseEntity<Map<String, Object>> handleOpenAiHttpException(OpenAiHttpException ex) {
        
        Map<String, Object> response = new HashMap<>();
        
        // HTTP 상태 코드에 따른 구체적인 에러 메시지
        int statusCode = ex.statusCode;
        if (statusCode == 401) {
            response.put("message", "AI 서비스 인증에 실패했습니다");
            response.put("error", "API 키가 올바르지 않거나 만료되었습니다");
            response.put("status", "UNAUTHORIZED");
            response.put("retryable", false);
            response.put("suggestion", "API 키를 확인하고 다시 시도해주세요");
            response.put("errorCode", "INVALID_API_KEY");
        } else if (statusCode == 429) {
            response.put("message", "AI 서비스 요청 한도를 초과했습니다");
            response.put("error", "너무 많은 요청을 보냈습니다");
            response.put("status", "TOO_MANY_REQUESTS");
            response.put("retryable", true);
            response.put("suggestion", "잠시 후 다시 시도해주세요");
            response.put("errorCode", "RATE_LIMIT_EXCEEDED");
        } else if (statusCode >= 500) {
            response.put("message", "AI 서비스에 일시적인 문제가 발생했습니다");
            response.put("error", "OpenAI 서버에서 오류가 발생했습니다");
            response.put("status", "SERVICE_UNAVAILABLE");
            response.put("retryable", true);
            response.put("suggestion", "잠시 후 다시 시도해주세요");
            response.put("errorCode", "OPENAI_SERVER_ERROR");
        } else {
            response.put("message", "AI 서비스 요청 중 오류가 발생했습니다");
            response.put("error", ex.getMessage());
            response.put("status", "BAD_REQUEST");
            response.put("retryable", false);
            response.put("suggestion", "요청 내용을 확인하고 다시 시도해주세요");
            response.put("errorCode", "OPENAI_REQUEST_ERROR");
        }
        
        log.error("OpenAI API HTTP 오류 발생 - 상태 코드: {}, 메시지: {}", statusCode, ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.valueOf(statusCode)).body(response);
    }
}
