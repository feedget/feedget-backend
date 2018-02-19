package kr.co.mashup.feedgetapi.batch;

import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ethan.kim on 2018. 2. 12..
 */
@Configuration
@EnableBatchProcessing
@Slf4j
public class CreationEndJobConfiguration {

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

    // tag::jobstep[]
    @Bean(name = "creationEndJob")
    public Job creationEndJob(@Qualifier("creationEndStep") Step creationEndStep) {
        return jobBuilderFactory.get("creationEndJob")
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

    @StepScope
    @Bean(name = "endCreationReader")
    public JpaPagingItemReader<Creation> readEndCreation(@Value("#{jobParameters}") Map<String, Object> jobParameters) throws Exception {
        String readQuery = "SELECT c FROM Creation c WHERE c.dueDate > :startDate" +
                " AND c.dueDate < :endDate AND c.status = :status";

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", LocalDateTime.now().minusDays(1L));
        params.put("endDate", LocalDateTime.now());
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
                }
            }
        };
    }
}
