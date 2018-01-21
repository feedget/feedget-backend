package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.web.dto.UserDto;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 1. 20..
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService sut;

    @Test
    public void signInUser_유저_signIn_최초_signIn시_signUp_성공() {
        // given : 유저의 signIn 정보로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        when(userRepository.findByEmail("test@mashup.co.kr")).thenReturn(Optional.empty());

        // when : signIn을 하면
        sut.signInUser(dto);

        // then : signIn되고, signUp까지 진행된다
        verify(userRepository, times(1)).findByEmail("test@mashup.co.kr");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void signInUser_유저_signIn_최초_signIn이_아닐시_성공() {
        // given : 유저의 signIn 정보로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        User user = new User();
        user.setRealName("realName");
        user.setNickname("nickname");
        user.setEmail("test@mashup.co.kr");
        user.setOAuthToken("oauthToken");
        user.setOAuthType(User.OAuthType.FB);

        when(userRepository.findByEmail("test@mashup.co.kr")).thenReturn(Optional.of(user));

        // when : signIn을 하면
        sut.signInUser(dto);

        // then : signIn이 성공된다
        verify(userRepository, times(1)).findByEmail("test@mashup.co.kr");
        verify(userRepository, never()).save(any(User.class));
    }
}
