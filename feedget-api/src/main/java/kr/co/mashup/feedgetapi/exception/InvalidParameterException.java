package kr.co.mashup.feedgetapi.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 파라미터 잘못넣었을 경우 발생
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
@ResponseStatus(value = BAD_REQUEST, reason = "Invalid Parameter")
public class InvalidParameterException extends BaseException {

    public InvalidParameterException() {
        this("Invalid Parameter");
    }

    public InvalidParameterException(String message) {
        super(BAD_REQUEST.value(), message);
    }
}
