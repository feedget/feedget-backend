package kr.co.mashup.feedgetcommon.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Created by ethan.kim on 2017. 12. 10..
 */
@Entity
@Data
public class Creation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long creationId;


}
