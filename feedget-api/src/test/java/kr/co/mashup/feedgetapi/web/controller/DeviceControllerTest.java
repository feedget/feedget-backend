package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.DeviceService;
import kr.co.mashup.feedgetapi.web.dto.DeviceDto;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 3. 18..
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController sut;

    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void setUpClass() {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void registerCloudMessagingDevice_Cloud_Messaging_RegId_등록_성공() throws Exception {
        // given : 유저 ID, Cloud Messaging Device 정보로
        long userId = 1L;
        DeviceDto.UpdateCloudMsgRegId dto = new DeviceDto.UpdateCloudMsgRegId();
        dto.setCloudMsgRegToken("testCloudToken");

        // when : Cloud Messaging Device 정보를 등록하면
        ResultActions resultActions = mockMvc.perform(patch("/devices/cloud-messaging")
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 200 / Cloud Messaging Device 정보가 등록된다
        MvcResult result = resultActions.andExpect(status().isOk())
                .andReturn();
        verify(deviceService, times(1)).registerCloudMessagingDevice(userId, dto);
    }

    @Test
    public void registerCloudMessagingDevice_Cloud_Messaging_RegId가_공백이라_등록_실패() throws Exception {
        // given : 유저 ID, Cloud Messaging Device 정보로
        long userId = 1L;
        DeviceDto.UpdateCloudMsgRegId dto = new DeviceDto.UpdateCloudMsgRegId();
        dto.setCloudMsgRegToken("");

        // when : Cloud Messaging Device 정보를 등록하면
        ResultActions resultActions = mockMvc.perform(patch("/devices/cloud-messaging")
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / CloudMsgRegId가 공백이라 Cloud Messaging Device 정보가 등록되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(deviceService, never()).registerCloudMessagingDevice(userId, dto);
    }
}
