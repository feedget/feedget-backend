package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.FeedbackQueryService;
import kr.co.mashup.feedgetapi.service.FeedbackCommandService;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 18..
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeedbackCommandService feedbackCommandService;

    @Mock
    private FeedbackQueryService feedbackQueryService;

    @InjectMocks
    private FeedbackController sut;

    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void setUpClass() {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .alwaysDo(print())
                .build();
    }

    @Test
    public void readFeedbacks_피드백_리스트_조회_성공() throws Exception {
        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);

        List<FeedbackDto.Response> feedbacks = Collections.emptyList();

        when(feedbackQueryService.readFeedbackList(eq(userId), eq(creationId), any(), anyLong())).thenReturn(feedbacks);
        ArgumentCaptor<Pageable> pageableArg = ArgumentCaptor.forClass(Pageable.class);

        // when : 피드백 리스트를 조회하면
        MvcResult result = mockMvc.perform(get("/creations/{creationId}/feedback", creationId)
                .requestAttr("userId", userId)
                .param("cursor", String.valueOf(1))
                .param("page", String.valueOf(0))
                .param("size", String.valueOf(10))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 피드백 리스트가 조회된다
        verify(feedbackQueryService, times(1)).readFeedbackList(eq(userId), eq(creationId), pageableArg.capture(), anyLong());
        assertEquals(pageableArg.getValue(), pageable);
    }

    @Test
    public void createFeedback_창작물에_피드백_추가_성공() throws Exception {
        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("feedback content");
        dto.setAnonymity(true);

        // when : 창작물에 피드백을 추가하면
        MvcResult result = mockMvc.perform(post("/creations/{creationId}/feedback", creationId)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated())
                .andReturn();

        // then : 피드백이 추가된다
        verify(feedbackCommandService, times(1)).addFeedback(userId, creationId, dto);
    }

    @Test
    public void createFeedback_피드백_내용이_짧아_창작물에_피드백_추가_실패() throws Exception {
        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("min");
        dto.setAnonymity(true);

        // when : 창작물에 피드백을 추가하면
        MvcResult result = mockMvc.perform(post("/creations/{creationId}/feedback", creationId)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 피드백 내용이 짧아 피드백이 추가되지 않는다
        verify(feedbackCommandService, never()).addFeedback(userId, creationId, dto);
    }

    @Test
    public void deleteFeedback_창작물의_피드백_삭제_성공() throws Exception {
        // given : 유저 ID, 창작물 ID, 피드백 ID로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;

        // when : 창작물의 피드백을 삭제하면
        MvcResult result = mockMvc.perform(delete("/creations/{creationId}/feedback/{feedbackId}", creationId, feedbackId)
                .requestAttr("userId", userId)
        ).andExpect(status().isOk())
                .andReturn();

        // then : 창작물의 피드백이 삭제된다
        verify(feedbackCommandService, times(1)).removeFeedback(userId, creationId, feedbackId);
    }

    @Test
    public void selectFeedback_창작물의_피드백_채택_성공() throws Exception {
        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        // when : 창작물의 피드백을 채택하면
        MvcResult result = mockMvc.perform(put("/creations/{creationId}/feedback/{feedbackId}/selection", creationId, feedbackId)
                        .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 피드백이 채택된다
        verify(feedbackCommandService, times(1)).selectFeedback(userId, creationId, feedbackId, dto);
    }

    @Test
    public void selectFeedback_채택_의견이_짧아_창작물의_피드백_채택_실패() throws Exception {
        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("co");

        // when : 창작물의 피드백을 채택하면
        MvcResult result = mockMvc.perform(put("/creations/{creationId}/feedback/{feedbackId}/selection", creationId, feedbackId)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 채택 의견이 짧아 피드백이 채택되지 않는다
        verify(feedbackCommandService, never()).selectFeedback(userId, creationId, feedbackId, dto);
    }
}
