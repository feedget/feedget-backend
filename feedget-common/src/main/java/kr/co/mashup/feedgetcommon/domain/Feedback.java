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
@ToString(exclude = {})
@EqualsAndHashCode(callSuper = false, of = "feedbackId")
public class Feedback extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "feedback_id", columnDefinition = "INT(11)")
    private Long feedbackId;

    // 피드백 내용
    @Column(name = "description", nullable = false)
    private String description;

    // 컨텐츠 정보 - 최대 3개
    @OneToMany(mappedBy = "feedback")
    private List<FeedbackContents> contents;

    @ManyToOne(fetch = FetchType.LAZY)  // Feedback(Many) : User(One)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_feedback_to_writer_id"))
    private User writer;

    // 작성자 프로필 공개 여부 추가
    @Column(name = "anonymity")
    @Type(type = "yes_no")
    private boolean anonymity;

    @ManyToOne(fetch = FetchType.LAZY)  // Feedback(Many) : Creation(One)
    @JoinColumn(name = "creation_id", foreignKey = @ForeignKey(name = "fk_feedback_to_creation_id"))
    private Creation creation;

    // Todo: 채택 여부 추가??
    // Todo: 감사인사 추가
}
