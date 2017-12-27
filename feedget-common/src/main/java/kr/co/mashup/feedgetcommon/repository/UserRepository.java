package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ethankim on 2017. 11. 5..
 */
public interface UserRepository extends JpaRepository<User, Long> {

}
