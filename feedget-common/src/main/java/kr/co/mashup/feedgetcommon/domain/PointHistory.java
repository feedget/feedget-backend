package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;

/**
 * 포인트 히스토리
 * <p>
 * Created by ethan.kim on 2017. 12. 18..
 */
@Entity
@Table(name = "point_history")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {})
@EqualsAndHashCode(callSuper = false, of = "pointHistoryId")
public class PointHistory extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "point_history_id")
    private Long pointHistoryId;

    // Todo: relation mapping 추가
    // 포인트 지급자
    @Column(name = "giver_id", insertable = false, updatable = false)
    private Long giverId;

    // 포인트 수여자
    @Column(name = "receiver_id", insertable = false, updatable = false)
    private Long receiverId;

    // 포인트
    @Column(name = "point", nullable = false, columnDefinition = "DECIMAL(12,2) default 0")
    private Double point;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", length = 20)
    private Type type;

    // 포인트 타입
    public enum Type {
        // 피드백 기본 포인트
        FEEDBACK_BASIC,

        // 피드백 채택 보상 포인트
        FEEDBACK_REWARD;
    }
}
