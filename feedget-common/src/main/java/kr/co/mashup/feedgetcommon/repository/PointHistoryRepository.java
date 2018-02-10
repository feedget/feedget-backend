package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ethan.kim on 2018. 2. 6..
 */
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
