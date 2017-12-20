package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * 카테고리
 * <p>
 * Created by ethan.kim on 2017. 11. 4..
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"creations"})
@EqualsAndHashCode(callSuper = false, of = "categoryId")
public class Category extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id", columnDefinition = "INT(11)")
    private Long categoryId;

    // 카테고리 이름
    @Column(name = "name", length = 20)
    private String name;

    // 카테고리에 속한 창작물
    @OneToMany(mappedBy = "category")
    private List<Creation> creations;
}
