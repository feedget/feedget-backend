package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CategoryRepository;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private CreationService sut;

    @Test
    public void addCreation_창작물_추가_성공() {
        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setRewardPoint(10.0);
        dto.setCategory("design");

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
        dto.setRewardPoint(10.0);
        dto.setCategory("design");

        when(userRepository.findOne(userId)).thenReturn(null);

        // when : 창작물을 추가하면
        sut.addCreation(userId, dto);

        // then : 작성자가 없어 창작물이 추가되지 않는다
    }

    @Test(expected = InvalidParameterException.class)
    public void addCreation_창작물_추가_포인트_초과로_실패() {
        // given : 유저 ID, 추가할 창작물 데이터로
        long userId = 1L;
        CreationDto.Create dto = new CreationDto.Create();
        dto.setRewardPoint(10.0);
        dto.setCategory("design");

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
        dto.setRewardPoint(10.0);
        dto.setCategory("design");

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
}
