package kr.co.mashup.feedgetapi.web;

import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ethan.kim on 2018. 1. 15..
 */
@RunWith(MockitoJUnitRunner.class)
public class CreationUpdateValidatorTest {

    @Mock
    private BindingResult result;

    @InjectMocks
    private CreationUpdateValidator sut;

    @Test
    public void supports_검증할_수_있는_타입인지_확인_성공() {
        // given : CreationDto.Update를
        CreationDto.Update update = new CreationDto.Update();

        // when : 검증할 수 있는 타입인지 확인하면
        boolean support = sut.supports(update.getClass());

        // then : 검증할 수 있다
        assertTrue(support);
    }

    @Test
    public void supports_검증할_수_있는_타입인지_확인_실패() {
        // given : CreationDto.Create를
        CreationDto.Create create = new CreationDto.Create();

        // when : 검증할 수 있는 타입인지 확인하면
        boolean support = sut.supports(create.getClass());

        // then : 검증할 수 없다
        assertFalse(support);
    }

    @Test
    public void validate_필드_검증_성공() {
        // given : 정상적인 Update 객체로
        CreationDto.Update update = new CreationDto.Update();
        update.setTitle("title");
        update.setDescription("description");
        update.setCategory("design");

        // when : 필드를 검증하면
        sut.validate(update, result);

        // then : 에러코드가 발생하지 않는다
        verify(result, never()).rejectValue(anyString(), anyString());
    }

    @Test
    public void validate_필드_검증_공백_제목() {
        // given : 공백인 제목만 가지고 있는 Update 객체로
        CreationDto.Update update = new CreationDto.Update();
        update.setTitle("");

        ArgumentCaptor<String> fieldArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorCodeArg = ArgumentCaptor.forClass(String.class);

        // when : 필드를 검증하면
        sut.validate(update, result);

        // then : 공백 문자열이라 에러코드 발생
        verify(result, times(1)).rejectValue(fieldArg.capture(), errorCodeArg.capture());

        assertEquals(fieldArg.getValue(), "title");
        assertEquals(errorCodeArg.getValue(), "field is not blank");
    }

    @Test
    public void validate_필드_검증_4자리_제목() {
        // given : 4자리 제목만 가지고 있는 Update 객체로
        CreationDto.Update update = new CreationDto.Update();
        update.setTitle("titl");

        ArgumentCaptor<String> fieldArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorCodeArg = ArgumentCaptor.forClass(String.class);

        // when : 필드를 검증하면
        sut.validate(update, result);

        // then : 자리수 부족으로 에러코드 발생
        verify(result, times(1)).rejectValue(fieldArg.capture(), errorCodeArg.capture());

        assertEquals(fieldArg.getValue(), "title");
        assertEquals(errorCodeArg.getValue(), "field is not blank");
    }

    @Test
    public void validate_필드_검증_공백_설명() {
        // given : 공백인 설명만 가지고 있는 Update 객체로
        CreationDto.Update update = new CreationDto.Update();
        update.setDescription("");

        ArgumentCaptor<String> fieldArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorCodeArg = ArgumentCaptor.forClass(String.class);

        // when : 필드를 검증하면
        sut.validate(update, result);

        // then : 공백 문자열이라 에러코드 발생
        verify(result, times(1)).rejectValue(fieldArg.capture(), errorCodeArg.capture());

        assertEquals(fieldArg.getValue(), "description");
        assertEquals(errorCodeArg.getValue(), "field is not blank");
    }

    @Test
    public void validate_필드_검증_공백_카테고리() {
        // given : 공백인 카테고리만 가지고 있는 Update 객체로
        CreationDto.Update update = new CreationDto.Update();
        update.setCategory("");

        ArgumentCaptor<String> fieldArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorCodeArg = ArgumentCaptor.forClass(String.class);

        // when : 필드를 검증하면
        sut.validate(update, result);

        // then : 공백 문자열이라 에러코드 발생
        verify(result, times(1)).rejectValue(fieldArg.capture(), errorCodeArg.capture());

        assertEquals(fieldArg.getValue(), "category");
        assertEquals(errorCodeArg.getValue(), "field is not blank");
    }

    @Test
    public void validate_필드_검증_1자리_카테고리() {
        // given : 1자리인 카테고리만 가지고 있는 Update 객체로
        CreationDto.Update update = new CreationDto.Update();
        update.setCategory("a");

        ArgumentCaptor<String> fieldArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorCodeArg = ArgumentCaptor.forClass(String.class);

        // when : 필드를 검증하면
        sut.validate(update, result);

        // then : 빈 문자열이라 에러코드 발생
        verify(result, times(1)).rejectValue(fieldArg.capture(), errorCodeArg.capture());

        assertEquals(fieldArg.getValue(), "category");
        assertEquals(errorCodeArg.getValue(), "field is not blank");
    }
}
