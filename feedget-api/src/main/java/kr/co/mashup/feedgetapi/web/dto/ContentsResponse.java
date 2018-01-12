package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.CreationContent;
import lombok.Data;

/**
 * Created by ethankim on 2017. 11. 5..
 */
@Data
public class ContentsResponse {

    private long contentId;

    private String url;

    /**
     * make Dto from Entity
     *
     * @param content Entity
     * @return
     */
    public static ContentsResponse fromContent(CreationContent content) {
        ContentsResponse contentDto = new ContentsResponse();
        contentDto.setContentId(content.getCreationContentId());
        contentDto.setUrl(content.getUrl());
        return contentDto;
    }
}
