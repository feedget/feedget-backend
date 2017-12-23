package kr.co.mashup.feedgetapi.web.dto;

import lombok.Data;

/**
 * 카테고리의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2017. 12. 23..
 */
public class CategoryDto {

    @Data
    public static class Response {

        // 카테고리 이름
        private String name;
    }
}
