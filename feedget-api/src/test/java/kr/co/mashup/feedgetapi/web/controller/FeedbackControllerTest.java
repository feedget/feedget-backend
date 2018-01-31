package kr.co.mashup.feedgetapi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.mashup.feedgetapi.service.FeedbackService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 1. 18..
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeedbackService feedbackService;

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

        when(feedbackService.readFeedbacks(eq(userId), eq(creationId), any(), anyLong())).thenReturn(feedbacks);
        ArgumentCaptor<Pageable> pageableArg = ArgumentCaptor.forClass(Pageable.class);

        // when : 피드백 리스트를 조회하면
        MvcResult result = mockMvc.perform(get("/creations/{creationId}/feedbacks", creationId)
                .requestAttr("userId", userId)
                .param("cursor", String.valueOf(1))
                .param("page", String.valueOf(0))
                .param("size", String.valueOf(10))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 피드백 리스트가 조회된다
        verify(feedbackService, times(1)).readFeedbacks(eq(userId), eq(creationId), pageableArg.capture(), anyLong());
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
        MvcResult result = mockMvc.perform(post("/creations/{creationId}/feedbacks", creationId)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated())
                .andReturn();

        // then : 피드백이 추가된다
        verify(feedbackService, times(1)).addFeedback(userId, creationId, dto);
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
        MvcResult result = mockMvc.perform(post("/creations/{creationId}/feedbacks", creationId)
                .requestAttr("userId", userId)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 피드백 내용이 짧아 피드백이 추가되지 않는다
        verify(feedbackService, never()).addFeedback(userId, creationId, dto);
    }
}
