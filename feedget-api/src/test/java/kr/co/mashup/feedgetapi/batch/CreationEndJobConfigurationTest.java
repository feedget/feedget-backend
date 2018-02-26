package kr.co.mashup.feedgetapi.batch;

import kr.co.mashup.feedgetapi.FeedgetApiApplication;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobScopeTestExecutionListener;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 2. 18..
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FeedgetApiApplication.class, CreationEndJobConfigurationTest.TestJobConfiguration.class})
@ActiveProfiles(profiles = "test")
@TestExecutionListeners(value = {JobScopeTestExecutionListener.class, StepScopeTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class CreationEndJobConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private CreationRepository creationRepository;

    @MockBean(name = "endCreationReader")
    private JpaPagingItemReader endCreationReader;

    private Creation createCreation(long creationId) {
        Creation creation = new Creation();
        creation.setCreationId(creationId);
        creation.setStatus(Creation.Status.PROCEEDING);
        return creation;
    }

    @Configuration
    static class TestJobConfiguration {

        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils() {

                @Autowired
                @Override
                public void setJob(@Qualifier("creationEndJob") Job job) {
                    super.setJob(job);
                }
            };
        }
    }

    /**
     * End-To-End Testing of Batch Jobs
     * Job을 실행하고, 완료 여부만 검증한다
     */
    @Test
    public void creationEndJob_창작물_마감_성공() throws Exception {
        // given : 진행중인 창작물 2개로
        when(endCreationReader.read())
                .thenReturn(createCreation(1L), createCreation(2L), null);

        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("processingAt", new Date());

        // when : 창작물 마감 job이 실행되면
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(builder.toJobParameters());

        // then : 창작물이 마감된다
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        verify(creationRepository, times(2)).save(any(Creation.class));
    }

    /**
     * Testing Individual Steps
     * Job이 복잡하여 End-To-End Testing of Batch Job을 하기 어려운 경우 각각의 step별로 testing
     */
    @Test
    public void creationEndStep_창작물_마감_성공() throws Exception {
        // given : 진행중인 창작물 2개로
        when(endCreationReader.read())
                .thenReturn(createCreation(1L), createCreation(2L), null);

        // when : 창작물 마감 step이 실행되면
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("creationEndStep");

        // then : 창작물이 마감된다
        boolean status = jobExecution.getStepExecutions().stream()
                .allMatch(stepExecution -> stepExecution.getStatus() == BatchStatus.COMPLETED);
        boolean exitStatus = jobExecution.getStepExecutions().stream()
                .allMatch(stepExecution -> stepExecution.getExitStatus().equals(ExitStatus.COMPLETED));

        assertThat(status).isTrue();
        assertThat(exitStatus).isTrue();

        verify(creationRepository, times(2)).save(any(Creation.class));
    }
}
