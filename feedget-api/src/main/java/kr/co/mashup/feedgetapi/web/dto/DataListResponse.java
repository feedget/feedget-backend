package kr.co.mashup.feedgetapi.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 응답코드/메시지 + 여러개의 데이터(엔티티, VO)가 있는 경우
 * <p>
 * Created by ethan.kim on 2017. 12. 21..
 * Todo: page -> offset, limit로 수정?
 */
@Getter
@Setter
@ToString
public class DataListResponse<T> extends Response {

    @JsonIgnore
    private static final int NOT_EXIST_NEXT_PAGE = -1;

    private List<T> list;

    private long nextPage;

    public DataListResponse(int resultCode, String message) {
        super(resultCode, message);
    }

    public DataListResponse(List<T> list) {
        this(HttpStatus.OK.value(), "success");
        this.list = list;
    }

    public DataListResponse(Page<T> page) {
        this(HttpStatus.OK.value(), "success");
        this.list = page.getContent();  // 검색된 데이터
        this.nextPage = NOT_EXIST_NEXT_PAGE;

        if (page.hasNext()) {
            this.nextPage = page.getNumber() + 1;
        }
    }
}
