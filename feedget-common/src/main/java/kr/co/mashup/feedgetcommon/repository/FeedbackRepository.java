package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by ethan.kim on 2018. 1. 11..
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Todo: 컬럼 존재 여부만 가져올 수 없을까? exists 처럼
    Optional<Feedback> findByCreationIdAndWriterId(long creationId, long writerId);
}
