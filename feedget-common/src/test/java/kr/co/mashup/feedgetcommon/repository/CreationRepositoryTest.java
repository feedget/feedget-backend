package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Creation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

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

    private Creation creation;

    @Before
    public void setUp() throws Exception {
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
}
