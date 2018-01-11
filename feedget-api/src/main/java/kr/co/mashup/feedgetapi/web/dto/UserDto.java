package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.User;
import lombok.Data;

/**
 * 유저의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2018. 1. 10..
 */
public class UserDto {

    @Data
    public static class Response {

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
            userDto.setNickname(writer.getNickname());
            userDto.setGrade(writer.getUserGrade());

            return userDto;
        }
    }
}
