package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.UserService;
import kr.co.mashup.feedgetapi.web.dto.UserDto;
import kr.co.mashup.feedgetcommon.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 21..
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController sut;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    public void signInUser_유저_signIn_성공() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 로그인 된다
        verify(userService, times(1)).signInUser(dto);
    }

    @Test
    public void signInUser_유저_signIn_실명이_없어서_실패() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 실명이 없어서 로그인되지 않는다
        verify(userService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_유저_signIn_닉네임이_없어서_실패() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 닉네임이 없어서 로그인되지 않는다
        verify(userService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_유저_signIn_이메일이_없어서_실패() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 이메일이 없어서 로그인되지 않는다
        verify(userService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_유저_signIn_이메일이_형식이_아니라_실패() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test");
        dto.setOAuthToken("oauthToken");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 이메일 형식이 아니라 로그인되지 않는다
        verify(userService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_유저_signIn_oauthToken이_없어서_실패() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("");
        dto.setOAuthType(User.OAuthType.FB);

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : oauthToken이 없어서 로그인되지 않는다
        verify(userService, never()).signInUser(dto);
    }

    @Test
    public void signInUser_유저_signIn_oauthType이_없어서_실패() throws Exception {
        // given : signIn 데이터로
        UserDto.SignIn dto = new UserDto.SignIn();
        dto.setRealName("realName");
        dto.setNickname("nickname");
        dto.setEmail("test@mashup.co.kr");
        dto.setOAuthToken("oauthToken");

        // when : signIn을 하면
        MvcResult result = mockMvc.perform(post("/users/sign-in")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : oauthType이 없어서 로그인되지 않는다
        verify(userService, never()).signInUser(dto);
    }

    @Test
    public void updateUserNickname_유저_닉네임_수정_성공() throws Exception {
        // given : 유저ID, 수정할 닉네임으로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("nickname");

        // when : 닉네임을 수정하면
        MvcResult result = mockMvc.perform(patch("/users/nickname")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 닉네임이 수정된다
        verify(userService, times(1)).modifyUserNickname(userId, dto);
    }

    @Test
    public void updateUserNickname_유저_닉네임_수정_닉네임이_없어서_실패() throws Exception {
        // given : 유저ID, 공백인 닉네임으로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("");

        // when : 닉네임을 수정하면
        MvcResult result = mockMvc.perform(patch("/users/nickname")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 수정할 닉네임이 없어서 수정되지 않는다
        verify(userService, never()).modifyUserNickname(userId, dto);
    }

    @Test
    public void updateUserNickname_유저_닉네임_수정_닉네임이_자리수_제한을_넘어서_실패() throws Exception {
        // given : 유저ID, 11자리의 닉네임으로
        long userId = 1L;
        UserDto.UpdateNickname dto = new UserDto.UpdateNickname();
        dto.setNickname("12345678910");

        // when : 닉네임을 수정하면
        MvcResult result = mockMvc.perform(patch("/users/nickname")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 자리수 제한을 넘어 닉네임이 수정되지 않는다
        verify(userService, never()).modifyUserNickname(userId, dto);
    }
}
