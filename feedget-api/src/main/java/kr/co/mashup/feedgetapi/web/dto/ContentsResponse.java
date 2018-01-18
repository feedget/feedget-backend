package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.CreationContent;
import kr.co.mashup.feedgetcommon.domain.FeedbackContent;
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
     * @param content CreationContent
     * @return
     */
    public static ContentsResponse newResponse(CreationContent content) {
        ContentsResponse contentDto = new ContentsResponse();
        contentDto.setContentId(content.getCreationContentId());
        contentDto.setUrl(content.getUrl());
        return contentDto;
    }

    /**
     * make Response from FeedbackContent
     *
     * @param content FeedbackContent
     * @return
     */
    public static ContentsResponse newResponse(FeedbackContent content) {
        ContentsResponse contentDto = new ContentsResponse();
        contentDto.setContentId(content.getFeedbackContentId());
        contentDto.setUrl(content.getUrl());
        return contentDto;
    }
}
