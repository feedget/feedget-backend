package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.common.StorageProperties;
import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.ContentsDto;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.CreationContent;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.FeedbackAttachedContent;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private CreationRepository creationRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

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

        when(storageProperties.getPath()).thenReturn("storage");
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

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

        when(storageProperties.getPath()).thenReturn("storage");
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

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

        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

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

    @Test
    public void addFeedbackAttachedContents_피드백의_이미지_첨부_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 피드백 ID, 첨부 이미지 컨텐츠로
        long creationId = 1L;
        long feedbackId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes()));

        FeedbackDto.AttachedContent dto = new FeedbackDto.AttachedContent();
        dto.setContentsType("IMAGE");
        dto.setFiles(multipartFiles);

        Feedback feedback = new Feedback();
        feedback.setCreationId(creationId);
        feedback.setAttachedContents(new ArrayList<>());

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));
        when(storageProperties.getPath()).thenReturn("storage");
        when(storageProperties.getUri()).thenReturn("uri");

        // when : 피드백의 첨부 컨텐츠를 추가하면
        sut.addFeedbackAttachedContents(creationId, feedbackId, dto);

        // then : 추가된다
        verify(storageProperties, times(1)).getPath();
        verify(storageProperties, times(1)).getUri();
    }

    @Test
    public void addFeedbackAttachedContents_피드백의_오디오_첨부_컨텐츠_추가_성공() throws Exception {
        // given : 창작물 ID, 피드백 ID, 첨부 오디오 컨텐츠로
        long creationId = 1L;
        long feedbackId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.mp4", "audio/mp4", "some audio".getBytes()));

        FeedbackDto.AttachedContent dto = new FeedbackDto.AttachedContent();
        dto.setContentsType("AUDIO");
        dto.setFiles(multipartFiles);

        Feedback feedback = new Feedback();
        feedback.setCreationId(creationId);
        feedback.setAttachedContents(new ArrayList<>());

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));
        when(storageProperties.getPath()).thenReturn("storage");
        when(storageProperties.getUri()).thenReturn("uri");

        // when : 피드백의 첨부 컨텐츠를 추가하면
        sut.addFeedbackAttachedContents(creationId, feedbackId, dto);

        // then : 추가된다
        verify(storageProperties, never()).getPath();
        verify(storageProperties, never()).getUri();
    }

    @Test
    public void addFeedbackAttachedContents_존재하지_않은_피드백이라_피드백의_첨부_컨텐츠_추가_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found feedback");

        // given : 창작물 ID, 피드백 ID, 첨부 컨텐츠로
        long creationId = 1L;
        long feedbackId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes()));

        FeedbackDto.AttachedContent dto = new FeedbackDto.AttachedContent();
        dto.setContentsType("IMAGE");
        dto.setFiles(multipartFiles);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.empty());

        // when : 피드백의 첨부 컨텐츠를 추가하면
        sut.addFeedbackAttachedContents(creationId, feedbackId, dto);

        // then : 존재하지 않은 피드백이라 추가되지 않는다
    }

    @Test
    public void addFeedbackAttachedContents_다른_창작물의_피드백이라_피드백의_첨부_컨텐츠_추가_실패() throws Exception {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden request");

        // given : 창작물 ID, 피드백 ID, 첨부 컨텐츠로
        long creationId = 1L;
        long feedbackId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("files", "filename.jpg", "image/jpeg", "some image".getBytes()));

        FeedbackDto.AttachedContent dto = new FeedbackDto.AttachedContent();
        dto.setContentsType("IMAGE");
        dto.setFiles(multipartFiles);

        Feedback feedback = new Feedback();
        feedback.setCreationId(3L);
        feedback.setAttachedContents(new ArrayList<>());

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));

        // when : 피드백의 첨부 컨텐츠를 추가하면
        sut.addFeedbackAttachedContents(creationId, feedbackId, dto);

        // then : 창작물 ID가 달라 추가되지 않는다
    }

    @Test
    public void removeFeedbackAttachedContents_피드백의_첨부_컨텐츠_제거_성공() {
        // given : 창작물 ID, 피드백 ID, 컨텐츠 ID 리스트로
        long creationId = 1L;
        long feedbackId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        List<FeedbackAttachedContent> contents = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            FeedbackAttachedContent content = new FeedbackAttachedContent();
            content.setFeedbackAttachedContentId(i);
            contents.add(content);
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(feedbackId);
        feedback.setCreationId(creationId);
        feedback.setAttachedContents(contents);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));

        // when : 피드백의 컨텐츠를 제거하면
        sut.removeFeedbackAttachedContents(creationId, feedbackId, contentIds);

        // then : 피드백의 컨텐츠가 제거된다
        verify(feedbackRepository, times(1)).findByFeedbackId(feedbackId);
        verify(storageProperties, atLeastOnce()).getPath();
    }

    @Test
    public void removeFeedbackAttachedContents_존재하지_않은_피드백이라_피드백의_첨부_컨텐츠_제거_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found feedback");

        // given : 창작물 ID, 피드백 ID, 컨텐츠 ID 리스트로
        long creationId = 1L;
        long feedbackId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.empty());

        // when : 피드백의 컨텐츠를 제거하면
        sut.removeFeedbackAttachedContents(creationId, feedbackId, contentIds);

        // then : 피드백의 컨텐츠가 제거되지 않는다
    }

    @Test
    public void removeFeedbackAttachedContents_다른_창작물의_피드백이라_피드백의_첨부_컨텐츠_제거_실패() throws Exception {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden request");

        // given : 창작물 ID, 피드백 ID, 컨텐츠 ID 리스트로
        long creationId = 1L;
        long feedbackId = 1L;
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);

        List<FeedbackAttachedContent> contents = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            FeedbackAttachedContent content = new FeedbackAttachedContent();
            content.setFeedbackAttachedContentId(i);
            contents.add(content);
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(feedbackId);
        feedback.setCreationId(2L);
        feedback.setAttachedContents(contents);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));

        // when : 피드백의 컨텐츠를 제거하면
        sut.removeFeedbackAttachedContents(creationId, feedbackId, contentIds);

        // then : 피드백의 컨텐츠가 제거되지 않는다
    }
}
