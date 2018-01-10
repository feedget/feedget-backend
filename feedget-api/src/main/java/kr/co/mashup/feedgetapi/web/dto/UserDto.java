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

        // 창작물 작성 여부
        private boolean sameUser;

        /**
         * make Dto from Entity
         *
         * @param writer Entity
         * @param user
         * @return
         */
        public static UserDto.Response fromUser(User writer, User user) {
            UserDto.Response userDto = new UserDto.Response();
            userDto.setNickname(writer.getNickname());
            userDto.setGrade(writer.getUserGrade());
            userDto.setSameUser(writer.isSameUser(user));

            return userDto;
        }
    }
}
