package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by ethan.kim on 2018. 1. 11..
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * creation ID, user ID로 유저가 작성한 Feedback을 조회한다
     *
     * @param creationId Creation ID
     * @param writerId   User ID
     * @return
     */
    Optional<Feedback> findByCreationIdAndWriterId(long creationId, long writerId);

    /**
     * creation ID로 창작물의 채택된 Feedback을 조회한다
     *
     * @param creationId Creation ID
     * @return
     */
    Optional<Feedback> findByCreationIdAndSelectionIsTrue(@Param("creationId") long creationId);

    /**
     * creation ID로 창작물의 채택되지 않은 Feedback List를 조회한다(pagenation)
     *
     * @param creationId Creation ID
     * @param pageable   pagenation info
     * @return
     */
    Page<Feedback> findByCreationIdAndSelectionIsFalse(long creationId, Pageable pageable);
}
