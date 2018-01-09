package kr.co.mashup.feedgetapi.web.dto;

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
 * Todo: page -> offset, limit로 수정
 */
@Getter
@Setter
@ToString
public class DataListResponse<T> extends Response {

    private List<T> list;

    // page에 들어있는 item 수
    private int pageSize;

    // 페이지 번호
    private int pageNo;

    // item total count
    private long total;

    // pageNo total count
    private int pageTotal;

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
        this.pageSize = page.getSize();
        this.pageNo = page.getNumber();
        this.total = page.getTotalElements();  // 검색된 전체 data 수
        this.pageTotal = page.getTotalPages();  // 전체 페이지 수
    }
}
