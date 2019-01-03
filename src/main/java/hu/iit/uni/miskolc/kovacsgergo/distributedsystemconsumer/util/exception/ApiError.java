package hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.util.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ApiError {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    private ApiError(){
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.errors = errors;

    }

    public ApiError(HttpStatus status, String message, String error) {
        super();
        timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

}