package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by ethan.kim on 2017. 12. 10..
 */
public interface CreationRepository extends JpaRepository<Creation, Long> {

    /**
     * creation ID로 Creation을 조회한다
     *
     * @param creationId
     * @return
     */
    Optional<Creation> findByCreationId(long creationId);

    /**
     * category로 Creation 리스트 조회(페이징)
     *
     * @param category
     * @param pageable
     * @return
     */
    Page<Creation> findByCategory(Category category, Pageable pageable);
}
