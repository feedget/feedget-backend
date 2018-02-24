package kr.co.mashup.feedgetapi.common.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;

import java.util.Map;

/**
 * Created by ethan.kim on 2018. 2. 24..
 */
public interface JobConfig {
    
    /**
     * Job 이름 조회
     *
     * @return
     */
    String getJobName();

    /**
     * 실행할 Job 인스턴스 조회
     *
     * @return
     */
    Job getJobInstance();

    /**
     * 전달 받은 params으로 {@link JobParameters} 조회
     *
     * @param actionParams
     * @return
     */
    JobParameters getJobParameters(Map<String, Object> actionParams);
}
