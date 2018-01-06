package kr.co.mashup.feedgetapi.common;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP POST, PUT method Multipart request를 허용하는 Resolver
 * Spring Boot AutoConfiguration인 MultipartAutoConfiguration에서
 * StandardServletMultipartResolver를 사용하고 있어서 상속받아 구현
 * <p>
 * 참고
 * https://stackoverflow.com/questions/12616928/restful-put-with-file-upload-and-form-data-in-spring-mvc
 * https://blog.outsider.ne.kr/1001
 * <p>
 * Created by ethan.kim on 2018. 1. 5..
 */
@Component
public class PutAwareCommonsMultipartResolver extends StandardServletMultipartResolver {

    private static final String MULTIPART = "multipart/";

    private static final String POST_METHOD = "post";

    private static final String PUT_METHOD = "put";

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        String method = request.getMethod().toLowerCase();
        if (!POST_METHOD.equals(method) && !PUT_METHOD.equals(method)) {
            return false;
        }

        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(MULTIPART);
    }
}
