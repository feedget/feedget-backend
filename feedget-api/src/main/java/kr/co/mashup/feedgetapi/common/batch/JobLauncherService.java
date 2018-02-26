package kr.co.mashup.feedgetapi.common.batch;

import java.util.Map;

/**
 * Batch Job의 실행을 담당
 * <p>
 * Created by ethan.kim on 2018. 2. 24..
 */
public interface JobLauncherService {

    /**
     * Launch Job
     *
     * @param jobName      the job name
     * @param actionParams the action params
     * @return true, if successful
     */
    boolean launch(String jobName, Map<String, Object> actionParams);
}
