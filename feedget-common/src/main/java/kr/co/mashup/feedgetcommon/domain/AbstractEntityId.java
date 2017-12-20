package kr.co.mashup.feedgetcommon.domain;

import java.io.Serializable;

/**
 * 복합키 ID의 추상클래스
 * 복합키 설정
 * 1. @Embeddable 선언
 * 2. Serializable 구현
 * 3. equals, hashCode 구현
 * 4. default constructor 존재
 * 5. public class
 * <p>
 * Created by ethan.kim on 2017. 12. 19..
 */
public abstract class AbstractEntityId implements Serializable {

    public abstract String toString();

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
