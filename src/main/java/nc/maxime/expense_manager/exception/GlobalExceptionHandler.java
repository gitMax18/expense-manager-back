package nc.maxime.expense_manager.exception;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import nc.maxime.expense_manager.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
                var fieldErrors = exception.getBindingResult().getFieldErrors();
                var details = fieldErrors.stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                FieldError::getDefaultMessage,
                                                (existing, replacement) -> replacement,
                                                LinkedHashMap::new));

                return ResponseEntity.badRequest()
                                .body(new ErrorResponse("Validation failed", details));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
                var details = exception.getConstraintViolations().stream()
                                .collect(Collectors.toMap(
                                                violation -> violation.getPropertyPath().toString(),
                                                violation -> violation.getMessage(),
                                                (existing, replacement) -> replacement,
                                                LinkedHashMap::new));

                return ResponseEntity.badRequest()
                                .body(new ErrorResponse("Validation failed", details));
        }

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException exception) {
                var errorMessage = exception.getReason() == null
                                ? exception.getStatusCode().toString()
                                : exception.getReason();

                return ResponseEntity.status(exception.getStatusCode())
                                .body(new ErrorResponse(errorMessage, null));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
                var errorMessage = exception.getMessage() == null
                                ? "Unexpected error"
                                : exception.getMessage();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse(errorMessage, null));
        }
}
