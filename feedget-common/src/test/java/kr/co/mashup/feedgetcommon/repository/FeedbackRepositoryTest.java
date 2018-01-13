package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.util.UniqueIdGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Created by ethan.kim on 2018. 1. 12..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class FeedbackRepositoryTest {

    @Autowired
    private FeedbackRepository sut;

    @Autowired
    private TestEntityManager entityManager;

    private Creation creation;

    private User writer;

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
        entityManager.persistAndFlush(creation);

        writer = new User();
        writer.setName("test");
        writer.setNickname("test");
        writer.setEmail("test@test.io");
        writer.setUuid(UniqueIdGenerator.getStringId());
        writer.setUserGrade(User.UserGrade.BRONZE);
        writer.setTotalPoint(100.0);
        writer.setCurrentPoint(100.0);
        writer.setPeriodPoint(100.0);
        writer.setFeedbackWritingCount(1);
        writer.setFeedbackRewardCount(1);
        entityManager.persistAndFlush(writer);

        Feedback feedback = new Feedback();
        feedback.setDescription("test");
        feedback.setAnonymity(true);
        feedback.setWriter(writer);
        feedback.setCreation(creation);
        sut.save(feedback);
    }

    @After
    public void tearDown() throws Exception {
        sut.deleteAll();
    }

    @Test
    public void findByCreationIdAndWriterId_피드백_단건_조회() {
        // given : 창작물 ID, 작성자 ID로
        long creationId = creation.getCreationId();
        long writerId = writer.getUserId();

        // when : 피드백을 조회하면
        Optional<Feedback> feedbackOp = sut.findByCreationIdAndWriterId(creationId, writerId);

        // then : 피드백이 조회된다
        assertTrue(feedbackOp.isPresent());
    }
}
