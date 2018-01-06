package kr.co.mashup.feedgetapi.web.controller;

import kr.co.mashup.feedgetapi.service.ContentsService;
import kr.co.mashup.feedgetapi.web.dto.ContentsDto;
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
 * Created by ethan.kim on 2018. 1. 1..
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContentsService contentsService;

    @InjectMocks
    private ContentsController sut;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void createContents_창작물의_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 컨텐츠 파일1개로
        long creationId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 창작물의 컨텐츠를 추가하면
        MvcResult result = mockMvc.perform(fileUpload("/creations/{creationId}/contents", creationId)
                .file(file)
                .param("contentsType", "IMAGE")
        ).andExpect(status().isCreated())
                .andReturn();

        // then : 컨텐츠가 추가된다
        verify(contentsService, times(1)).addContents(eq(creationId), any(ContentsDto.class));
    }

    @Test
    public void createContents_창작물의_컨텐츠_추가_컨텐츠_파일이_없으면_실패() throws Exception {
        // given : 창작물 ID로
        long creationId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 창작물의 컨텐츠를 추가하면
        MvcResult result = mockMvc.perform(fileUpload("/creations/{creationId}/contents", creationId)
                .param("contentsType", "IMAGE")
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 컨텐츠가 추가되지 않는다
        verify(contentsService, never()).addContents(eq(creationId), any(ContentsDto.class));
    }

    @Test
    public void createContents_창작물의_컨텐츠_추가_컨텐츠_파일이_10개이상이면_실패() throws Exception {
        // given : 창작물 ID, 컨텐츠파일 11로
        long creationId = 1L;
        MockMultipartFile file = new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes());

        // when : 창작물의 컨텐츠를 추가하면
        MvcResult result = mockMvc.perform(fileUpload("/creations/{creationId}/contents", creationId)
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
                .param("contentsType", "IMAGE")
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 컨텐츠가 추가되지 않는다
        verify(contentsService, never()).addContents(eq(creationId), any(ContentsDto.class));
    }

    @Test
    public void deleteContents_창작물의_컨텐츠_제거_성공() throws Exception {
        // given : 창작물 ID, 제거할 컨텐츠 ID 리스트로
        long creationId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        // when : 창작물의 컨텐츠를 제거하면
        MvcResult result = mockMvc.perform(delete("/creations/{creationId}/contents", creationId)
                .param("contentId", String.valueOf(1L))
                .param("contentId", String.valueOf(2L))
                .param("contentId", String.valueOf(3L))
        ).andExpect(status().isOk())
                .andReturn();

        // then : 컨텐츠가 제거된다
        verify(contentsService, times(1)).removeContents(creationId, contentIds);
    }

    @Test
    public void deleteContents_창작물의_컨텐츠_제거_제거할_컨텐츠_ID가_없어서_실패() throws Exception {
        // given : 창작물 ID
        long creationId = 1L;

        // when : 창작물의 컨텐츠를 제거하면
        MvcResult result = mockMvc.perform(delete("/creations/{creationId}/contents", creationId)
        ).andExpect(status().isBadRequest())
                .andReturn();

        // then : 컨텐츠가 제거되지 않는다
        verify(contentsService, never()).removeContents(eq(creationId), anyListOf(Long.class));
    }
}
