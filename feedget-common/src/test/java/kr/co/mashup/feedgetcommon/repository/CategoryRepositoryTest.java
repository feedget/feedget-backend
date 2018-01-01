package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Category;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Created by ethan.kim on 2018. 1. 2..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository sut;

    private Category category;

    @Before
    public void setup() {
        category = new Category();
        category.setName("design");
        category = sut.save(category);
    }

    @After
    public void tearDown() {
        sut.deleteAll();
    }

    @Test
    public void findByName_카테고리_이름으로_단건_조회_성공() {
        // given : 카테고리 이름으로
        String categoryName = category.getName();

        // when : 카테고리를 조회하면
        Optional<Category> categoryOp = sut.findByName(categoryName);

        // then : 카테고리가 조회된다
        assertTrue(categoryOp.isPresent());
    }
}
