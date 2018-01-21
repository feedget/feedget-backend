package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.User;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 유저의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2018. 1. 10..
 */
public class UserDto {

    @Data
    public static class SignIn {

        @NotBlank
        private String realName;

        @NotBlank
        @Size(min = 2, max = 10)
        private String nickname;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String oAuthToken;

        @NotNull
        private User.OAuthType oAuthType;
    }

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
}
