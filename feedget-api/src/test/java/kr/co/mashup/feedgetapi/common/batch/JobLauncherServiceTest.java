package kr.co.mashup.feedgetapi.common.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 2. 25..
 */
@RunWith(MockitoJUnitRunner.class)
public class JobLauncherServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private List<JobConfig> jobConfigs;

    @Mock
    private JobExplorer jobExplorer;

    @InjectMocks
    private JobLauncherServiceImpl sut;

    private JobConfig createJobConfig(String jobName) {
        return new JobConfig() {
            @Override
            public String getJobName() {
                return jobName;
            }

            @Override
            public Job getJobInstance() {
                return new SimpleJob();
            }

            @Override
            public JobParameters getJobParameters(Map<String, Object> actionParams) {
                return new JobParameters();
            }
        };
    }

    @Test
    public void launch_job_실행_후_완료() throws Exception {
        // given : job 이름으로
        final String jobName = "testJobName";

        JobExecution execution = new JobExecution(1L);
        execution.setExitStatus(ExitStatus.COMPLETED);

        when(jobConfigs.stream()).thenReturn(Stream.of(createJobConfig(jobName)));
        when(jobExplorer.findRunningJobExecutions(jobName)).thenReturn(Collections.emptySet());
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(execution);

        // when : job을 실행하면
        boolean result = sut.launch(jobName, new HashMap<>());

        // then : job을 완료한다
        assertThat(result).isTrue();
        verify(jobExplorer, times(1)).findRunningJobExecutions(anyString());
        verify(jobLauncher, times(1)).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    public void launch_job_실행_후_미완료() throws Exception {
        // given : job 이름으로
        final String jobName = "testJobName";

        JobExecution execution = new JobExecution(1L);
        execution.setExitStatus(ExitStatus.FAILED);

        when(jobConfigs.stream()).thenReturn(Stream.of(createJobConfig(jobName)));
        when(jobExplorer.findRunningJobExecutions(jobName)).thenReturn(Collections.emptySet());
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(execution);

        // when : job을 실행하면
        boolean result = sut.launch(jobName, new HashMap<>());

        // then : job을 미완료한다
        assertThat(result).isFalse();
        verify(jobExplorer, times(1)).findRunningJobExecutions(anyString());
        verify(jobLauncher, times(1)).run(any(Job.class), any(JobParameters.class));
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void launch_존재하지_않는_job_실행() throws Exception {
        // given : job 이름으로
        final String jobName = "testJobName";

        when(jobConfigs.stream()).thenReturn(Stream.empty());

        // when : job을 실행하면
        sut.launch(jobName, new HashMap<>());

        // then : 존재하지 않는 job이라 exception 발생
    }

    @Test
    public void launch_실행중인_job_실행() throws Exception {
        // given : job 이름으로
        final String jobName = "testJobName";

        Set<JobExecution> jobExecutions = new HashSet<>();
        jobExecutions.add(new JobExecution(1L));

        when(jobConfigs.stream()).thenReturn(Stream.of(createJobConfig(jobName)));
        when(jobExplorer.findRunningJobExecutions(jobName)).thenReturn(jobExecutions);

        // when : job을 실행하면
        boolean result = sut.launch(jobName, new HashMap<>());

        // then : job을 실행하지 않는다
        assertThat(result).isFalse();
        verify(jobExplorer, times(1)).findRunningJobExecutions(anyString());
        verify(jobLauncher, never()).run(any(Job.class), any(JobParameters.class));
    }
}
