package kr.co.mashup.feedgetapi.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
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

    @Nullable
    private List<MultipartFile> files;
}
