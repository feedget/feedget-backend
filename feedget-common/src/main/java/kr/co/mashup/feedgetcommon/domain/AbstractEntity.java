package kr.co.mashup.feedgetcommon.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 도메인 클래스 추상화
 * <p>
 * Created by ethankim on 2017. 11. 4..
 */
@MappedSuperclass  //Parent Entity Class 지원
@EntityListeners(AuditingEntityListener.class)
//@JsonInclude(value = JsonInclude.Include.ALWAYS)   //필드값 존재 여부에 따라 Json에 포함할지 여부
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility =
//        JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AbstractEntity<K extends Serializable> implements Serializable {

    /**
     * 생성일자
     */
    @CreatedDate
    @Column(name = "created_at", insertable = true, updatable = false)
    // insert 구문 포함, update 못하게 설정
    private LocalDateTime createdAt;

    /**
     * 수정일자
     */
    @LastModifiedDate
    @Column(name = "updated_at", insertable = true, updatable = true)
    // insert, update 구문 포함
    private LocalDateTime updatedAt;

//    @PrePersist
//    private void onPresist() {
//        this.createdAt = this.updatedAt = LocalDateTime.now();
//    }

//    @PreUpdate
//    private void onUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }

    public abstract String toString();

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public long getCreatedTimestamp() {
        if (this.createdAt == null) {
            return 0;
        }
        return Timestamp.valueOf(this.createdAt).getTime();
    }

    public long getUpdatedTimestamp() {
        if (this.updatedAt == null) {
            return 0;
        }
        return Timestamp.valueOf(this.updatedAt).getTime();
    }
}
