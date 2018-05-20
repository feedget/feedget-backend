package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.UserCommandService;
import kr.co.mashup.feedgetapi.web.dto.SignInDto;
import kr.co.mashup.feedgetcommon.domain.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 29..
 */
@RunWith(MockitoJUnitRunner.class)
public class SignInControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserCommandService userCommandService;

    @InjectMocks
    private SignInController sut;

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
    public void signInUser_유저의_signIn_성공() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 200 / 로그인 된다
        MvcResult result = resultActions.andExpect(status().isOk())
                .andReturn();
        verify(userCommandService, times(1)).signInUser(dto);
    }

    @Test
    public void signInUser_실명이_없어서_유저의_signIn_실패() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / 실명이 없어서 로그인되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_닉네임이_없어서_유저의_signIn_실패() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / 닉네임이 없어서 로그인되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_이메일이_없어서_유저의_signIn_실패() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        );

        // then : HttpStatus 400 / 이메일이 없어서 로그인되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_이메일_형식이_아니라_유저의_signIn_실패() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / 이메일 형식이 아니라 로그인되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_oauthToken이_없어서_유저의_signIn_실패() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / oauthToken이 없어서 로그인되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_oauthType이_없어서_유저의_signIn_실패() throws Exception {
        // given : signIn 데이터로
        SignInDto.Create dto = new SignInDto.Create();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");

        // when : signIn을 하면
        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then : HttpStatus 400 / oauthType이 없어서 로그인되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(userCommandService, never()).signInUser(dto);
    }
}
