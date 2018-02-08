package kr.co.mashup.feedgetcommon.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

/**
 * 피드백
 * <p>
 * Created by ethan.kim on 2017. 11. 4..
 */
@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"attachedContents"})
@EqualsAndHashCode(callSuper = false, of = "feedbackId")
public class Feedback extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "feedback_id")
    private Long feedbackId;

    // 피드백 내용
    @Column(name = "content", nullable = false)
    private String content;

    // 첨부 컨텐츠 - 최대 3개
    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackAttachedContent> attachedContents;

    // 작성자 ID(read only)
    @Column(name = "writer_id", nullable = false, insertable = false, updatable = false)
    private Long writerId;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)  // Feedback(Many) : User(One)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_feedback_to_writer_id"))
    private User writer;

    // 작성자 프로필 익명 여부
    @Column(name = "anonymity")
    @Type(type = "yes_no")
    private boolean anonymity;

    // 창작물 ID(read only)
    @Column(name = "creation_id", nullable = false, insertable = false, updatable = false)
    private Long creationId;

    // 창작물
    @ManyToOne(fetch = FetchType.LAZY)  // Feedback(Many) : Creation(One)
    @JoinColumn(name = "creation_id", foreignKey = @ForeignKey(name = "fk_feedback_to_creation_id"))
    private Creation creation;

    // 피드백 채택 여부
    @Column(name = "selection")
    @Type(type = "yes_no")
    private boolean selection;

    // 피드백 채택 의견
    @Column(name = "selection_comment", length = 255)
    private String selectionComment;

    /**
     * 작성자인지 여부 조회
     *
     * @param user 조회할 유저
     * @return 작성자면 true
     */
    public boolean isWritedBy(User user) {
        return this.writer.isSameUser(user);
    }

    /**
     * 컨텐츠 추가
     *
     * @param content 추가할 컨텐츠
     */
    public void addAttachedContent(FeedbackAttachedContent content) {
        this.attachedContents.add(content);
    }

    /**
     * 컨텐츠 제거
     */
    public void removeAttachedContent(FeedbackAttachedContent content) {
        this.attachedContents.remove(content);
    }

    /**
     * Creation의 피드백인지 조회
     *
     * @param creationId
     * @return
     */
    public boolean fromCreation(long creationId) {
        return this.creationId == creationId;
    }
}
