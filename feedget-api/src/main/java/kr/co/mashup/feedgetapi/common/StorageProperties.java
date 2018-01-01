package kr.co.mashup.feedgetapi.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * storage관련 prooerties를 관리
 * <p>
 * Created by ethankim on 2017. 11. 4..
 */
@Component
//@ConfigurationProperties(prefix = "storage")
@Getter
public class StorageProperties {

    @NotNull
    @Value("${storage.path}")
    private String path;

    @NotNull
    @Value("${storage.uri}")
    private String uri;
}
