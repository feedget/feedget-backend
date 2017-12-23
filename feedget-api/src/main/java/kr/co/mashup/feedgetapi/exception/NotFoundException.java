package kr.co.mashup.feedgetapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 리소스가 존재하지 않을 경우 발생
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found")
public class NotFoundException extends BaseException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }
}
