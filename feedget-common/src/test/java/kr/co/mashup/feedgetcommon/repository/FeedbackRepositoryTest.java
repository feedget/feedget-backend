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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by ethan.kim on 2018. 1. 12..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class FeedbackRepositoryTest {

    private static final int LOOP_COUNT = 10;

    @Autowired
    private FeedbackRepository sut;

    @Autowired
    private TestEntityManager entityManager;

    private Creation creation;

    private User writer;

    private long creataedFeedbackId;

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
        writer.setRealName("test");
        writer.setNickname("test");
        writer.setEmail("testUser1@test.io");
        writer.setUuid(UniqueIdGenerator.getStringId());
        writer.setUserGrade(User.UserGrade.BRONZE);
        writer.setTotalPointAmount(100.0);
        writer.setCurrentPointAmount(100.0);
        writer.setPeriodPointAmount(100.0);
        writer.setFeedbackWritingCount(1);
        writer.setFeedbackSelectionCount(1);
        entityManager.persistAndFlush(writer);

        User otherUser = new User();
        otherUser.setRealName("test");
        otherUser.setNickname("test");
        otherUser.setEmail("testUser2@test.io");
        otherUser.setUuid(UniqueIdGenerator.getStringId());
        otherUser.setUserGrade(User.UserGrade.BRONZE);
        otherUser.setTotalPointAmount(100.0);
        otherUser.setCurrentPointAmount(100.0);
        otherUser.setPeriodPointAmount(100.0);
        otherUser.setFeedbackWritingCount(1);
        otherUser.setFeedbackSelectionCount(1);
        entityManager.persistAndFlush(otherUser);

        Feedback feedback;
        feedback = new Feedback();
        feedback.setContent("test");
        feedback.setAnonymity(true);
        feedback.setWriter(writer);
        feedback.setCreation(creation);
        feedback.setSelection(true);
        sut.save(feedback);
        creataedFeedbackId = feedback.getFeedbackId();

        for (int i = 0; i < LOOP_COUNT; i++) {
            feedback = new Feedback();
            feedback.setContent("test");
            feedback.setAnonymity(true);
            feedback.setWriter(otherUser);
            feedback.setCreation(creation);
            feedback.setSelection(false);
            sut.save(feedback);
        }
    }

    @After
    public void tearDown() throws Exception {
        sut.deleteAll();
    }

    @Test
    public void findByCreationIdAndWriterId_유저가_작성한_창작물의_피드백_단건_조회() {
        // given : 창작물 ID, 작성자 ID로
        long creationId = creation.getCreationId();
        long writerId = writer.getUserId();

        // when : 피드백을 조회하면
        Optional<Feedback> feedbackOp = sut.findByCreationIdAndWriterId(creationId, writerId);

        // then : 피드백이 조회된다
        assertTrue(feedbackOp.isPresent());
    }

    @Test
    public void findByCreationIdAndSelectionIsTrue_채택된_창작물의_피드백_단건_조회() {
        // given : 창작물 ID로
        long creationId = creation.getCreationId();

        // when : 피드백을 조회하면
        Optional<Feedback> feedbackOp = sut.findByCreationIdAndSelectionIsTrue(creationId);

        // then : 피드백이 조회된다
        assertTrue(feedbackOp.isPresent());
    }

    @Test
    public void findByCreationIdAndSelectionIsFalse_채택되지_않은_창작물의_피드백_다건_조회() {
        // given : 창작물 ID로
        long creationId = creation.getCreationId();

        // when : 피드백을 조회하면
        Page<Feedback> feedbackPage = sut.findByCreationIdAndSelectionIsFalse(creationId, new PageRequest(0, 10));

        // then : 피드백이 조회된다
        assertThat(feedbackPage.getContent())
                .isNotEmpty()
                .hasSize(LOOP_COUNT);
    }

    @Test
    public void findByFeedbackId_피드백_단건_조회() {
        // given : 피드백 ID로
        long feedbackId = creataedFeedbackId;

        // when : 피드백을 조회하면
        Optional<Feedback> feedbackOp = sut.findByFeedbackId(feedbackId);

        // then : 피드백이 조회된다
        assertTrue(feedbackOp.isPresent());
    }
}
