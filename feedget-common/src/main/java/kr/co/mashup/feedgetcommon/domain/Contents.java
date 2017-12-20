package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 컨텐츠
 * image, audio 등
 * <p>
 * Created by ethan.kim on 2017. 11. 4..
 */
@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {})
@EqualsAndHashCode(callSuper = false, of = "contentsId")
public class Contents extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "contents_id", columnDefinition = "INT(11)")
    private Long contentsId;

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
    private Type type;

    // Todo: 클라우드 스토리지 이용시 사용 고려
//    @Column(length = 255, nullable = false)
//    private String imageUrl;

    public enum Type {
        IMAGE("IMAGE"),
        AUDIO("AUDIO");

        private String value;

        Type(String value) {
            this.value = value;
        }

        private static final Map<String, Type> stringToEnum = new HashMap<>();

        static {  // 상수 이름을 실제 상수로 대응시키는 map 초기화
            for (Type type : values()) {
                stringToEnum.put(type.toString(), type);
            }
        }

        public static Type fromString(String symbol) {
            return stringToEnum.get(symbol);
        }
    }
}
