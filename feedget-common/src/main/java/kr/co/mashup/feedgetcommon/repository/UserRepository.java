package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by ethankim on 2017. 11. 5..
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * user ID로 User를 조회한다
     *
     * @param userId 유저 ID
     * @return
     */
    Optional<User> findByUserId(long userId);

    /**
     * email로 User를 조회한다
     *
     * @param email email
     * @return
     */
    Optional<User> findByEmail(String email);

    /**
     * UUID로 User를 조회한다
     *
     * @param uuid UUID
     * @return
     */
    Optional<User> findByUuid(String uuid);
}
