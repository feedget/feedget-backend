package kr.co.mashup.feedgetapi.web.dto;

import lombok.Data;

/**
 * 알림의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2017. 12. 23..
 */
public class NotificationDto {

    @Data
    public static class Response {

        // 설명
        private String description;

        // Todo: clock interaction에 따른 추가 정보 ex. creationId
    }
}
