package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.UserCommandService;
import kr.co.mashup.feedgetapi.service.UserQueryService;
import kr.co.mashup.feedgetapi.web.dto.UserDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 21..
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserController sut;

    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void setUpClass() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void updateUserNickname_유저_닉네임_수정_성공() throws Exception {
        // given : 유저ID, 수정할 닉네임으로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("nickname");

        // when : 닉네임을 수정하면
        ResultActions resultActions = mockMvc.perform(patch("/users/nickname")
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 200 / 닉네임이 수정된다
        MvcResult result = resultActions.andExpect(status().isOk())
                .andReturn();
        verify(userCommandService, times(1)).modifyUserNickname(userId, dto);
    }

    @Test
    public void updateUserNickname_유저_닉네임_수정_닉네임이_없어서_실패() throws Exception {
        // given : 유저ID, 공백인 닉네임으로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("");

        // when : 닉네임을 수정하면
        ResultActions resultActions = mockMvc.perform(patch("/users/nickname")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / 수정할 닉네임이 없어서 수정되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).modifyUserNickname(userId, dto);
    }

    @Test
    public void updateUserNickname_유저_닉네임_수정_닉네임이_자리수_제한을_넘어서_실패() throws Exception {
        // given : 유저ID, 11자리의 닉네임으로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("12345678910");

        // when : 닉네임을 수정하면
        ResultActions resultActions = mockMvc.perform(patch("/users/nickname")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / 자리수 제한을 넘어 닉네임이 수정되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).modifyUserNickname(userId, dto);
    }

    @Test
    public void getUserInfo_유저_정보_조회_성공() throws Exception {
        // given : 유저 ID
        long userId = 1L;
        String uuid = "me";

        // when : 유저 정보를 조회하면
        ResultActions resultActions = mockMvc.perform(get("/users/{uuid}", uuid)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON));

        // then : HttpStatus 200 / 유저 정보가 조회된다
        MvcResult result = resultActions.andExpect(status().isOk())
                .andReturn();
        verify(userQueryService, times(1)).readUserInfo(userId, uuid);
    }
}
