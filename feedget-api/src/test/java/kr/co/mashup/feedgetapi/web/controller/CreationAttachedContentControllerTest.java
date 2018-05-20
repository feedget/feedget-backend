package kr.co.mashup.feedgetapi.web.controller;

import kr.co.mashup.feedgetapi.service.ContentsService;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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
 * Created by ethan.kim on 2018. 1. 1..
 */
@RunWith(MockitoJUnitRunner.class)
public class CreationAttachedContentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContentsService contentsService;

    @InjectMocks
    private CreationAttachedContentController sut;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void createCreationAttachedContents_창작물의_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 컨텐츠 파일1개로
        long creationId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 창작물의 컨텐츠를 추가하면
        ResultActions resultActions = mockMvc.perform(fileUpload("/creations/{creationId}/contents", creationId)
                .file(file)
                .param("contentsType", "IMAGE"));

        // then : HttpStatus 201 / 컨텐츠가 추가된다
        MvcResult result = resultActions.andExpect(status().isCreated())
                .andReturn();
        verify(contentsService, times(1)).addCreationAttachedContents(eq(creationId), any(CreationDto.AttachedContent.class));
    }

    @Test
    public void createCreationAttachedContents_컨텐츠_파일이_없으면_창작물의_컨텐츠_추가_실패() throws Exception {
        // given : 창작물 ID로
        long creationId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 창작물의 컨텐츠를 추가하면
        ResultActions resultActions = mockMvc.perform(fileUpload("/creations/{creationId}/contents", creationId)
                .param("contentsType", "IMAGE"));

        // then : HttpStatus 400 / 컨텐츠가 추가되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(contentsService, never()).addCreationAttachedContents(eq(creationId), any(CreationDto.AttachedContent.class));
    }

    @Test
    public void createCreationAttachedContents_컨텐츠_파일이_10개초과면_창작물의_컨텐츠_추가_실패() throws Exception {
        // given : 창작물 ID, 컨텐츠파일 11로
        long creationId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 창작물의 컨텐츠를 추가하면
        ResultActions resultActions = mockMvc.perform(fileUpload("/creations/{creationId}/contents", creationId)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .file(file)
                .param("contentsType", "IMAGE"));

        // then : HttpStatus 400 / 컨텐츠가 추가되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(contentsService, never()).addCreationAttachedContents(eq(creationId), any(CreationDto.AttachedContent.class));
    }

    @Test
    public void deleteCreationAttachedContents_창작물의_컨텐츠_제거_성공() throws Exception {
        // given : 창작물 ID, 제거할 컨텐츠 ID 리스트로
        long creationId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        // when : 창작물의 컨텐츠를 제거하면
        ResultActions resultActions = mockMvc.perform(delete("/creations/{creationId}/contents", creationId)
                .param("contentId", String.valueOf(1L))
                .param("contentId", String.valueOf(2L))
                .param("contentId", String.valueOf(3L)));

        // then : HttpStatus 200 / 컨텐츠가 제거된다
        MvcResult result = resultActions.andExpect(status().isOk())
                .andReturn();
        verify(contentsService, times(1)).removeCreationAttachedContents(creationId, contentIds);
    }

    @Test
    public void deleteCreationAttachedContents_제거할_컨텐츠_ID가_없으면_창작물의_컨텐츠_제거_실패() throws Exception {
        // given : 창작물 ID
        long creationId = 1L;

        // when : 창작물의 컨텐츠를 제거하면
        ResultActions resultActions = mockMvc.perform(delete("/creations/{creationId}/contents", creationId));

        // then : HttpStatus 400 / 컨텐츠가 제거되지 않는다
        MvcResult result = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        verify(contentsService, never()).removeCreationAttachedContents(eq(creationId), anyListOf(Long.class));
    }
}
