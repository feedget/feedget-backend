package kr.co.mashup.feedgetapi.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * 응답코드/메시지 + 1개의 데이터(엔티티, VO)가 있는 경우
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
@Setter
@Getter
@ToString
public class DataResponse<T> extends Response {

    private T item;

    public DataResponse(Integer resultCode) {
        super(resultCode);
    }

    public DataResponse(Integer resultCode, String message) {
        super(resultCode, message);
    }

    public DataResponse(T item) {
        this(HttpStatus.OK.value(), "success");
        this.item = item;
    }
}
