package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 1. 2..
 */
@RunWith(MockitoJUnitRunner.class)
public class CreationServiceTest {

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
    private CreationService sut;

    @Test
    public void addCreation_창작물_추가_성공() {
        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Category category = new Category();
        category.setName("design");

        Creation creation = new Creation();
        creation.setCreationId(1L);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(categoryRepository.findByName("design")).thenReturn(Optional.of(category));
        when(creationRepository.save(any(Creation.class))).thenReturn(creation);

        // when : 창작물을 추가하면
        long addedCreationId = sut.addCreation(userId, dto);

        // then : 창작물이 추가된다
        verify(creationRepository, times(1)).save(any(Creation.class));
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(addedCreationId, 1L);
    }

    @Test
    public void addCreation_창작물_추가_작성자가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found writer");

        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        when(userRepository.findOne(userId)).thenReturn(null);

        // when : 창작물을 추가하면
        sut.addCreation(userId, dto);

        // then : 작성자가 없어 창작물이 추가되지 않는다
    }

    @Test(expected = InvalidParameterException.class)
    public void addCreation_창작물_추가_보유_포인트_초과로_실패() {
        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(5.0);
        writer.setPeriodPoint(5.0);

        when(userRepository.findOne(userId)).thenReturn(writer);

        // when : 창작물을 추가하면
        sut.addCreation(userId, dto);

        // then : 포인트 초과로 창작물이 추가되지 않는다
    }

    @Test
    public void addCreation_창작물_추가_카테고리가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found category");

        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(categoryRepository.findByName("design")).thenReturn(Optional.empty());

        // when : 창작물을 추가하면
        sut.addCreation(userId, dto);

        // then : 카테고리가 없어 창작물이 추가되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_성공() {
        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        Category category = new Category();
        category.setName("design");

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(categoryRepository.findByName(dto.getCategory())).thenReturn(Optional.of(category));

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 창작물이 수정된다
        verify(userRepository, times(1)).save(any(User.class));
        verify(creationRepository, times(1)).save(any(Creation.class));
    }

    @Test
    public void modifyCreation_창작물_수정_작성자가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found writer");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        when(userRepository.findOne(userId)).thenReturn(null);

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 존재하지 않는 작성자라 창작물이 수정되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_창작물이_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 존재하지 않는 창작물이라 수정되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_작성자가_아니라_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("not match writer");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        User otherUser = new User();
        otherUser.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(otherUser);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 작성자가 아니라 창작물이 수정되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_게시기간_마감이라_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("creation is deadline");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.DEADLINE);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 게시기간 마감이라 창작물이 수정되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_피드백이_존재해_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden modify");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(1L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 피드백이 존재해 창작물이 수정되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_보유_포인트_초과로_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("exceed current point");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setAnonymity(true);
        dto.setRewardPoint(1000.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 존재하지 않는 카테고리라 창작물이 수정되지 않는다
    }

    @Test
    public void modifyCreation_창작물_수정_카테고리가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found category");

        // given : 유저 ID, 창작물 ID, 수정할 창작물 데이터로
        long userId = 1L;
        long creationId = 1L;
        CreationDto.Update dto = new CreationDto.Update();
        dto.setTitle("title");
        dto.setDescription("description");
        dto.setCategory("design");
        dto.setAnonymity(true);
        dto.setRewardPoint(10.0);

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));
        when(categoryRepository.findByName(dto.getCategory())).thenReturn(Optional.empty());

        // when : 창작물을 수정하면
        sut.modifyCreation(userId, creationId, dto);

        // then : 존재하지 않는 카테고리라 창작물이 수정되지 않는다
    }

    @Test
    public void removeCreation_창작물_삭제_성공() {
        // given : 유저 ID, 창작물 ID
        long userId = 1L;
        long creationId = 1L;

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 삭제하면
        sut.removeCreation(userId, creationId);

        // then : 창작물이 삭제된다
        verify(creationRepository, times(1)).findByCreationId(creationId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(creationRepository, times(1)).delete(creationId);
    }

    @Test
    public void removeCreation_창작물_삭제_작성자가_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found writer");

        // given : 유저 ID, 창작물 ID
        long userId = 1L;
        long creationId = 1L;

        when(userRepository.findOne(userId)).thenReturn(null);

        // when : 창작물을 삭제하면
        sut.removeCreation(userId, creationId);

        // then : 존재하지 않는 작성자라 창작물이 삭제되지 않는다
    }

    @Test
    public void removeCreation_창작물_삭제_창작물이_없어_실패() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("not found creation");

        // given : 유저 ID, 창작물 ID
        long userId = 1L;
        long creationId = 1L;

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());

        // when : 창작물을 삭제하면
        sut.removeCreation(userId, creationId);

        // then : 존재하지 않는 창작물이라 삭제되지 않는다
    }

    @Test
    public void removeCreation_창작물_삭제_작성자가_아니라_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("not match writer");

        // given : 유저 ID, 창작물 ID
        long userId = 1L;
        long creationId = 1L;

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        User otherUser = new User();
        otherUser.setUserId(2L);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(otherUser);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 삭제하면
        sut.removeCreation(userId, creationId);

        // then : 작성자가 아니라 창작물이 삭제되지 않는다
    }

    @Test
    public void removeCreation_창작물_삭제_게시기간_마감이라_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("creation is deadline");

        // given : 유저 ID, 창작물 ID
        long userId = 1L;
        long creationId = 1L;

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(0L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.DEADLINE);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 삭제하면
        sut.removeCreation(userId, creationId);

        // then : 게시기간 마감이라 창작물이 삭제되지 않는다
    }

    @Test
    public void removeCreation_창작물_삭제_피드백이_존재해_실패() {
        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("forbidden modify");

        // given : 유저 ID, 창작물 ID
        long userId = 1L;
        long creationId = 1L;

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setWriter(writer);
        creation.setFeedbackCount(1L);
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);

        when(userRepository.findOne(userId)).thenReturn(writer);
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.of(creation));

        // when : 창작물을 삭제하면
        sut.removeCreation(userId, creationId);

        // then : 피드백이 존재해 창작물이 삭제되지 않는다
    }

    @Test
    public void readCreations_창작물_리스트_조회_모든_창작물_조회_성공() throws Exception {
        // given : 유저 ID, 카테고리 이름, 페이지 정보로
        long userId = 1L;
        String category = "ALL";
        Pageable pageable = new PageRequest(0, 10);

        User user = new User();
        user.setUserId(userId);
        user.setCurrentPoint(100.0);
        user.setPeriodPoint(100.0);

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

        User user = new User();
        user.setUserId(userId);
        user.setCurrentPoint(100.0);
        user.setPeriodPoint(100.0);

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

        User user = new User();
        user.setUserId(userId);
        user.setCurrentPoint(100.0);
        user.setPeriodPoint(100.0);

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

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

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

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

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

        User writer = new User();
        writer.setUserId(userId);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(writer));
        when(creationRepository.findByCreationId(creationId)).thenReturn(Optional.empty());
        when(feedbackRepository.findByCreationIdAndWriterId(creationId, userId)).thenReturn(Optional.empty());

        // when : 창작물을 조회하면
        CreationDto.DetailResponse detailResponses = sut.readCreation(userId, creationId);

        // then : 창작물이 없어 창작물이 조회되지 않는다
    }
}
