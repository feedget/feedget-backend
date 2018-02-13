package kr.co.mashup.feedgetapi.batch;

import kr.co.mashup.feedgetcommon.domain.Creation;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Created by ethan.kim on 2018. 2. 12..
 */
@Configuration
@EnableBatchProcessing
public class CreationEndJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;


    // tag::jobstep[]
    @Bean(name = "creationEndJob")
    public Job creationEndJob(@Qualifier("creationEndStep") Step creationEndStep) {
        return jobBuilderFactory.get("creationEndJob")
                .incrementer(new RunIdIncrementer())
                .start(creationEndStep)
                .build();
    }

    @Bean(name = "creationEndStep")
    public Step creationEndStep(@Value("#{jobParameters}") Map<String, Object> jobParameters,
                                @Qualifier("endCreationReader") JpaPagingItemReader<Creation> endCreationReader,
                                @Qualifier("endCreationProcessor") ItemProcessor<Creation, Creation> endCreationProcessor,
                                @Qualifier("endCreationWriter") ItemWriter<? super Creation> endCreationWriter) {
        return stepBuilderFactory.get("creationEndStep")
                .allowStartIfComplete(true)
                .<Creation, Creation>chunk(new SimpleCompletionPolicy((Integer) jobParameters.get("chunk")))
                .reader(endCreationReader)
                .processor(endCreationProcessor)
                .writer(endCreationWriter)
                .build();
    }
    // end::jobstep[]

    @Bean(name = "endCreationReader")
    public JpaPagingItemReader readEndCreation(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        // Todo: implements

        JpaPagingItemReader<Creation> reader = new JpaPagingItemReader<>();
        return reader;
    }

    @Bean(name = "endCreationProcessor")
    public ItemProcessor<Creation, Creation> proeceeEndCreation() {
        return new ItemProcessor<Creation, Creation>() {
            @Override
            public Creation process(Creation item) throws Exception {
                // Todo: implements
                return item;
            }
        };
    }

    @Bean(name = "endCreationWriter")
    public ItemWriter<Creation> writeEndCreation() {
        return new ItemWriter<Creation>() {
            @Override
            public void write(List<? extends Creation> items) throws Exception {
                // Todo: implements
            }
        };
    }
}
