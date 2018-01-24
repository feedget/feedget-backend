package kr.co.mashup.feedgetapi.web.interceptor;

import kr.co.mashup.feedgetapi.exception.BaseException;
import kr.co.mashup.feedgetapi.exception.InvalidTokenException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.security.JwtProperties;
import kr.co.mashup.feedgetapi.security.TokenManager;
import kr.co.mashup.feedgetapi.web.controller.UserController;
import kr.co.mashup.feedgetapi.web.dto.Response;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Controller 에서 체크해야할 request 의 정합성을 체크한다
 * <p>
 * Created by ethan.kim on 2018. 1. 23..
 */
@Component
@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static final String HEADER_PREFIX = "Bearer ";

    @Autowired
    private Environment environment;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserRepository userRepository;

    /**
     * 진입 전에 체크해야할 항목을 체크하고, 실패한 경우 Controller로 진입시키지 않는다
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Todo: 배포할 경우 제거
//        if (ArrayUtils.contains(environment.getActiveProfiles(), "localhost")) {
//            return super.preHandle(request, response, handler);
//        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (isAuthNotRequiredController(handlerMethod)) {
            log.debug("this class cannot be intercepted {}", handlerMethod.getBean().getClass().toString());
            return super.preHandle(request, response, handler);
        }

        log.debug("handler class is {}", handlerMethod.getBean().getClass().toString());

        // Todo: validator로 로직 분리
        // header
        // Authorization: Bearer <token>
        final String authHeader = request.getHeader(jwtProperties.getHttpHeader());

        if (authHeader == null
                || !StringUtils.startsWith(authHeader, HEADER_PREFIX)) {
            // 401은 인증 실패, 403은 인가 실패라고 볼 수 있음
            throw new InvalidTokenException("Missing or invalid Authorization header");
        }

        log.info("authorization header : {}", authHeader);

        final String token = StringUtils.substring(authHeader, HEADER_PREFIX.length());  // the part after "Bearer "
        if (!tokenManager.validateToken(token)) {
            throw new InvalidTokenException("Invaild token");
        }

        String uuid = tokenManager.getUserUuid(token);

        Optional<User> userOp = userRepository.findByUuid(uuid);
        User user = userOp.orElseThrow(() -> new NotFoundException("user not found"));

        // Todo: header에 userID 셋팅 or header로 userID를 사용하는 곳의 코드 수정
        // Todo: remove logic user not found exception thorwn in service layer
        // Todo: logging interceptor 추가 - http://www.baeldung.com/spring-mvc-handlerinterceptor

        request.setAttribute("userId", user.getUserId());

        return super.preHandle(request, response, handler);
    }

    /**
     * 적용 대상이 아니면 true
     *
     * @param handler
     * @return
     */
    private boolean isAuthNotRequiredController(HandlerMethod handler) {
        Object controllerClass = handler.getBean();

        // Todo: interceptor 적용 여부를 나누기 위해 UserController에서 로그인 로직 분리
        if (controllerClass instanceof UserController) {
            return true;
        }

        return false;
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public Response handleNotFoundException(NotFoundException ex, HttpServletResponse resp) {
        return new Response(ex.getStatus(), ex.getMessage());
    }

}
