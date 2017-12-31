package kr.co.mashup.feedgetcommon.domain;

import kr.co.mashup.feedgetcommon.domain.code.ContentType;
import lombok.*;

import javax.persistence.*;

/**
 * 창작물의 내용물
 * <p>
 * Created by ethan.kim on 2017. 12. 19..
 */
@Entity
@Table(name = "creation_content")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false, of = "creationContentId")
public class CreationContent extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "creation_content_id", columnDefinition = "INT(11)")
    private Long creationContentId;

    //Todo: length 조정. 중복방지용, 36byte(32글자 + 확장자)
    @Column(name = "file_name", length = 255, nullable = false, unique = true)
    private String fileName;  // 업로드한 이미지 파일 이름(서버에서 중복되지 않게 재생성)

    // Todo: length 조정. 260byte(window 최대 256글자 + 확장자)
    @Column(name = "original_file_name", length = 255, nullable = false)
    private String originalFileName;  // 업로드한 이미지 파일 원본 이름

    @Column(name = "size", columnDefinition = "INT(11) default 0")
    private Long size;  // 파일 사이즈

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private ContentType type;

    // Todo: 클라우드 스토리지 이용시 사용 고려
//    @Column(length = 255, nullable = false)
//    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creation_id", foreignKey = @ForeignKey(name = "fk_content_to_creation_id"))
    private Creation creation;
}
