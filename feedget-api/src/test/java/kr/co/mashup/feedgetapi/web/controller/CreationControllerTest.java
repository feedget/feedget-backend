package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.CreationCommandService;
import kr.co.mashup.feedgetapi.service.CreationQueryService;
import kr.co.mashup.feedgetapi.web.CreationUpdateValidator;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 2..
 */
@RunWith(MockitoJUnitRunner.class)
public class CreationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreationCommandService creationCommandService;

    @Mock
    private CreationQueryService creationQueryService;

    @Spy
    private CreationUpdateValidator creationUpdateValidator;

    @InjectMocks
    private CreationController sut;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
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
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated())
                .andReturn();

        // then : 창작물이 추가된다
        verify(creationCommandService, times(1)).addCreation(anyLong(), any(CreationDto.Create.class));
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
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 제목이 없어서 창작물이 추가되지 않는다
        verify(creationCommandService, never()).addCreation(anyLong(), any(CreationDto.Create.class));
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
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 설명이 없어서 창작물이 추가되지 않는다
        verify(creationCommandService, never()).addCreation(anyLong(), any(CreationDto.Create.class));
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
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 카테고리가 없어서 창작물이 추가되지 않는다
        verify(creationCommandService, never()).addCreation(anyLong(), any(CreationDto.Create.class));
    }

    @Test
    public void updateCreation_창작물_수정_성공() throws Exception {
        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(1000.0);

        // when : 창작물을 수정하면
        MvcResult result = mockMvc.perform(put("/creations/{creationId}", creationId)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 창작물이 수정된다
        verify(creationCommandService, times(1)).modifyCreation(userId, creationId, dto);
    }

    @Test
    public void updateCreation_창작물_수정_제목이_없어서_실패() throws Exception {
        // given : 유저 ID, 창작물 ID, 제목이 없는 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(1000.0);

        // when : 창작물을 수정하면
        MvcResult result = mockMvc.perform(put("/creations/{creationId}", creationId)
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 제목이 없어서 창작물이 수정되지 않는다
        verify(creationCommandService, never()).modifyCreation(userId, creationId, dto);
    }

    @Test
    public void updateCreation_창작물_수정_설명이_없어서_실패() throws Exception {
        // given : 유저 ID, 창작물 ID, 설명이 없는 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(1000.0);

        // when : 창작물을 수정하면
        MvcResult result = mockMvc.perform(put("/creations/{creationId}", creationId)
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 설명이 없어서 창작물이 수정되지 않는다
        verify(creationCommandService, never()).modifyCreation(userId, creationId, dto);
    }

    @Test
    public void updateCreation_창작물_수정_카테고리가_없어서_실패() throws Exception {
        // given : 유저 ID, 창작물 ID, 카테고리가 없는 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("");
        dto.setAnonymity(true);
        dto.setRewardPoint(1000.0);

        // when : 창작물을 수정하면
        MvcResult result = mockMvc.perform(put("/creations/{creationId}", creationId)
                .header("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 카테고리가 없어서 창작물이 수정되지 않는다
        verify(creationCommandService, never()).modifyCreation(userId, creationId, dto);
    }

    @Test
    public void deleteCreation_창작물_삭제_성공() throws Exception {
        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;

        // when : 창작물을 삭제하면
        MvcResult result = mockMvc.perform(delete("/creations/{creationId}", creationId)
                .requestAttr("userId", userId)
        ).andExpect(status().isOk())
                .andReturn();

        // then : 창작물이 삭제된다
        verify(creationCommandService, times(1)).removeCreation(userId, creationId);
    }

    @Test
    public void readCreations_창작물_리스트_조회_성공() throws Exception {
        // given : 유저 ID, 카테고리 이름으로
        long userId = 1L;
        String category = "ALL";
        Pageable pageable = new PageRequest(0, 10);

        Page<CreationDto.Response> creationPage = new PageImpl<>(Collections.emptyList());

        when(creationQueryService.readCreations(eq(userId), eq(category), any())).thenReturn(creationPage);
        ArgumentCaptor<Pageable> pageableArg = ArgumentCaptor.forClass(Pageable.class);

        // when : 창작물 리스트를 조회하면
        MvcResult result = mockMvc.perform(get("/creations")
                .requestAttr("userId", userId)
                .param("category", category)
                .param("page", String.valueOf(0))
                .param("size", String.valueOf(10))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 창작물 리스트가 조회된다
        verify(creationQueryService, times(1)).readCreations(eq(userId), eq(category), pageableArg.capture());
        assertEquals(pageableArg.getValue(), pageable);
    }

    @Test
    public void readCreation_창작물_단건_조회_성공() throws Exception {
        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;

        // when : 창작물을 조회하면
        MvcResult result = mockMvc.perform(get("/creations/{creationId}", creationId)
                .requestAttr("userId", userId)
        ).andExpect(status().isOk())
                .andReturn();

        // then : 창작물이 조회된다
        verify(creationQueryService, times(1)).readCreation(eq(userId), eq(creationId));
    }
}
