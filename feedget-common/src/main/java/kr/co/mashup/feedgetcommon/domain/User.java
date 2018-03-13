package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * 유저
 * <p>
 * Created by ethan.kim on 2017. 11. 4..
 */
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA에서 default constructor를 사용 가능, project에서 금지
@ToString(exclude = {"creations", "feedbacks"})
@EqualsAndHashCode(callSuper = false, of = "userId")
public class User extends AbstractEntity<Long> {

    @Setter  // TODO: 2018. 3. 12. TC에서 사용. 제거할 방법 고려
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    // uuid(universally unique identifier) - 외부로 노출되는 ID
    // Todo: 유저 생성시 중복값 체크 로직 추가
    @Column(name = "uuid", length = 32, nullable = false, unique = true)
    private String uuid;

    // 실명
    @Column(name = "real_name", length = 20, nullable = false)
    private String realName;

    // 닉네임
    @Column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    // 이메일
    @Column(name = "email", length = 30, nullable = false, unique = true)
    private String email;

    // cloud messaging(FCM) 전송 ID
    @Column(name = "cloud_msg_reg_id", length = 256)
    private String cloudMsgRegId;

    // 등급
    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_grade_cd", length = 20, nullable = false, columnDefinition = "varchar(20) default 'BRONZE'")
    private UserGrade userGrade;

    // 유저가 사용하고 있는 앱의 버전 코드
    @Column(name = "use_version_code", columnDefinition = "int(11) default '0'")
    private int useVersionCode;

    // 획득한 포인트 총액
    @Column(name = "total_point_amount", nullable = false, columnDefinition = "decimal(12,2) default '0.00'")
    private Double totalPointAmount;

    // 현재 보유 포인트 금액
    @Column(name = "current_point_amount", nullable = false, columnDefinition = "decimal(12,2) default '0.00'")
    private Double currentPointAmount;

    // 일정 기간동안 획득한 포인트 금액  Todo: 필요 없을시 제거
    @Column(name = "period_point_amount", nullable = false, columnDefinition = "decimal(12,2) default '0.00'")
    private Double periodPointAmount;

    // 피드백 작성 횟수
    @Column(name = "feedback_writing_count", nullable = false, columnDefinition = "int(11) default '0'")
    private int feedbackWritingCount;

    // 작성한 피드백 채택 횟수
    @Column(name = "feedback_selection_count", nullable = false, columnDefinition = "int(11) default '0'")
    private int feedbackSelectionCount;

    // Todo: 아래 필드 어떻게 할지...? 매번 계산할지, 결과 저장할지

    // 답변 채택률
    @Column(name = "feedback_selection_rate", nullable = false, columnDefinition = "decimal(12,2) default '0.00'")
    private Double feedbackSelectionRate;

    // 창작물 마감률
    @Column(name = "creation_deadline_rate", nullable = false, columnDefinition = "decimal(12,2) default '0.00'")
    private Double creationDeadlineRate;

    // Todo: 다른 social login 지원시 entity 분리 고려
    // Todo: 필요없을 시 제거
    // OAuth Access Token
    @Column(name = "oauth_token", length = 256)
    private String oAuthToken;

    // 가입한 OAuth Type(카톡, FB)
    @Enumerated(EnumType.STRING)  // enum 이름을 DB에 저장
    @Column(name = "oauth_type", length = 20)
    private OAuthType oAuthType;

    // 유저가 작성한 피드백
    @OneToMany(mappedBy = "writer")
    private List<Feedback> feedbacks;

    // 유저가 작성한 창작물
    @OneToMany(mappedBy = "writer")
    private List<Creation> creations;

    /**
     * oauth type 관리
     */
    public enum OAuthType {
        KAKAO,
        FB;
    }

    // TODO: 2018. 3. 12. 너무 field가 많다. domain 분리 필요
    @Builder
    public User(String uuid, String realName, String nickname, String email, String cloudMsgRegId, UserGrade userGrade,
                int useVersionCode, Double totalPointAmount, Double currentPointAmount, Double periodPointAmount,
                int feedbackWritingCount, int feedbackSelectionCount, Double feedbackSelectionRate, Double creationDeadlineRate,
                String oAuthToken, OAuthType oAuthType) {
        this.uuid = uuid;
        this.realName = realName;
        this.nickname = nickname;
        this.email = email;
        this.cloudMsgRegId = cloudMsgRegId;
        this.userGrade = userGrade;
        this.useVersionCode = useVersionCode;
        this.totalPointAmount = totalPointAmount;
        this.currentPointAmount = currentPointAmount;
        this.periodPointAmount = periodPointAmount;
        this.feedbackWritingCount = feedbackWritingCount;
        this.feedbackSelectionCount = feedbackSelectionCount;
        this.feedbackSelectionRate = feedbackSelectionRate;
        this.creationDeadlineRate = creationDeadlineRate;
        this.oAuthToken = oAuthToken;
        this.oAuthType = oAuthType;
    }

    /**
     * 유저 등급
     * Todo: 임시 naming이므로 수정 필요
     */
    public enum UserGrade {
        GOLD,
        SILVER,
        BRONZE;
    }

    public boolean isSameUser(User user) {
        if (user == null) {
            return false;
        }
        return this.userId.equals(user.userId);
    }

    public boolean changePoint(double point) {
        this.totalPointAmount += point;
        this.currentPointAmount += point;
        this.periodPointAmount += point;

        if (currentPointAmount < 0) {
            return false;
        }
        return true;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
