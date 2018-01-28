package kr.co.mashup.feedgetapi.web.interceptor;

import kr.co.mashup.feedgetapi.exception.InvalidTokenException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.security.JwtProperties;
import kr.co.mashup.feedgetapi.security.TokenManager;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Ref
 * https://stackoverflow.com/questions/22626100/how-to-unit-test-spring-mvc-interceptors-without-controller-class
 * https://stackoverflow.com/questions/24140494/how-to-test-spring-handlerinterceptor-mapping
 * <p>
 * Created by ethan.kim on 2018. 1. 25..
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthInterceptorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @MockBean
    private Environment environment;

    @MockBean
    private TokenManager tokenManager;

    @SpyBean
    private JwtProperties jwtProperties;

    @MockBean
    private UserRepository userRepository;

    @SpyBean
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private AuthInterceptor sut;

    @Test
    public void preHandle_localhost라_토큰_검증_안하고_통과() throws Exception {
        // given : local profile, 토큰 검증이 필요한 request URI로
        String[] activeProfiles = {"localhost"};

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.toString(), "/creations");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain hec = handlerMapping.getHandler(request);

        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        // when : 인터셉터를 통과시키면
        boolean result = sut.preHandle(request, response, hec.getHandler());

        // then : 토큰 검증 안하고 통과된다
        assertTrue(result);
        verify(jwtProperties, never()).getHttpHeader();
        verify(tokenManager, never()).validateToken(anyString());
        verify(tokenManager, never()).getUserUuid(anyString());
        verify(userRepository, never()).findByUuid(anyString());
    }

    @Test
    public void preHandle_토큰_검증이_불필요한_URI라_통과() throws Exception {
        // given : develop profile, 토큰 검증이 불필요한 request URI로
        String[] activeProfiles = {"develop"};

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.toString(), "/users/sign-in");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain hec = handlerMapping.getHandler(request);

        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        // when : 인터셉터를 통과시키면
        boolean result = sut.preHandle(request, response, hec.getHandler());

        // then : 토큰 검증 안하고 통과된다
        assertTrue(result);
        verify(jwtProperties, never()).getHttpHeader();
        verify(tokenManager, never()).validateToken(anyString());
        verify(tokenManager, never()).getUserUuid(anyString());
        verify(userRepository, never()).findByUuid(anyString());
    }

    @Test
    public void preHandle_정상_토큰이고_토큰_검증이_필요한_URI라_토큰_검증_후_통과() throws Exception {
        // given : develop profile, 토큰 검증이 필요한 request URI로
        String[] activeProfiles = {"develop"};
        String accessToken = "accessToken";
        String uuid = "UUID";

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.toString(), "/creations");
        request.addHeader("Authorization", "Bearer " + accessToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain hec = handlerMapping.getHandler(request);

        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(tokenManager.getUserUuid(accessToken)).thenReturn(uuid);
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(new User()));

        // when : 인터셉터를 통과시키면
        boolean result = sut.preHandle(request, response, hec.getHandler());

        // then : 토큰 검증 후 통과된다
        assertTrue(result);
        verify(jwtProperties, times(1)).getHttpHeader();
        verify(tokenManager, times(1)).validateToken(anyString());
        verify(tokenManager, times(1)).getUserUuid(anyString());
        verify(userRepository, times(1)).findByUuid(anyString());
    }

    @Test
    public void preHandle_비정상_헤더라_통과_실패() throws Exception {
        expectedException.expect(InvalidTokenException.class);
        expectedException.expectMessage("Missing or invalid Authorization header");

        // given : develop profile, 토큰 검증이 필요한 request URI, 비정상 header로
        String[] activeProfiles = {"develop"};
        String accessToken = "accessToken";

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.toString(), "/creations");
        request.addHeader("Authorization", "Bear " + accessToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain hec = handlerMapping.getHandler(request);

        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        // when : 인터셉터를 통과시키면
        sut.preHandle(request, response, hec.getHandler());

        // then : 헤더 검증에 실패하여 통과되지 못한다
    }

    @Test
    public void preHandle_존재하지_않는_유저라_통과_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("user not found");

        // given : develop profile, 토큰 검증이 필요한 request URI, 정상 header로
        String[] activeProfiles = {"develop"};
        String accessToken = "accessToken";
        String uuid = "UUID";

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.toString(), "/creations");
        request.addHeader("Authorization", "Bearer " + accessToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain hec = handlerMapping.getHandler(request);

        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(tokenManager.getUserUuid(accessToken)).thenReturn(uuid);
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when : 인터셉터를 통과시키면
        sut.preHandle(request, response, hec.getHandler());

        // then : 존재하지 않는 유저라 통과되지 못한다
    }
}
