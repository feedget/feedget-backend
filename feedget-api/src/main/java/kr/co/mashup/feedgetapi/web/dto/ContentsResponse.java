package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.CreationAttachedContent;
import kr.co.mashup.feedgetcommon.domain.FeedbackAttachedContent;
import lombok.Data;

/**
 * Created by ethankim on 2017. 11. 5..
 */
@Data
public class ContentsResponse {

    private long contentId;

    private String url;

    /**
     * make Response from Entity
     *
     * @param content CreationAttachedContent
     * @return
     */
    public static ContentsResponse newResponse(CreationAttachedContent content) {
        ContentsResponse contentDto = new ContentsResponse();
        contentDto.setContentId(content.getCreationAttachedContentId());
        contentDto.setUrl(content.getUrl());
        return contentDto;
    }

    /**
     * make Response from FeedbackAttachedContent
     *
     * @param content FeedbackAttachedContent
     * @return
     */
    public static ContentsResponse newResponse(FeedbackAttachedContent content) {
        ContentsResponse contentDto = new ContentsResponse();
        contentDto.setContentId(content.getFeedbackAttachedContentId());
        contentDto.setUrl(content.getUrl());
        return contentDto;
    }
}
