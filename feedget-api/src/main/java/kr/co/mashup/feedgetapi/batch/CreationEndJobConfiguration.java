package kr.co.mashup.feedgetapi.batch;

import kr.co.mashup.feedgetapi.common.batch.JobConfig;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 창작물 마감 batch job
 * step 1)
 * reader : 어제 마감인 창작물 조회
 * processor : 미구현
 * writer : 창작물의 상태 마감으로 변경
 * <p>
 * Created by ethan.kim on 2018. 2. 12..
 */
@Configuration
@Slf4j
public class CreationEndJobConfiguration implements JobConfig {

    public static final String JOB_NAME = "creationEndJob";
    private static final int PAGE_SIZE = 100;
    private static final int CHUNK_SIZE = 10;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private CreationRepository creationRepository;

    @Autowired
    @Qualifier(value = JOB_NAME)
    private Job creationEndJob;

    @Override
    public String getJobName() {
        return JOB_NAME;
    }

    @Override
    public Job getJobInstance() {
        return creationEndJob;
    }

    @Override
    public JobParameters getJobParameters(Map<String, Object> actionParams) {
        Date processingAt = (Date) actionParams.getOrDefault("processingAt", new Date());

        return new JobParametersBuilder()
                .addDate("processingAt", processingAt)
                .toJobParameters();
    }

    // tag::jobstep[]
    @Bean(name = "creationEndJob")
    public Job creationEndJob(@Qualifier("creationEndStep") Step creationEndStep) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(creationEndStep)
                .build();
    }

    @JobScope
    @Bean(name = "creationEndStep")
    public Step creationEndStep(@Value("#{jobParameters}") Map<String, Object> jobParameters,
                                @Qualifier("endCreationReader") JpaPagingItemReader<Creation> endCreationReader,
                                @Qualifier("endCreationProcessor") ItemProcessor<Creation, Creation> endCreationProcessor,
                                @Qualifier("endCreationWriter") ItemWriter<? super Creation> endCreationWriter) {
        return stepBuilderFactory.get("creationEndStep")
                .allowStartIfComplete(true)
                .<Creation, Creation>chunk(new SimpleCompletionPolicy((Integer) jobParameters.getOrDefault("chunk", CHUNK_SIZE)))
                .reader(endCreationReader)
                .processor(endCreationProcessor)
                .writer(endCreationWriter)
                .build();
    }
    // end::jobstep[]

    /**
     * 마감 대상인 진행중인 창작물 조회하는 reader
     *
     * @param processingDate 처리 날짜
     * @return endCreationReader
     * @throws Exception
     */
    @StepScope
    @Bean(name = "endCreationReader")
    public JpaPagingItemReader<Creation> readEndCreation(@Value("#{jobParameters['processingAt']}") Date processingDate) throws Exception {
        String readQuery = "SELECT c FROM Creation c WHERE c.dueDate > :startDate" +
                " AND c.dueDate < :endDate AND c.status = :status";

        LocalDateTime processingAt = LocalDateTime.ofInstant(processingDate.toInstant(), ZoneId.of("UTC"));

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", processingAt.minusDays(1L));
        params.put("endDate", processingAt);
        params.put("status", Creation.Status.PROCEEDING);

        JpaPagingItemReader<Creation> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString(readQuery);
        reader.setParameterValues(params);
        reader.setPageSize(PAGE_SIZE);
        reader.setSaveState(true);

        return reader;
    }

    @Bean(name = "endCreationProcessor")
    public ItemProcessor<Creation, Creation> proeceeEndCreation() {
        return new ItemProcessor<Creation, Creation>() {
            @Override
            public Creation process(Creation item) throws Exception {
                // Todo: implements

                log.info("process {}", item);
                return item;
            }
        };
    }

    @Bean(name = "endCreationWriter")
    public ItemWriter<Creation> writeEndCreation() {
        return new ItemWriter<Creation>() {
            @Override
            public void write(List<? extends Creation> items) throws Exception {
                for (Creation item : items) {
                    log.info("write {}", item);

                    item.setStatus(Creation.Status.DEADLINE);
                    creationRepository.save(item);

                    // Todo: 마감 후 창작물 게시자에게 push로 창작물 마감 알림을 보낸다
                    // 24시 01분에 마감한다고 생각했을 때 이 시간대에 푸시를 보내면 안된다
                    // 푸시는 아침 정도에 보내는게 적당하다면 이걸 어떻게 분리할 것인가?
                }
            }
        };
    }
}
