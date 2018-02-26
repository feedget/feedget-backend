package kr.co.mashup.feedgetapi.batch;

import kr.co.mashup.feedgetapi.common.batch.JobLauncherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ethan.kim on 2018. 2. 25..
 */
@Service
public class CreationEndJobService {

    @Autowired
    private JobLauncherService jobLauncherService;

    @Scheduled(cron = "${schedule.cron.creation-end}", zone = "Asia/Seoul")
    public void runEndCreationJob() {
        Map<String, Object> params = new HashMap<>();
        params.put("processingAt", new Date());

        jobLauncherService.launch(CreationEndJobConfiguration.JOB_NAME, params);
    }
}
