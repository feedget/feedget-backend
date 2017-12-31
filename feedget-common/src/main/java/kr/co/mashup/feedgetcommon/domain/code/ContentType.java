package kr.co.mashup.feedgetcommon.domain.code;

import java.util.HashMap;
import java.util.Map;

/**
 * 컨텐츠 종류
 * <p>
 * Created by ethan.kim on 2017. 12. 31..
 */
public enum ContentType {
    IMAGE("IMAGE"),
    AUDIO("AUDIO");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    private static final Map<String, ContentType> stringToEnum = new HashMap<>();

    static {  // 상수 이름을 실제 상수로 대응시키는 map 초기화
        for (ContentType type : values()) {
            stringToEnum.put(type.toString(), type);
        }
    }

    public static ContentType fromString(String symbol) {
        return stringToEnum.get(symbol);
    }
}
