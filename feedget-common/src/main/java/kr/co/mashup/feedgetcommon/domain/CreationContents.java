package kr.co.mashup.feedgetcommon.domain;

import lombok.*;

import javax.persistence.*;

/**
 * 창작물 - 컨텐츠
 * <p>
 * Created by ethan.kim on 2017. 12. 19..
 */
@Entity
@Table(name = "creation_contents")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
public class CreationContents extends AbstractEntity<CreationContents.Id> {

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY)  // CreationContents(Many) : Creation(One)
    @MapsId(value = "creationId")
    private Creation creation;

    @OneToOne(fetch = FetchType.LAZY)  // CreationContents(Many) : Contents(One)
    @MapsId(value = "contentsId")
    private Contents contents;

    public CreationContents(Creation creation, Contents contents) {
        this.id.creationId = creation.getCreationId();
        this.creation = creation;
        this.id.contentsId = contents.getContentsId();
        this.contents = contents;
    }

    @Embeddable
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode(callSuper = false, of = {"creationId", "contentsId"})
    public static class Id extends AbstractEntityId {

        @Column(name = "creation_id", columnDefinition = "INT(11)")
        private Long creationId;

        @Column(name = "contents_id", columnDefinition = "INT(11)")
        private Long contentsId;
    }
}
