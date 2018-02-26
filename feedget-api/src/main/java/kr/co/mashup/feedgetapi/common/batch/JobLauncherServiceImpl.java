package kr.co.mashup.feedgetapi.common.batch;

import kr.co.mashup.feedgetapi.common.util.ZonedDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by ethan.kim on 2018. 2. 24..
 */
@Service
@Slf4j
public class JobLauncherServiceImpl implements JobLauncherService {

    private static final long JOB_EXECUTE_DURATION = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private List<JobConfig> jobConfigs;

    @Autowired
    private JobExplorer jobExplorer;

    @Override
    public boolean launch(String jobName, Map<String, Object> actionParams) {
        try {
            JobConfig jobConfig = this.lookupJobConfig(jobName);

            // 해당 job이 이미 실행 중이면 실행시키지 않는다
            Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);
            if (!CollectionUtils.isEmpty(jobExecutions)) {
                return false;
            }

            JobExecution execution = jobLauncher.run(jobConfig.getJobInstance(), jobConfig.getJobParameters(actionParams));
            if (execution.getExitStatus() == ExitStatus.COMPLETED) {
                return true;
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Job : {}, failed!! params : {}", jobName, actionParams, e);
        }

        return false;
    }

    /**
     * job을 조회한다
     *
     * @param jobName the job name
     * @return job, if exists
     * @throws NoSuchBeanDefinitionException if not exists job
     */
    private JobConfig lookupJobConfig(String jobName) throws NoSuchBeanDefinitionException {
        Optional<JobConfig> jobConfigOp = jobConfigs.stream()
                .filter(jobConfig -> StringUtils.equals(jobName, jobConfig.getJobName()))
                .findAny();

        return jobConfigOp.orElseThrow(() -> new NoSuchBeanDefinitionException(jobName));
    }

    /**
     * @param jobConfig
     * @param params
     * @return
     */
    @Deprecated
    private JobParameters getJobParameters(JobConfig jobConfig, Map<String, Object> params) {
        JobParameters jobParameters = jobConfig.getJobParameters(params);

        // 강제 재시작 모드면 run.id를 추가해 restart 시칸
        if (StringUtils.equalsIgnoreCase("Y", String.valueOf(params.get("force")))) {
            jobParameters = new JobParametersBuilder(jobParameters)
                    .addString("run.id", ZonedDateTimeUtils.getCurrentZonedDateTimeWithFormat("yyyy-MM-dd HH:mm:ss", "UTC"))
                    .toJobParameters();
        }
        return jobParameters;
    }

    /**
     * job이 실행 가능한지 검사한다
     *
     * @param jobName
     * @return false, if completed or running
     */
    @Deprecated
    private boolean validateRunJob(String jobName) {

        List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(jobName, 0, 1);  // fetch last job
        // jobExplorer.getJobInstances(jobName, 0, 1);

        Optional<JobInstance> jobInstanceOp = jobInstances.stream().findFirst();
        if (!jobInstanceOp.isPresent()) {
            return false;
        }

        JobExecution oldExecution = jobExplorer.getJobExecution(jobInstanceOp.get().getInstanceId());
        if (oldExecution.getStatus() == BatchStatus.COMPLETED
                || oldExecution.isRunning()) {
            return false;
        }

        return true;
    }

    /**
     * 동일한 이름의 job이 실행된지 10분 안되었으면 30초 대기
     *
     * @param jobName
     */
    @Deprecated
    private void waitRunningJob(String jobName) {
        Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);
        if (CollectionUtils.isEmpty(jobExecutions)) {
            return;
        }

        long now = System.currentTimeMillis();
        boolean shouldWait = jobExecutions.stream()
                .map(jobExecution -> jobExecution.getStartTime().getTime())
                .anyMatch(startTimestamp -> now - startTimestamp < JOB_EXECUTE_DURATION);

        if (shouldWait) {
            log.warn("[CHECK] Same Job is Running now. wait for 30 sec. jobName : {}", jobName);

            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                log.info("thread sleep finish");
            }
        }
    }
}
