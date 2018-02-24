package kr.co.mashup.feedgetapi.common.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ethan.kim on 2018. 2. 24..
 */
@Service
@Slf4j
public class JobLauncherServiceImpl implements JobLauncherService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private List<JobConfig> jobConfigs;

    @Override
    public boolean launch(String jobName, Map<String, Object> actionParams) {
        try {
            JobConfig jobConfig = this.lookupJobConfig(jobName);
            JobExecution execution = jobLauncher.run(jobConfig.getJobInstance(), jobConfig.getJobParameters(actionParams));

            if (execution.getExitStatus() == ExitStatus.COMPLETED) {
                return true;
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Job : {}, failed!! params : {}", jobName, actionParams, e);
        }

        return false;
    }
    
    private JobConfig lookupJobConfig(String jobName) {
        Optional<JobConfig> jobConfigOp = jobConfigs.stream()
                .filter(jobConfig -> StringUtils.equals(jobName, jobConfig.getJobName()))
                .findAny();

        return jobConfigOp.orElseThrow(() -> new NoSuchBeanDefinitionException(jobName));
    }
}
