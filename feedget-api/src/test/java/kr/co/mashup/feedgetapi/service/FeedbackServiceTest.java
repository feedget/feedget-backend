package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.PointHistory;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
import kr.co.mashup.feedgetcommon.repository.PointHistoryRepository;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 1. 18..
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreationRepository creationRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private FeedbackService sut;

    @Test
    public void readFeedbackList_피드백_리스트_조회_0page_조회_성공() {
        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Creation creation = new Creation();

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(1L);
        feedback.setContent("description");
        feedback.setWriter(user);
        feedback.setAnonymity(true);
        feedback.setSelection(true);
        feedback.setAttachedContents(Collections.emptyList());

        Page<Feedback> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.findByCreationIdAndSelectionIsFalse(creationId, pageable)).thenReturn(page);
        when(feedbackRepository.findByCreationIdAndSelectionIsTrue(creationId)).thenReturn(Optional.of(feedback));

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbackList = sut.readFeedbackList(userId, creationId, pageable, cursor);

        // then : 피드백 리스트가 조회된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(feedbackRepository, times(1)).findByCreationIdAndWriterId(creationId, userId);
        verify(feedbackRepository, times(1)).findByCreationIdAndSelectionIsFalse(creationId, pageable);
        verify(feedbackRepository, times(1)).findByCreationIdAndSelectionIsTrue(creationId);
    }

    @Test
    public void readFeedbackList_피드백_리스트_조회_1page_조회_성공() {
        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(1, 10);
        Long cursor = null;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Creation creation = new Creation();

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(1L);
        feedback.setContent("description");
        feedback.setWriter(user);
        feedback.setAnonymity(true);
        feedback.setSelection(true);
        feedback.setAttachedContents(Collections.emptyList());

        Page<Feedback> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.findByCreationIdAndSelectionIsFalse(creationId, pageable)).thenReturn(page);

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> readFeedbackList = sut.readFeedbackList(userId, creationId, pageable, cursor);

        // then : 피드백 리스트가 조회된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(feedbackRepository, times(1)).findByCreationIdAndWriterId(creationId, userId);
        verify(feedbackRepository, times(1)).findByCreationIdAndSelectionIsFalse(creationId, pageable);
        verify(feedbackRepository, never()).findByCreationIdAndSelectionIsTrue(creationId);
    }

    @Test
    public void readFeedbackList_피드백_리스트_조회_유저가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found user");

        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> readFeedbackList = sut.readFeedbackList(userId, creationId, pageable, cursor);

        // then : 유저가 없어 피드백 리스트가 조회되지 않는다
    }

    @Test
    public void readFeedbackList_피드백_리스트_조회_창작물이_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbackList = sut.readFeedbackList(userId, creationId, pageable, cursor);

        // then : 창작물이 없어 피드백 리스트가 조회되지 않는다
    }

    @Test
    public void readFeedbackList_피드백_리스트_조회_자신의_피드백이_없어서_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found feedback");

        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Creation creation = new Creation();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> readFeedbackList = sut.readFeedbackList(userId, creationId, pageable, cursor);

        // then : 자신의 피드백이 없어 피드백 리스트가 조회되지 않는다
    }

    @Test
    public void addFeedback_창작물에_피드백_추가_성공() {
        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("feedback content");
        dto.setAnonymity(true);

        User user = User.builder()
                .build();
        user.setUserId(userId);

        User otherUser = User.builder()
                .build();
        otherUser.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(otherUser);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 창작물에 피드백을 추가하면
        sut.addFeedback(userId, creationId, dto);

        // then : 피드백이 추가된다
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    public void addFeedback_존재하지_않는_유저라_창작물에_피드백_추가_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found writer");

        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("feedback content");
        dto.setAnonymity(true);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 창작물에 피드백을 추가하면
        sut.addFeedback(userId, creationId, dto);

        // then : 존재하지 않는 유저라 피드백이 추가되지 않는다
    }

    @Test
    public void addFeedback_존재하지_않는_창작물이라_창작물에_피드백_추가_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("feedback content");
        dto.setAnonymity(true);

        User user = User.builder()
                .build();
        user.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 창작물에 피드백을 추가하면
        sut.addFeedback(userId, creationId, dto);

        // then : 존재하지 않는 창작물이라 피드백이 추가되지 않는다
    }

    @Test
    public void addFeedback_창작물_작성자라_창작물에_피드백_추가_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden write feedback");

        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("feedback content");
        dto.setAnonymity(true);

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(user);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물에 피드백을 추가하면
        sut.addFeedback(userId, creationId, dto);

        // then : 창작물 작성자라 피드백이 추가되지 않는다
    }

    @Test
    public void addFeedback_이미_피드백을_작성해서_창작물에_피드백_추가_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("exceed write feedback");

        // given : 유저 ID, 창작물 ID, 추가할 피드백 데이터로
        long userId = 1L;
        long creationId = 1L;
        FeedbackDto.Create dto = new FeedbackDto.Create();
        dto.setContent("feedback content");
        dto.setAnonymity(true);

        User user = User.builder()
                .build();
        user.setUserId(userId);

        User otherUser = User.builder()
                .build();
        otherUser.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(otherUser);

        Feedback feedback = new Feedback();
        feedback.setWriter(user);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));

        // when : 창작물에 피드백을 추가하면
        sut.addFeedback(userId, creationId, dto);

        // then : 이미 피드백을 작성해서 피드백이 추가되지 않는다
    }

    @Test
    public void removeFeedback_창작물의_피드백_제거_성공() {
        // given : 유저 ID, 창작물 ID, 피드백 ID로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Feedback feedback = new Feedback();
        feedback.setWriter(user);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물의 피드백을 제거하면
        sut.removeFeedback(userId, creationId, feedbackId);

        // then : 피드백이 제거된다
        verify(feedbackRepository, times(1)).delete(feedbackId);
    }


    @Test
    public void removeFeedback_작성하지_않은_피드백이라_창작물의_피드백_제거_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found feedback");

        // given : 유저 ID, 창작물 ID, 피드백 ID로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;

        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 창작물의 피드백을 제거하면
        sut.removeFeedback(userId, creationId, feedbackId);

        // then : 작성하지 않은 피드백이라 피드백이 제거되지 않는다
        verify(feedbackRepository, times(1)).delete(feedbackId);
    }

    @Test
    public void removeFeedback_존재하지_않은_창작물이라_창작물의_피드백_제거_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID, 피드백 ID로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Feedback feedback = new Feedback();
        feedback.setWriter(user);

        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 창작물의 피드백을 제거하면
        sut.removeFeedback(userId, creationId, feedbackId);

        // then : 존재하지 않은 창작물이라 피드백이 제거되지 않는다
    }

    @Test
    public void removeFeedback_마감된_창작물이라_창작물의_피드백_제거_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden remove feedback");

        // given : 유저 ID, 창작물 ID, 피드백 ID로
        long userId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;

        User user = User.builder()
                .build();
        user.setUserId(userId);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setStatus(Creation.Status.DEADLINE);

        Feedback feedback = new Feedback();
        feedback.setWriter(user);

        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물의 피드백을 제거하면
        sut.removeFeedback(userId, creationId, feedbackId);

        // then : 마감된 창작물이라 피드백이 제거되지 않는다
    }

    @Test
    public void selectFeedback_피드백_채택_성공() {
        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long creationWriterId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        User creationWriter = User.builder()
                .build();
        creationWriter.setUserId(creationWriterId);

        User feedbackWriter = User.builder()
                .totalPointAmount(10.0)
                .currentPointAmount(10.0)
                .periodPointAmount(10.0)
                .build();
        feedbackWriter.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(creationWriter);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.DEADLINE);

        Feedback feedback = new Feedback();
        feedback.setWriter(feedbackWriter);
        feedback.setCreationId(creationId);
        feedback.setCreation(creation);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));
        when(userRepository.findByUserId(creationWriterId)).thenReturn(Optional.of(creationWriter));

        // when : 피드백을 채택하면
        sut.selectFeedback(creationWriterId, creationId, feedbackId, dto);

        // then : 피드백이 채택된다
        verify(feedbackRepository, times(1)).findByFeedbackId(feedbackId);
        verify(userRepository, times(1)).findByUserId(creationWriterId);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    public void selectFeedback_존재하지_않은_피드백이라_피드백_채택_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found feedback");

        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long creationWriterId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.empty());

        // when : 피드백을 채택하면
        sut.selectFeedback(creationWriterId, creationId, feedbackId, dto);

        // then : 존재하지 않은 피드백이라 피드백 채택에 실패한다
    }

    @Test
    public void selectFeedback_요청한_창작물의_피드백이_아니라_피드백_채택_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden request");

        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long creationWriterId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        User feedbackWriter = User.builder()
                .totalPointAmount(10.0)
                .currentPointAmount(10.0)
                .periodPointAmount(10.0)
                .build();
        feedbackWriter.setUserId(2L);

        Feedback feedback = new Feedback();
        feedback.setWriter(feedbackWriter);
        feedback.setCreationId(2L);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));

        // when : 피드백을 채택하면
        sut.selectFeedback(creationWriterId, creationId, feedbackId, dto);

        // then : 요청한 창작물의 피드백이 아니라 피드백 채택에 실패한다
    }

    @Test
    public void selectFeedback_존재하지_않은_창작물_작성자라_피드백_채택_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found writer");

        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long creationWriterId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        User feedbackWriter = User.builder()
                .totalPointAmount(10.0)
                .currentPointAmount(10.0)
                .periodPointAmount(10.0)
                .build();
        feedbackWriter.setUserId(2L);

        Feedback feedback = new Feedback();
        feedback.setWriter(feedbackWriter);
        feedback.setCreationId(creationId);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));
        when(userRepository.findByUserId(creationWriterId)).thenReturn(Optional.empty());

        // when : 피드백을 채택하면
        sut.selectFeedback(creationWriterId, creationId, feedbackId, dto);

        // then : 존재하지 않은 창작물 작성자라 피드백 채택에 실패한다
    }

    @Test
    public void selectFeedback_마감된_창작물이_아니라_피드백_채택_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden request");

        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long creationWriterId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        User creationWriter = User.builder()
                .build();
        creationWriter.setUserId(creationWriterId);

        User feedbackWriter = User.builder()
                .totalPointAmount(10.0)
                .currentPointAmount(10.0)
                .periodPointAmount(10.0)
                .build();
        feedbackWriter.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(creationWriter);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        Feedback feedback = new Feedback();
        feedback.setWriter(feedbackWriter);
        feedback.setCreationId(creationId);
        feedback.setCreation(creation);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));
        when(userRepository.findByUserId(creationWriterId)).thenReturn(Optional.of(creationWriter));

        // when : 피드백을 채택하면
        sut.selectFeedback(creationWriterId, creationId, feedbackId, dto);

        // then : 마감된 창작물이 아니라 피드백 채택에 실패한다
    }

    @Test
    public void selectFeedback_창작물_작성자가_아니라_피드백_채택_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden request");

        // given : 창작물 작성자 ID, 창작물 ID, 피드백 ID, 채택 데이터로
        long creationWriterId = 1L;
        long creationId = 1L;
        long feedbackId = 1L;
        FeedbackDto.Selection dto = new FeedbackDto.Selection();
        dto.setSelectionComment("comment");

        User creationWriter = User.builder()
                .build();
        creationWriter.setUserId(creationWriterId);

        User feedbackWriter = User.builder()
                .totalPointAmount(10.0)
                .currentPointAmount(10.0)
                .periodPointAmount(10.0)
                .build();
        feedbackWriter.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(feedbackWriter);
        creation.setRewardPoint(10.0);

        Feedback feedback = new Feedback();
        feedback.setWriter(feedbackWriter);
        feedback.setCreationId(creationId);
        feedback.setCreation(creation);
        creation.setStatus(Creation.Status.DEADLINE);

        when(feedbackRepository.findByFeedbackId(feedbackId)).thenReturn(Optional.of(feedback));
        when(userRepository.findByUserId(creationWriterId)).thenReturn(Optional.of(creationWriter));

        // when : 피드백을 채택하면
        sut.selectFeedback(creationWriterId, creationId, feedbackId, dto);

        // then : 창작물 작성자가 아니라 피드백 채택에 실패한다
    }
}
