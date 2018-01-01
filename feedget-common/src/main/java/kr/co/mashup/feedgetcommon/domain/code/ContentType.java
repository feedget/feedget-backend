package kr.co.mashup.feedgetcommon.domain.code;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<String, ContentType> lookup = new ConcurrentHashMap<>();

    static {  // 상수 이름을 실제 상수로 대응시키는 map 초기화
        for (ContentType type : values()) {
            lookup.put(type.value, type);
        }
    }

    public static ContentType fromString(String symbol) {
        return lookup.get(symbol);
    }
}
