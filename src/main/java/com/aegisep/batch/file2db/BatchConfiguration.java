package com.aegisep.batch.file2db;

import com.aegisep.batch.dto.ResidentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Repository
@Slf4j
public class BatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("${aegis.batch.chunkSize}")
	private int chunkSize;

	@Autowired
	private DataSource dataSource;

	@Bean
	public FlatFileItemReader<ResidentVo> flatFileItemReader() {

		int index = 1;

		Range [] ranges = {
				new Range(index, index = index+4),
				new Range(index = index + 1, index = index + 4),
				new Range(index = index + 1, index = index + 7),
				new Range(index = index + 1, index = index + 7),
				new Range(index = index + 1, index = index + 9),
				new Range(index = index + 1, index = index + 9),
				new Range(index = index + 1, index + 12)};

		log.debug(" >>>>>>>>>>> " + ranges.length + " >>>>>>>>>>>> " + index);

		return new FlatFileItemReaderBuilder<ResidentVo>()
				.name("personItemReader")
				.resource(new PathResource("output/RS001.20230118.txt"))
				.encoding("EUC-KR")
				.fixedLength()
				.columns(ranges)
				.names("aptcd", "orgaptcd", "dongho", "occu_date", "rel", "name", "mobile_tel_no1")
				.fieldSetMapper(new BeanWrapperFieldSetMapper<ResidentVo>() {{
					setTargetType(ResidentVo.class);
				}})
				.build();
	}

	@Bean
	public JdbcBatchItemWriter<ResidentVo> jdbcBatchItemWriter() {
		return new JdbcBatchItemWriterBuilder<ResidentVo>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO resident (aptcd, orgaptcd, dongho, occu_date, rel, name, mobile_tel_no1) " +
						"VALUES (:aptcd, :orgaptcd, :dongho, :occu_date, :rel, :name, :mobile_tel_no1)")
				.dataSource(dataSource)
				.build();
	}
	@Bean
	public ResidentItemProcessor processor() {
		return new ResidentItemProcessor();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importUserJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1())
			.end()
			.build();
	}

	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
			.<ResidentVo, ResidentVo> chunk(chunkSize)
			.reader(flatFileItemReader())
			.processor(processor())
			.writer(jdbcBatchItemWriter())
			.faultTolerant()
			.noRollback(NullPointerException.class) // null point exception
			.noRollback(FlatFileParseException.class)
			.build();
	}
}
