package kr.co.mashup.feedgetapi.service;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
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
