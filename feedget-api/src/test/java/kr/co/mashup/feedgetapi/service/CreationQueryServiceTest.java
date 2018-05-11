package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CategoryRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 5. 12..
 */
@RunWith(MockitoJUnitRunner.class)
public class CreationQueryServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CreationRepository creationRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private CreationQueryService sut;

    @Test
    public void readCreations_창작물_리스트_조회_모든_창작물_조회_성공() throws Exception {
        // given : 유저 ID, 카테고리 이름, 페이지 정보로
        long userId = 1L;
        String category = "ALL";
        Pageable pageable = new PageRequest(0, 10);

        User user = User.builder()
                .totalPointAmount(100.0)
                .currentPointAmount(100.0)
                .periodPointAmount(100.0)
                .build();
        user.setUserId(userId);

        Page<Creation> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(creationRepository.findAll(pageable)).thenReturn(page);

        // when : 창작물 리스트를 조회하면
        Page<CreationDto.Response> creationPage = sut.readCreations(userId, category, pageable);

        // then : 창작물 리스트가 조회된다
        verify(creationRepository, times(1)).findAll(pageable);
        verify(categoryRepository, never()).findByName(anyString());
        verify(creationRepository, never()).findByCategory(any(Category.class), eq(pageable));
    }

    @Test
    public void readCreations_창작물_리스트_조회_특정_카테고리_조회_성공() throws Exception {
        // given : 유저 ID, 카테고리 이름, 페이지 정보로
        long userId = 1L;
        String categoryName = "design";
        Pageable pageable = new PageRequest(0, 10);

        User user = User.builder()
                .totalPointAmount(100.0)
                .currentPointAmount(100.0)
                .periodPointAmount(100.0)
                .build();
        user.setUserId(userId);

        Page<Creation> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Category category = new Category();
        category.setName(categoryName);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(creationRepository.findByCategory(category, pageable)).thenReturn(page);

        // when : 창작물 리스트를 조회하면
        Page<CreationDto.Response> creationPage = sut.readCreations(userId, categoryName, pageable);

        // then : 창작물 리스트가 조회된다
        verify(creationRepository, never()).findAll(pageable);
        verify(categoryRepository, times(1)).findByName(anyString());
        verify(creationRepository, times(1)).findByCategory(any(Category.class), eq(pageable));
    }

    @Test
    public void readCreations_창작물_리스트_조회_유저가_없어_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found user");

        // given : 유저 ID, 카테고리 이름, 페이지 정보로
        long userId = 1L;
        String categoryName = "design";
        Pageable pageable = new PageRequest(0, 10);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 창작물 리스트를 조회하면
        Page<CreationDto.Response> creationPage = sut.readCreations(userId, categoryName, pageable);

        // then : 유저가 없어 창작물 리스트가 조회되지 않는다
    }

    @Test
    public void readCreations_창작물_리스트_조회_특정_카테고리_조회_카테고리가_없어_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found category");

        // given : 유저 ID, 카테고리 이름, 페이지 정보로
        long userId = 1L;
        String categoryName = "design";
        Pageable pageable = new PageRequest(0, 10);

        User user = User.builder()
                .totalPointAmount(100.0)
                .currentPointAmount(100.0)
                .periodPointAmount(100.0)
                .build();
        user.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // when : 창작물 리스트를 조회하면
        Page<CreationDto.Response> creationPage = sut.readCreations(userId, categoryName, pageable);

        // then : 카테고리가 없어 창작물 리스트가 조회되지 않는다
    }

    @Test
    public void readCreation_창작물_단건_조회_피드백을_작성한_창작물_조회_성공() throws Exception {
        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;

        User writer = User.builder()
                .totalPointAmount(100.0)
                .currentPointAmount(100.0)
                .periodPointAmount(100.0)
                .build();
        writer.setUserId(userId);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(1L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);
        creation.setDueDate(LocalDateTime.now());
        creation.setContents(Collections.emptyList());

        Feedback feedback = new Feedback();
        feedback.setWriterId(userId);
        feedback.setCreationId(creationId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(writer));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.of(feedback));

        // when : 창작물을 조회하면
        CreationDto.DetailResponse detailResponses = sut.readCreation(userId, creationId);

        // then : 피드백을 작성한 창작물이 조회된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(feedbackRepository, times(1)).findByCreationIdAndWriterId(creationId, userId);

        assertTrue(detailResponses.isWroteFeedback());
    }

    @Test
    public void readCreation_창작물_단건_조회_피드백을_작성하지_않은_창작물_조회_성공() throws Exception {
        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;

        User writer = User.builder()
                .totalPointAmount(100.0)
                .currentPointAmount(100.0)
                .periodPointAmount(100.0)
                .build();
        writer.setUserId(userId);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(1L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);
        creation.setDueDate(LocalDateTime.now());
        creation.setContents(Collections.emptyList());

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(writer));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 창작물을 조회하면
        CreationDto.DetailResponse detailResponses = sut.readCreation(userId, creationId);

        // then : 피드백을 작성하지 않은 창작물이 조회된다
        verify(userRepository, times(1)).findByUserId(userId);
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(feedbackRepository, times(1)).findByCreationIdAndWriterId(creationId, userId);

        assertFalse(detailResponses.isWroteFeedback());
    }

    @Test
    public void readCreation_창작물_단건_조회_유저가_없어_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found user");

        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when : 창작물을 조회하면
        CreationDto.DetailResponse detailResponses = sut.readCreation(userId, creationId);

        // then : 유저가 없어 창작물이 조회되지 않는다
    }

    @Test
    public void readCreation_창작물_단건_조회_창작물이_없어_실패() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID로
        long userId = 1L;
        long creationId = 1L;

        User writer = User.builder()
                .totalPointAmount(100.0)
                .currentPointAmount(100.0)
                .periodPointAmount(100.0)
                .build();
        writer.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(writer));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 창작물을 조회하면
        CreationDto.DetailResponse detailResponses = sut.readCreation(userId, creationId);

        // then : 창작물이 없어 창작물이 조회되지 않는다
    }
}