package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by ethan.kim on 2017. 12. 24..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class CreationRepositoryTest {

    @Autowired
    private CreationRepository sut;

    @Autowired
    private CategoryRepository categoryRepository;

    private Creation creation;

    private Category category;

    @Before
    public void setUp() throws Exception {
        category = new Category();
        category.setName("design");
        category = categoryRepository.save(category);

        creation = new Creation();
        creation.setTitle("title");
        creation.setDescription("description");
        creation.setCategory(null);
        creation.setDueDate(LocalDateTime.now().plusDays(14));
        creation.setRewardPoint(10.0);
        creation.setStatus(Creation.Status.PROCEEDING);
        creation.setWriter(null);
        creation.setAnonymity(true);
        creation.setFeedbackCount(0L);
        creation.setCategory(category);
        creation = sut.save(creation);
    }

    @After
    public void tearDown() throws Exception {
        sut.deleteAll();
    }

    @Test
    public void findByCreationId_창작물_단건_조회_성공() throws Exception {
        // given : 창작물 ID로
        long creationId = creation.getCreationId();

        // when : 창작물을 조회하면
        Optional<Creation> creationOp = sut.findByCreationId(creationId);

        // then : 창작물이 조회된다
        assertTrue(creationOp.isPresent());
    }

    @Test
    public void findByCategory_카테고리의_창작물_리스트_조회_성공() {
        // given : 카테고리로

        // when : 카테고리의 창작물 리스트를 조회하면
        Page<Creation> creationPage = sut.findByCategory(category, new PageRequest(0, 10));

        // then : 카테고리의 창작물 리스트를 조회된다
        assertThat(creationPage.getContent())
                .isNotEmpty();
    }
}
