package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.Feedback;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 피드백의 데이터 전달을 담당한다
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
public class FeedbackDto {

    @Data
    public static class Create {

        // 피드백 내용
        @NotBlank
        @Size(min = 5)
        private String content;

        // 작성자 프로필 익명 여부
        private boolean anonymity;
    }

    @Data
    public static class Response {

        // ID
        private long feedbackId;

        // 피드백 내용
        private String content;

        // 컨텐츠
        private List<ContentsResponse> contents;

        // 피드백 작성자
        private UserDto.Response writer;

        // 작성자 프로필 익명 여부
        private boolean anonymity;

        // 피드백 채택 여부
        private boolean selection;

        // 피드백 채택 의견
        private String selectionComment;

        public static FeedbackDto.Response newResponse(Feedback feedback) {
            FeedbackDto.Response response = new FeedbackDto.Response();
            response.setFeedbackId(feedback.getFeedbackId());
            response.setContent(feedback.getContent());
            response.setWriter(UserDto.Response.fromUser(feedback.getWriter()));
            response.setAnonymity(feedback.isAnonymity());
            response.setSelection(feedback.isSelection());
            response.setSelectionComment(feedback.getSelectionComment());

            List<ContentsResponse> contents = feedback.getAttachedContents().stream()
                    .map(ContentsResponse::newResponse)
                    .collect(Collectors.toList());
            response.setContents(contents);

            return response;
        }
    }

    /**
     * 첨부 컨텐츠
     */
    @Data
    public static class AttachedContent {

        // 컨텐츠 타입
        @NotBlank
        @Size(min = 5, max = 6)
        private String contentsType;

        // 컨텐츠 파일 - 컨텐츠는 3개까지 게시할 수 있다
        @NotNull
        @Size(max = 3)
        private List<MultipartFile> files;
    }

    /**
     * 피드백 채택
     */
    @Data
    public static class Selection {

        // 피드백 채택 의견
        @NotBlank
        @Size(min = 3, max = 255)
        private String selectionComment;
    }
}
