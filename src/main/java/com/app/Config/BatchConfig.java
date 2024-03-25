package com.app.Config;

import com.app.Batch.AuthorItemProcessor;
import com.app.Model.Author;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Value("${file.input}")
    private String fileInput;

    @Bean
    public FlatFileItemReader<Author> reader(){
        return new FlatFileItemReaderBuilder<Author>().name("authorItemReader")
                .resource(new ClassPathResource(fileInput))
                .delimited()
                .names(new String[]{"name"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>(){{
                    setTargetType(Author.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Author> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Author>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO authors (name) VALUES (:name)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository,
                             JobExecutionListener listener,
                             Step step1){
        return new JobBuilder("importUserJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end().build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      JdbcBatchItemWriter<Author> writer){
        return new StepBuilder("step1",jobRepository)
                .<Author,Author> chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public ItemProcessor<Author,Author> processor() {
        return new AuthorItemProcessor();
    }
}
