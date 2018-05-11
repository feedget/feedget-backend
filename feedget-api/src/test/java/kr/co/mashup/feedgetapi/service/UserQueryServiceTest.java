package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 5. 12..
 */
@RunWith(MockitoJUnitRunner.class)
public class UserQueryServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService sut;

    @Test
    public void readUserInfo_유저_자신의_정보_조회_성공() {
        // given : 유저 ID, 정보 조회할 유저의 UUID로
        long userId = 1L;
        String uuid = "me";

        User user = User.builder()
                .realName("realName")
                .nickname("nickname")
                .email("test@mashup.co.kr")
                .oAuthToken("oauthToken")
                .oAuthType(User.OAuthType.FB)
                .currentPointAmount(100.0)
                .feedbackSelectionRate(10.0)
                .creationDeadlineRate(10.0)
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // when : 유저 정보를 조회하면
        UserDto.DetailResponse response = sut.readUserInfo(userId, uuid);

        // then : 자신의 정보가 조회된다
        assertThat(response).isNotNull();
        verify(userRepository, never()).findByUuid(uuid);
        verify(userRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void readUserInfo_다른_유저의_정보_조회_성공() {
        // given : 유저 ID, 정보 조회할 유저의 UUID로
        long userId = 1L;
        String uuid = "feeerer3403035dd223fhd";

        User user = User.builder()
                .realName("realName")
                .nickname("nickname")
                .email("test@mashup.co.kr")
                .oAuthToken("oauthToken")
                .oAuthType(User.OAuthType.FB)
                .currentPointAmount(100.0)
                .feedbackSelectionRate(10.0)
                .creationDeadlineRate(10.0)
                .build();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        // when : 유저 정보를 조회하면
        UserDto.DetailResponse response = sut.readUserInfo(userId, uuid);

        // then : 다른 유저의 정보가 조회된다
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).findByUuid(uuid);
        verify(userRepository, never()).findByUserId(userId);
    }

    @Test
    public void readUserInfo_존재하지_않는_유저라_유저_정보_조회_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found user");

        // given : 유저 ID, 정보 조회할 유저의 UUID로
        long userId = 1L;
        String uuid = "me";

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 유저 정보를 조회하면
        UserDto.DetailResponse response = sut.readUserInfo(userId, uuid);

        // then : 존재하지 않는 유저라 유저 정보를 조회할 수 없다
    }
}