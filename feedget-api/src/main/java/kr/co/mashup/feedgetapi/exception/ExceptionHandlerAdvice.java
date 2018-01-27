package kr.co.mashup.feedgetapi.exception;

import kr.co.mashup.feedgetapi.web.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * RestController에서 예외 발생시 처리할 핸들러 클래스
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BaseException.class)
    public Response baseException(BaseException e) {
        return new Response(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public Response invalidTokenException(InvalidTokenException e) {
        return new Response(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Response notFoundException(NotFoundException ex) {
        return new Response(ex.getStatus(), ex.getMessage());
    }
}
