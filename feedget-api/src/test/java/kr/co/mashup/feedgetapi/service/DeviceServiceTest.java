package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.DeviceDto;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 3. 17..
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceService sut;

    @Test
    public void registerCloudMessagingDevice_Cloud_Messaging_Device_등록_성공() {
        // given : 유저 ID, Cloud Messaging Device 정보로
        long userId = 1L;
        DeviceDto.UpdateCloudMsgRegId dto = new DeviceDto.UpdateCloudMsgRegId();
        dto.setCloudMsgRegToken("testCloudMsgRegId");

        User user = User.builder()
                .cloudMsgRegId(null)
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // when : Cloud Messaging Device 정보를 등록하면
        sut.registerCloudMessagingDevice(userId, dto);

        // then : Cloud Messaging Device 정보가 등록된다
        verify(userRepository, times(1)).findByUserId(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void registerCloudMessagingDevice_Cloud_Messaging_Device_등록_이전_데이터와_같아서_실패() {
        // given : 유저 ID, Cloud Messaging Device 정보로
        long userId = 1L;
        DeviceDto.UpdateCloudMsgRegId dto = new DeviceDto.UpdateCloudMsgRegId();
        dto.setCloudMsgRegToken("testCloudMsgRegId");

        User user = User.builder()
                .cloudMsgRegId("testCloudMsgRegId")
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        // when : Cloud Messaging Device 정보를 등록하면
        sut.registerCloudMessagingDevice(userId, dto);

        // then : 이전과 같은 값이라 Cloud Messaging Device 정보가 등록되지 않는다
        verify(userRepository, times(1)).findByUserId(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test(expected = NotFoundException.class)
    public void registerCloudMessagingDevice_Cloud_Messaging_Device_등록_존재하지_않는_유저라_실패() {
        // given : 유저 ID, Cloud Messaging Device 정보로
        long userId = 1L;
        DeviceDto.UpdateCloudMsgRegId dto = new DeviceDto.UpdateCloudMsgRegId();
        dto.setCloudMsgRegToken("testCloudMsgRegId");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : Cloud Messaging Device 정보를 등록하면
        sut.registerCloudMessagingDevice(userId, dto);

        // then : 존재하지 않는 유저라 등록 실패
    }
}
