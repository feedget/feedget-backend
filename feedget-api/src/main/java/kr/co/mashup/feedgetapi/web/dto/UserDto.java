package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * 유저의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2018. 1. 10..
 */
public class UserDto {

    @Data
    public static class Response {

        // 유저 ID
        private String userId;

        // 닉네임
        private String nickname;

        // 등급
        private User.UserGrade grade;

        /**
         * make Dto from Entity
         *
         * @param writer Entity
         * @return
         */
        public static UserDto.Response fromUser(User writer) {
            UserDto.Response userDto = new UserDto.Response();
            userDto.setUserId(writer.getUuid());
            userDto.setNickname(writer.getNickname());
            userDto.setGrade(writer.getUserGrade());

            return userDto;
        }
    }

    @Data
    public static class DetailResponse {

        // 유저 ID
        private String userId;

        // 닉네임
        private String nickname;

        // 등급
        private User.UserGrade grade;

        // 현재 보유 포인트 금액
        private Double currentPointAmount;

        // 답변 채택률
        private Double feedbackSelectionRate;

        // 창작물 마감률
        private Double creationDeadlineRate;

        /**
         * make Dto from Entity
         *
         * @param writer Entity
         * @return
         */
        public static UserDto.DetailResponse newDetailResponse(User writer) {
            UserDto.DetailResponse userDto = new UserDto.DetailResponse();
            userDto.setUserId(writer.getUuid());
            userDto.setNickname(writer.getNickname());
            userDto.setGrade(writer.getUserGrade());
            userDto.setCurrentPointAmount(writer.getCurrentPointAmount());
            userDto.setFeedbackSelectionRate(writer.getFeedbackSelectionRate());
            userDto.setCreationDeadlineRate(writer.getCreationDeadlineRate());

            return userDto;
        }
    }

    @Data
    public static class UpdateNickname {

        @NotBlank
        @Size(min = 2, max = 10)
        private String nickname;
    }
}
