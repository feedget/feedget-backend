package kr.co.mashup.feedgetapi.exception;

import kr.co.mashup.feedgetapi.web.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * RestController에서 예외 발생시 처리할 핸들러 클래스
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BaseException.class)
    public Response baseException(BaseException e, HttpServletRequest request) {
        return new Response(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity invalidTokenException(InvalidTokenException e) {
        return new ResponseEntity<>(new Response(e.getStatus(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
