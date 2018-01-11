package kr.co.mashup.feedgetapi.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
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

        // 디자인, 회화, 글, 공예, 기타
        @NotBlank
        @Size(min = 2)
        private String category;

        // 프로필 공개 여부(공개, 비공개)
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
        @JsonInclude(JsonInclude.Include.NON_NULL)
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

        // 창작물 작성자
        private UserDto.Response writer;

        /**
         * make Dto from Entity
         *
         * @param creation Entity
         * @return
         */
        public static CreationDto.Response fromCreation(Creation creation) {
            CreationDto.Response creationDto = new CreationDto.Response();
            creationDto.setCreationId(creation.getCreationId());
            creationDto.setTitle(creation.getTitle());
            creationDto.setDescription(creation.getDescription());
            creationDto.setDueDate(Timestamp.valueOf(creation.getDueDate()));
            creationDto.setRewardPoint(creation.getRewardPoint());
            creationDto.setStatus(creation.getStatus());
            creationDto.setFeedbackCount(creation.getFeedbackCount());

            UserDto.Response writer = UserDto.Response.fromUser(creation.getWriter());
            creationDto.setWriter(writer);

            List<ContentsResponse> contents = creation.getContents().stream()
                    .map(ContentsResponse::fromContent)
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
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String description;

        // 컨텐츠
        private List<ContentsResponse> contents;

        // 마감일
        private ZonedDateTime dueDate;

        // 창작물 작성일
        private ZonedDateTime writedDate;

        // 보상 포인트
        private double rewardPoint;

        // 게시물 상태
        private Creation.Status status;

        // 피드백 갯수
        private int feedbackCount;

        // 창작물 작성 여부
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        private boolean wroteCreation;

        // 창작물 게시자 닉네임
        private String nickname;

        // 창작물 게시자 등급
        private User.UserGrade grade;

        // 피드백 작성 여부
        private boolean wroteFeedback;
    }

    @Data
    public static class Update {

        @NotBlank
        @Size(min = 5)
        private String title;

        @NotBlank
        private String description;

        // 디자인, 회화, 글, 공예, 기타
        @NotBlank
        @Size(min = 2)
        private String category;

        // 프로필 공개 여부(공개, 비공개)
        private boolean anonymity;

        // 보상 포인트
        private double rewardPoint;
    }
}
