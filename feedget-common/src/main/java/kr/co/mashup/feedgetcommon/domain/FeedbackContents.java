package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;

/**
 * 피드백 - 컨텐츠
 * <p>
 * Created by ethan.kim on 2017. 12. 19..
 */
@Entity
@Table(name = "feedback_contents")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
public class FeedbackContents extends AbstractEntity<FeedbackContents.Id> {

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY)  // FeedbackContents(Many) : Feedback(One)
    @MapsId(value = "feedbackId")
    private Feedback feedback;

    @OneToOne(fetch = FetchType.LAZY)  // FeedbackContents(Many) : Contents(One)
    @MapsId(value = "contentsId")
    private Contents contents;

    public FeedbackContents(Feedback feedback, Contents contents) {
        this.id.feedbackId = feedback.getFeedbackId();
        this.feedback = feedback;
        this.id.contentsId = contents.getContentsId();
        this.contents = contents;
    }

    @Embeddable
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode(callSuper = false, of = {"feedbackId", "contentsId"})
    public static class Id extends AbstractEntityId {

        @Column(name = "feedback_id", columnDefinition = "INT(11)")
        private Long feedbackId;

        @Column(name = "contents_id", columnDefinition = "INT(11)")
        private Long contentsId;
    }
}
