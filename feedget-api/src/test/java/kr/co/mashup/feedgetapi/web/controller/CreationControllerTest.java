package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.CreationService;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 2..
 */
@RunWith(MockitoJUnitRunner.class)
public class CreationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreationService creationService;

    @InjectMocks
    private CreationController sut;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    public void createCreation_창작물_추가_성공() throws Exception {
        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        // when : 창작물을 추가하면
        MvcResult result = mockMvc.perform(post("/creations")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated())
                .andReturn();

        // then : 창작물이 추가된다
        verify(creationService, times(1)).addCreation(anyLong(), any(CreationDto.Create.class));
    }

    @Test
    public void createCreation_창작물_추가_제목이_없어서_실패() throws Exception {
        // given : 유저 ID, 제목이 없는 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        // when : 창작물을 추가하면
        MvcResult result = mockMvc.perform(post("/creations")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 설명이 없어서 창작물이 추가되지 않는다
        verify(creationService, never()).addCreation(anyLong(), any(CreationDto.Create.class));
    }

    @Test
    public void createCreation_창작물_추가_설명이_없어서_실패() throws Exception {
        // given : 유저 ID, 설명이 없는 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        // when : 창작물을 추가하면
        MvcResult result = mockMvc.perform(post("/creations")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 설명이 없어서 창작물이 추가되지 않는다
        verify(creationService, never()).addCreation(anyLong(), any(CreationDto.Create.class));
    }

    @Test
    public void createCreation_창작물_추가_카테고리가_없어서_실패() throws Exception {
        // given : 유저 ID, 카테고리가 없는 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        // when : 창작물을 추가하면
        MvcResult result = mockMvc.perform(post("/creations")
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 설명이 없어서 창작물이 추가되지 않는다
        verify(creationService, never()).addCreation(anyLong(), any(CreationDto.Create.class));
    }
}
