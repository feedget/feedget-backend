package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;

/**
 * 알림
 * <p>
 * Created by ethan.kim on 2017. 12. 15..
 */
@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {})
@EqualsAndHashCode(callSuper = false, of = "notificationId")
public class Notification extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "notification_id", columnDefinition = "INT(11)")
    private Long notificationId;

    // 알림 대상자
    @Column(name = "receiver_id", columnDefinition = "INT(11)")
    private Long receiverId;

    // 설명
    @Column(name = "description", nullable = false)
    private String description;
}
