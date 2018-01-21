package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.util.UniqueIdGenerator;
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
 * Created by ethan.kim on 2018. 1. 11..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository sut;

    private User user;

    private static final String USER_EMAIL = "email@mashup.io";

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setRealName("ethan");
        user.setNickname("ethannick");
        user.setEmail(USER_EMAIL);
        user.setUuid(UniqueIdGenerator.getStringId());
        user.setCloudMsgRegId(null);
        user.setUserGrade(User.UserGrade.BRONZE);
        user.setUseVersionCode(100000);
        user.setTotalPoint(100.0);
        user.setPeriodPoint(100.0);
        user.setCurrentPoint(100.0);
        user.setFeedbackWritingCount(3);
        user.setFeedbackRewardCount(3);
        sut.save(user);
    }

    @After
    public void tearDown() throws Exception {
        sut.deleteAll();
    }

    @Test
    public void findByUserId_유저_단건_조회_성공() {
        // given : 유저 ID로
        long userId = user.getUserId();

        // when : 유저를 조회하면
        Optional<User> userOp = sut.findByUserId(userId);

        // then : 유저가 조회된다
        assertTrue(userOp.isPresent());
    }

    @Test
    public void findByEmail_이메일로_유저_단건_조회_성공() {
        // given : 이메일로
        String userEmail = USER_EMAIL;

        // when : 유저를 조회하면
        Optional<User> userOp = sut.findByEmail(userEmail);

        // then : 유저가 조회된다
        assertTrue(userOp.isPresent());
    }
}
