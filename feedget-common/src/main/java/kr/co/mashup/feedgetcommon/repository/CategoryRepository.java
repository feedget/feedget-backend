package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by ethankim on 2017. 11. 4..
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
