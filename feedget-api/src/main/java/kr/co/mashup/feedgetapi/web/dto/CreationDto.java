package kr.co.mashup.feedgetapi.web.dto;

import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 창작물의 데이터 전달을 담당한다
 * <p>
 * Created by ethan.kim on 2017. 11. 5..
 */
public class CreationDto {

    @Data
    public static class Create {

        @NotBlank
        @Size(min = 5)
        private String title;

        @NotBlank
        private String description;

        // 카테고리 - 디자인, 회화, 글, 공예, 기타
        @NotBlank
        @Size(min = 2)
        private String category;

        // 작성자 프로필 익명 여부
        private boolean anonymity;

        // 보상 포인트
        private double rewardPoint;
    }

    @Data
    public static class Response {

        // ID
        private long creationId;

        // 제목
        private String title;

        // 설명
        private String description;

        // 컨텐츠
        private List<ContentsResponse> contents;

        // 마감일
        private Timestamp dueDate;

        // 보상 포인트
        private double rewardPoint;

        // 게시물 상태
        private Creation.Status status;

        // 피드백 갯수
        private long feedbackCount;

        // 작성자 프로필 익명 여부
        private boolean anonymity;

        // 창작물 작성자
        private UserDto.Response writer;

        /**
         * make Dto from Entity
         *
         * @param creation Entity
         * @return
         */
        public static CreationDto.Response newResponse(Creation creation) {
            CreationDto.Response creationDto = new CreationDto.Response();
            creationDto.setCreationId(creation.getCreationId());
            creationDto.setTitle(creation.getTitle());
            creationDto.setDescription(creation.getDescription());
            creationDto.setDueDate(Timestamp.valueOf(creation.getDueDate()));
            creationDto.setRewardPoint(creation.getRewardPoint());
            creationDto.setStatus(creation.getStatus());
            creationDto.setFeedbackCount(creation.getFeedbackCount());
            creationDto.setAnonymity(creation.isAnonymity());

            UserDto.Response writer = UserDto.Response.fromUser(creation.getWriter());
            creationDto.setWriter(writer);

            List<ContentsResponse> contents = creation.getContents().stream()
                    .map(ContentsResponse::newResponse)
                    .collect(Collectors.toList());
            creationDto.setContents(contents);

            return creationDto;
        }
    }

    @Data
    public static class DetailResponse {

        // ID
        private long creationId;

        // 제목
        private String title;

        // 설명
        private String description;

        // 컨텐츠
        private List<ContentsResponse> contents;

        // 마감일
        private Timestamp dueDate;

        // 창작물 작성일
        private Timestamp writedDate;

        // 보상 포인트
        private double rewardPoint;

        // 게시물 상태
        private Creation.Status status;

        // 피드백 갯수
        private long feedbackCount;

        // 작성자 프로필 익명 여부
        private boolean anonymity;

        // 창작물 작성자
        private UserDto.Response writer;

        // 피드백 작성 여부
        private boolean wroteFeedback;

        public static CreationDto.DetailResponse newDetailResponse(Creation creation, Optional<Feedback> feedbackOp) {
            CreationDto.DetailResponse detail = new CreationDto.DetailResponse();
            detail.setCreationId(creation.getCreationId());
            detail.setTitle(creation.getTitle());
            detail.setDescription(creation.getDescription());
            detail.setDueDate(Timestamp.valueOf(creation.getDueDate()));
            detail.setWritedDate(creation.getCreatedTimestamp());
            detail.setRewardPoint(creation.getRewardPoint());
            detail.setStatus(creation.getStatus());
            detail.setFeedbackCount(creation.getFeedbackCount());
            detail.setAnonymity(creation.isAnonymity());
            detail.setWriter(UserDto.Response.fromUser(creation.getWriter()));
            detail.setWroteFeedback(feedbackOp.isPresent());

            List<ContentsResponse> contents = creation.getContents().stream()
                    .map(ContentsResponse::newResponse)
                    .collect(Collectors.toList());
            detail.setContents(contents);

            return detail;
        }
    }

    @Data
    public static class Update {

        // 제목
        private String title;

        // 설명
        private String description;

        // 카테고리 - 디자인, 회화, 글, 공예, 기타
        private String category;

        // 작성자 프로필 익명 여부
        private Boolean anonymity;

        // 보상 포인트
        private Double rewardPoint;
    }

    /**
     * 첨부 컨텐츠
     */
    @Data
    public static class AttachedContent {

        @NotBlank
        @Size(min = 5, max = 6)
        private String contentsType;

        // 컨텐츠는 10개까지 게시할 수 있다
        @NotNull
        @Size(max = 10)
        private List<MultipartFile> files;
    }
}
