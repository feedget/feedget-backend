package kr.co.mashup.feedgetcommon.repository;

import kr.co.mashup.feedgetcommon.domain.Creation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by ethan.kim on 2017. 12. 24..
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class CreationRepositoryTest {

    @Autowired
    private CreationRepository creationRepository;

    @Test
    public void test() throws Exception {
        // given :

        List<Creation> creations = creationRepository.findAll();

        // when :

        // then :

    }
}
