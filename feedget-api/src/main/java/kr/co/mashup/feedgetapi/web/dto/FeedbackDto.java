package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 피드백의 데이터 전달을 담당한다
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
public class FeedbackDto {

    @Data
    public static class Create {

        // 피드백 내용
        @NotBlank
        @Size(min = 5)
        private String description;

        // 작성자 프로필 익명 여부
        private boolean anonymity;
    }

    @Data
    public static class Response {

        // ID
        private long feedbackId;

        // 피드백 내용
        private String description;

        // 컨텐츠
        private List<ContentsResponse> contents;

        // 작성자 닉네임
        private String nickname;

        // 작성자 등급
        private User.UserGrade grade;

        // Todo: 채택 여부 추가?, 작성자 프로필 익명 여부 추가
    }
}
