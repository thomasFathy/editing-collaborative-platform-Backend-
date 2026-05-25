package ntg.documentation.example.controller;


import lombok.extern.slf4j.Slf4j;
import ntg.documentation.example.domain.dto.ApiErrorResponse;
import ntg.documentation.example.exception.EmailAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException (Exception ex){

        log.error("Caught exception",ex);
        ApiErrorResponse error= ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(Exception ex){
        log.error("Ya3m b2a bad credentials wRbna");
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("No username with this name")
                .build();
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }


        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<Map<String, String>> handleEmailConflict(EmailAlreadyExistsException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }


}
