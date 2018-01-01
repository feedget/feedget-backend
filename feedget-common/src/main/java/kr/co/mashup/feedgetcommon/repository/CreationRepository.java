package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Creation;
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
}
