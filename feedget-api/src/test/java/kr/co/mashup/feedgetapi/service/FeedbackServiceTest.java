package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
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

    @InjectMocks
    private FeedbackService sut;

    @Test
    public void readFeedbacks_피드백_리스트_조회_0page_조회_성공() {
        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        User user = new User();
        user.setUserId(userId);

        Creation creation = new Creation();

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(1L);
        feedback.setDescription("description");
        feedback.setWriter(user);
        feedback.setAnonymity(true);
        feedback.setSelection(true);
        feedback.setContents(Collections.emptyList());

        Page<Feedback> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.findByCreationIdAndSelectionIsFalse(creationId, pageable)).thenReturn(page);
        when(feedbackRepository.findByCreationIdAndSelectionIsTrue(creationId)).thenReturn(Optional.of(feedback));

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbacks = sut.readFeedbacks(userId, creationId, pageable, cursor);

        // then : 피드백 리스트가 조회된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(feedbackRepository, times(1)).findByCreationIdAndWriterId(creationId, userId);
        verify(feedbackRepository, times(1)).findByCreationIdAndSelectionIsFalse(creationId, pageable);
        verify(feedbackRepository, times(1)).findByCreationIdAndSelectionIsTrue(creationId);
    }

    @Test
    public void readFeedbacks_피드백_리스트_조회_1page_조회_성공() {
        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(1, 10);
        Long cursor = null;

        User user = new User();
        user.setUserId(userId);

        Creation creation = new Creation();

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(1L);
        feedback.setDescription("description");
        feedback.setWriter(user);
        feedback.setAnonymity(true);
        feedback.setSelection(true);
        feedback.setContents(Collections.emptyList());

        Page<Feedback> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.findByCreationIdAndSelectionIsFalse(creationId, pageable)).thenReturn(page);

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbacks = sut.readFeedbacks(userId, creationId, pageable, cursor);

        // then : 피드백 리스트가 조회된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(feedbackRepository, times(1)).findByCreationIdAndWriterId(creationId, userId);
        verify(feedbackRepository, times(1)).findByCreationIdAndSelectionIsFalse(creationId, pageable);
        verify(feedbackRepository, never()).findByCreationIdAndSelectionIsTrue(creationId);
    }

    @Test
    public void readFeedbacks_피드백_리스트_조회_유저가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found user");

        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbacks = sut.readFeedbacks(userId, creationId, pageable, cursor);

        // then : 유저가 없어 피드백 리스트가 조회되지 않는다
    }

    @Test
    public void readFeedbacks_피드백_리스트_조회_창작물이_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        User user = new User();
        user.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbacks = sut.readFeedbacks(userId, creationId, pageable, cursor);

        // then : 창작물이 없어 피드백 리스트가 조회되지 않는다
    }

    @Test
    public void readFeedbacks_피드백_리스트_조회_자신의_피드백이_없어서_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found feedback");

        // given : 유저 ID, 창작물 ID, 페이지 정보, 커서로
        long userId = 1L;
        long creationId = 1L;
        Pageable pageable = new PageRequest(0, 10);
        Long cursor = null;

        User user = new User();
        user.setUserId(userId);

        Creation creation = new Creation();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 피드백 리스트를 조회하면
        List<FeedbackDto.Response> feedbacks = sut.readFeedbacks(userId, creationId, pageable, cursor);

        // then : 자신의 피드백이 없어 피드백 리스트가 조회되지 않는다
    }
}