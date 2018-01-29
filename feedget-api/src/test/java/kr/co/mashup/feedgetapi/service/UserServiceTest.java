package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.security.TokenManager;
import kr.co.mashup.feedgetapi.web.dto.SignInDto;
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

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private UserService sut;

    @Test
    public void signInUser_최초_signIn시_signUp과_signIn_성공() {
        // given : 유저의 signIn 정보로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        when(userRepository.findByEmail("test@mashup.co.kr")).thenReturn(Optional.empty());

        // when : signIn을 하면
        sut.signInUser(dto);

        // then : signIn되고, signUp까지 진행되고, access token, refresh token이 발급된다
        verify(userRepository, times(1)).findByEmail("test@mashup.co.kr");
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenManager, times(1)).generateAccessToken(any(User.class));
        verify(tokenManager, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    public void signInUser_최초_signIn이_아닐시_SignIn_성공() {
        // given : 유저의 signIn 정보로
        SignInDto.Create dto = new SignInDto.Create();
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

        // then : signIn이 성공되고, access token, refresh token이 발급된다
        verify(userRepository, times(1)).findByEmail("test@mashup.co.kr");
        verify(userRepository, never()).save(any(User.class));
        verify(tokenManager, times(1)).generateAccessToken(any(User.class));
        verify(tokenManager, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    public void modifyUserNickname_유저의_닉네임_수정_성공() {
        // given : 유저 ID, 수정할 닉네임 정보로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("123456");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new User()));

        // when : 닉네임을 수정하면
        sut.modifyUserNickname(userId, dto);

        // then : 닉네임이 수정된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void modifyUserNickname_존재하지_않는_유저라_유저의_닉네임_수정_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found user");

        // given : 유저 ID, 수정할 닉네임 정보로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("123456");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 닉네임을 수정하면
        sut.modifyUserNickname(userId, dto);

        // then : 존재하지 않는 유저라 닉네임이 수정되지 않는다
    }
}
