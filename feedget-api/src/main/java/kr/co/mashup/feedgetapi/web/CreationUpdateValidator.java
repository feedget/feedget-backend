package kr.co.mashup.feedgetapi.web;

import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Creation Update에 대한 validation을 담당
 * <p>
 * Created by ethan.kim on 2018. 1. 15..
 */
@Component
public class CreationUpdateValidator implements Validator {

    /**
     * 검증할 수 있는 오브젝트 타입인지를 확인
     *
     * @param clazz any class
     * @return CreationDto.Update면 true
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return CreationDto.Update.class.isAssignableFrom(clazz);
    }

    /**
     * supports(Class<?> clazz)가 true일 경우 호출
     *
     * @param target
     * @param errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        CreationDto.Update update = (CreationDto.Update) target;

        if (update.getTitle() != null) {
            String title = update.getTitle();

            if (StringUtils.isBlank(title) || StringUtils.length(title) < 5) {
                errors.rejectValue("title", "field is not blank");
            }
        }

        if (update.getDescription() != null) {
            String description = update.getDescription();

            if (StringUtils.isBlank(description)) {
                errors.rejectValue("description", "field is not blank");
            }
        }

        if (update.getCategory() != null) {
            String category = update.getCategory();

            if (StringUtils.isBlank(category) || StringUtils.length(category) < 2) {
                errors.rejectValue("category", "field is not blank");
            }
        }
    }
}
