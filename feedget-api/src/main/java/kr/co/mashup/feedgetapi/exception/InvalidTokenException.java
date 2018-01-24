package kr.co.mashup.feedgetapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by ethan.kim on 2018. 1. 24..
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invaild token")
public class InvalidTokenException extends BaseException {

    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
