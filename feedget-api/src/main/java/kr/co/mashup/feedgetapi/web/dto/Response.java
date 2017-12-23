package kr.co.mashup.feedgetapi.web.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * 응답 처리를 위한 클래스
 * 응답코드/메시지만 있는 경우
 * <p>
 * Created by ethan.kim on 2017. 12. 21..
 */
@Getter
@Setter
@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(value = JsonInclude.Include.ALWAYS)
public class Response {

    @JsonProperty
    private int status;

    @JsonProperty
    private String message;

    public Response(int status) {
        this.status = status;
    }

    public Response(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Response ok() {
        return new Response(HttpStatus.OK.value(), "success");
    }

    public static Response created() {
        return new Response(HttpStatus.CREATED.value(), "created");
    }

    public static Response noContent() {
        return new Response(HttpStatus.NO_CONTENT.value(), "no content");
    }
}
