package kr.co.mashup.feedgetapi.web.controller;

import kr.co.mashup.feedgetapi.service.ContentsService;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ethan.kim on 2018. 2. 4..
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackAttachedContentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContentsService contentsService;

    @InjectMocks
    private FeedbackAttachedContentController sut;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void createFeedbackAttachedContents_피드백의_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 피드백 ID, 컨텐츠 파일 1개로
        long creationId = 1L;
        long feedbackId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 피드백의 컨텐츠를 추가하면
        MvcResult result = mockMvc.perform(fileUpload("/creations/{creationId}/feedback/{feedbackId}/contents", creationId, feedbackId)
                .file(file)
                .param("contentsType", "IMAGE")
        ).andExpect(status().isCreated())
                .andReturn();

        // then : 컨텐츠가 추가된다
        verify(contentsService, times(1)).addFeedbackAttachedContents(eq(creationId), eq(feedbackId), any(FeedbackDto.AttachedContent.class));
    }

    @Test
    public void createFeedbackAttachedContents_컨텐츠_파일이_없어서_피드백의_컨텐츠_추가_실패() throws Exception {
        // given : 창작물 ID, 피드백 ID, 컨텐츠 파일 1개로
        long creationId = 1L;
        long feedbackId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 피드백의 컨텐츠를 추가하면
        MvcResult result = mockMvc.perform(fileUpload("/creations/{creationId}/feedback/{feedbackId}/contents", creationId, feedbackId)
                .param("contentsType", "IMAGE")
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 컨텐츠가 추가되지 않는다
        verify(contentsService, never()).addFeedbackAttachedContents(eq(creationId), eq(feedbackId), any(FeedbackDto.AttachedContent.class));
    }

    @Test
    public void createFeedbackAttachedContents_컨텐츠_파일이_3개초과면_피드백의_컨텐츠_추가_실패() throws Exception {
        // given : 창작물 ID, 피드백 ID, 컨텐츠 파일 4개로
        long creationId = 1L;
        long feedbackId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 피드백의 컨텐츠를 추가하면
        MvcResult result = mockMvc.perform(fileUpload("/creations/{creationId}/feedback/{feedbackId}/contents", creationId, feedbackId)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .param("contentsType", "IMAGE")
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 컨텐츠가 추가되지 않는다
        verify(contentsService, never()).addFeedbackAttachedContents(eq(creationId), eq(feedbackId), any(FeedbackDto.AttachedContent.class));
    }

    @Test
    public void deleteFeedbackAttachedContents_피드백의_컨텐츠_제거_성공() throws Exception {
        // given : 창작물 ID, 피드백 ID, 제거할 컨텐츠 ID 리스트로
        long creationId = 1L;
        long feedbackId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        // when : 피드백의 컨텐츠를 제거하면
        MvcResult result = mockMvc.perform(delete("/creations/{creationId}/feedback/{feedbackId}/contents", creationId, feedbackId)
                .param("contentId", String.valueOf(1L))
                .param("contentId", String.valueOf(2L))
                .param("contentId", String.valueOf(3L))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 컨텐츠가 제거된다
        verify(contentsService, times(1)).removeFeedbackAttachedContents(creationId, feedbackId, contentIds);
    }

    @Test
    public void deleteFeedbackAttachedContents_제거할_컨텐츠_ID가_없으면_피드백의_컨텐츠_제거_실패() throws Exception {
        // given : 창작물 ID, 피드백 ID
        long creationId = 1L;
        long feedbackId = 1L;

        // when : 피드백의 컨텐츠를 제거하면
        MvcResult result = mockMvc.perform(delete("/creations/{creationId}/feedback/{feedbackId}/contents", creationId, feedbackId)
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 컨텐츠가 제거되지 않는다
        verify(contentsService, never()).removeFeedbackAttachedContents(eq(creationId), eq(feedbackId), anyListOf(Long.class));
    }
}
