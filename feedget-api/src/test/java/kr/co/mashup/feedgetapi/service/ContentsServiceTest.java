package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.common.StorageProperties;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.ContentsDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.CreationContent;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2017. 12. 31..
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentsServiceTest {

    @Mock
    private CreationRepository creationRepository;

    @Mock
    private StorageProperties storageProperties;

    @InjectMocks
    private ContentsService sut;

    @Test
    public void addContents_창작물의_이미지_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 컨텐츠로
        long creationId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes()));

        ContentsDto dto = new ContentsDto();
        dto.setContentsType("IMAGE");
        dto.setFiles(multipartFiles);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setContents(new ArrayList<>());
        Optional<Creation> creationOp = Optional.of(creation);

        when(storageProperties.getPath()).thenReturn("storage");
        when(creationRepository.findByCreationId(creationId)).thenReturn(creationOp);

        // when : 창작물의 컨텐츠를 추가하면
        sut.addContents(creationId, dto);

        // then : 창작물의 컨텐츠가 추가된다
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(storageProperties, atLeastOnce()).getPath();
    }

    @Test(expected = NotFoundException.class)
    public void addContents_창작물의_컨텐츠_추가_창작물이_없으면_실패() throws Exception {
        // given : 창작물 ID, 컨텐츠로
        long creationId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes()));

        ContentsDto dto = new ContentsDto();
        dto.setContentsType("IMAGE");
        dto.setFiles(multipartFiles);

        when(storageProperties.getPath()).thenReturn("storage");
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 창작물의 컨텐츠를 추가하면
        sut.addContents(creationId, dto);

        // then : 창작물이 존재하지 않아 추가되지 않는다
    }

    @Test
    public void addContents_창작물의_오디오_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 컨텐츠로
        long creationId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.mp4", "audio/mp4", "some audio".getBytes()));

        ContentsDto dto = new ContentsDto();
        dto.setContentsType("AUDIO");
        dto.setFiles(multipartFiles);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setContents(new ArrayList<>());
        Optional<Creation> creationOp = Optional.of(creation);

        when(storageProperties.getPath()).thenReturn("storage");
        when(creationRepository.findByCreationId(creationId)).thenReturn(creationOp);

        // when : 창작물의 컨텐츠를 추가하면
        sut.addContents(creationId, dto);

        // then : 창작물의 컨텐츠가 추가된다
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(storageProperties, never()).getPath();
    }

    @Test
    public void removeContents_창작물의_컨텐츠_제거_성공() throws Exception {
        // given : 창작물 ID, 컨텐츠 ID 리스트로
        long creationId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        List<CreationContent> contents = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            CreationContent content = new CreationContent();
            content.setCreationContentId(i);
            contents.add(content);
        }

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setContents(contents);
        Optional<Creation> creationOp = Optional.of(creation);

        when(creationRepository.findByCreationId(creationId)).thenReturn(creationOp);

        // when : 창작물의 컨텐츠를 제거하면
        sut.removeContents(creationId, contentIds);

        // then : 창작물의 컨텐츠가 제거된다
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(storageProperties, atLeastOnce()).getPath();
    }

    @Test(expected = NotFoundException.class)
    public void removeContents_창작물의_컨텐츠_제거_창작물이_없어서_실패() throws Exception {
        // given : 창작물 ID, 컨텐츠 ID 리스트로
        long creationId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 창작물의 컨텐츠를 제거하면
        sut.removeContents(creationId, contentIds);

        // then : 창작물이 존재하지 않아 제거되지 않는다
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(storageProperties, never()).getPath();
    }
}
