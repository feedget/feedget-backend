package kr.co.mashup.feedgetapi.web.controller;

import kr.co.mashup.feedgetapi.service.FeedbackService;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        Page<FeedbackDto.Response> feedbackPage = new PageImpl<>(Collections.emptyList());

        when(feedbackService.readFeedbacks(eq(userId), eq(creationId), any(), anyLong())).thenReturn(feedbackPage);
        ArgumentCaptor<Pageable> pageableArg = ArgumentCaptor.forClass(Pageable.class);

        // when : 피드백 리스트를 조회하면
        MvcResult result = mockMvc.perform(get("/creations/{creationId}/feedbacks", creationId)
                .header("userId", userId)
                .param("cursor", String.valueOf(1))
                .param("page", String.valueOf(0))
                .param("size", String.valueOf(10))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 피드백 리스트가 조회된다
        verify(feedbackService, times(1)).readFeedbacks(eq(userId), eq(creationId), pageableArg.capture(), anyLong());
        assertEquals(pageableArg.getValue(), pageable);
    }
}
