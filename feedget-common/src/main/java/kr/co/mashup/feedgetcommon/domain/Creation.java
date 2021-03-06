package kr.co.mashup.feedgetcommon.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 창작물
 * <p>
 * Created by ethan.kim on 2017. 11. 4..
 */
@Entity
@Table(name = "creation")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"contents", "feedbacks"})
@EqualsAndHashCode(callSuper = false, of = "creationId")
public class Creation extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "creation_id")
    private Long creationId;

    // 제목
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    // 설명
    @Lob  // text type으로 사용하기 위해
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    // 창작물 상태
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    // 마감일 - ystem default 2주 후로 설정
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    // 보상 포인트
    @Column(name = "reward_point", nullable = false, columnDefinition = "DECIMAL(12,2) default 0")
    private Double rewardPoint;

    // 피드백 수
    @Column(name = "feedback_count", nullable = false, columnDefinition = "BIGINT(20) default 0")
    private Long feedbackCount;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_creation_to_writer_id"))
    private User writer;

    // 작성자 프로필 익명 여부
    @Column(name = "anonymity")
    @Type(type = "yes_no")
    private boolean anonymity;

    // 카테고리
    // 디자인, 회화, 글, 공예, 기타
    @ManyToOne(fetch = FetchType.LAZY)  // Creation(Many) : Category(One)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_creation_to_category_id"))
    private Category category;

    // 컨텐츠 정보 - 최대 10개
    @OneToMany(mappedBy = "creation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreationAttachedContent> contents;

    // 피드백 리스트
    // Todo: 필요없으면 제거
    @OneToMany(mappedBy = "creation")
    private List<Feedback> feedbacks;

    // 게시물 상태
    // Todo: 상태 코드 추가
    public enum Status {
        // 진행중
        PROCEEDING,

        // 마감
        DEADLINE;

//        평가 없음?
    }

    /**
     * 컨텐츠 추가
     *
     * @param content 추가할 창작물의 내용물
     */
    public void addAttachedContent(CreationAttachedContent content) {
        this.contents.add(content);
    }

    /**
     * 컨텐츠 제거
     */
    public void removeAttachedContent(CreationAttachedContent content) {
        this.contents.remove(content);
    }

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
     * 피드백이 작성되었는지 여부 조회
     *
     * @return 작성되었으면 true
     */
    public boolean hasFeedback() {
        return this.feedbackCount > 0;
    }

    /**
     * 마감인지 여부 조회
     *
     * @return 마감이면 true
     */
    public boolean isDeadline() {
        return this.status == Status.DEADLINE;
    }
}
