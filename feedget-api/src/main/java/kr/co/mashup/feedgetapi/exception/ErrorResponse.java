package kr.co.mashup.feedgetapi.exception;

import lombok.Data;

import java.util.List;

/**
 * Created by ethan.kim on 2017. 12. 23..
 */
@Data
public class ErrorResponse {
    private String message;

    private String code;

    private List<FieldError> errors;

    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}
