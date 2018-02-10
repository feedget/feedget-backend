package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.PointHistory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by ethan.kim on 2018. 2. 6..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository sut;

    private PointHistory pointHistory;

    @Before
    public void setUp() throws Exception {
        pointHistory = new PointHistory();
        pointHistory.setGiverId(1L);
        pointHistory.setReceiverId(2L);
        pointHistory.setPointAmount(100.0);
        pointHistory.setType(PointHistory.Type.FEEDBACK_REWARD);
        sut.save(pointHistory);
    }

    @After
    public void tearDown() throws Exception {
        sut.deleteAll();
    }

    @Test
    public void findOne_포인트_내역_단건_조회() throws Exception {
        // given : 포인트 내역 ID로
        long pointHistoryId = pointHistory.getPointHistoryId();

        // when : 단건 조회하면
        PointHistory pointHistory = sut.findOne(pointHistoryId);

        // then : 조회된다
        assertThat(pointHistory).isNotNull();
    }
}
