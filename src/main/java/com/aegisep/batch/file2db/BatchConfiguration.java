package com.aegisep.batch.file2db;

import com.aegisep.batch.dto.TextToDBVo;
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

	private final String [] names = {"aptcd","dongho","billym","monthfee","bfoverfee","bfoverduefee","bfduedtfee","afoverduefee","afduedtfee","modifydt"};

	// 5 8 6 11 11 11 11 11 11 8
	/*
	* 05065
	* 01101302
	* 202208
	* 263760
	* 0
	* 0
	* 263760
	* 0
	* 263760
	* 20220921
	* */
	@Bean
	public FlatFileItemReader<TextToDBVo> flatFileItemReader() {

		int index = 1;

		Range [] ranges = {
				new Range(index, index = index + 4),
				new Range(index = index + 1, index = index + 7),
				new Range(index = index + 1, index = index + 5),
				new Range(index = index + 1, index = index + 10),
				new Range(index = index + 1, index = index + 10),
				new Range(index = index + 1, index = index + 10),
				new Range(index = index + 1, index = index + 10),
				new Range(index = index + 1, index = index + 10),
				new Range(index = index + 1, index = index + 10),
				new Range(index = index + 1, index + 7)};

		log.debug(" >>>>>>>>>>> " + ranges.length + " >>>>>>>>>>>> " + index);

		return new FlatFileItemReaderBuilder<TextToDBVo>()
				.name("TextToDBItemReader")
				.resource(new PathResource("output/DS001.20230208.txt"))
				.encoding("EUC-KR")
				.fixedLength()
				.columns(ranges)
				.names(names)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
					setTargetType(TextToDBVo.class);
				}})
				.build();
	}

	@Bean
	public JdbcBatchItemWriter<TextToDBVo> jdbcBatchItemWriter() {
		return new JdbcBatchItemWriterBuilder<TextToDBVo>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO bill (aptcd,dongho,billym,custid,monthfee,bfoverfee,bfoverduefee,bfduedtfee,afoverduefee,afduedtfee,modifydt) " +
						"VALUES (:aptcd,:dongho,:billym, 0,:monthfee,:bfoverfee,:bfoverduefee,:bfduedtfee,:afoverduefee,:afduedtfee,:modifydt)")
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
			.<TextToDBVo, TextToDBVo> chunk(chunkSize)
			.reader(flatFileItemReader())
			.processor(processor())
			.writer(jdbcBatchItemWriter())
			.faultTolerant()
//			.noRollback(NullPointerException.class) // null point exception
//			.noRollback(FlatFileParseException.class)
			.build();
	}
}
