package kr.co.mashup.feedgetapi.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 컨텐츠의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2017. 12. 29..
 */
@Data
public class ContentsDto {

    @NotBlank
    @Size(min = 5, max = 6)
    private String contentsType;

    // 컨텐츠는 10개까지 게시할 수 있다
    @NotNull
    @Size(max = 10)
    private List<MultipartFile> files;
}
