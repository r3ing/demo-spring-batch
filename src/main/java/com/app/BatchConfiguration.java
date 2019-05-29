package com.app;

import javax.sql.DataSource;

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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.app.listener.JobListener;
import com.app.model.Persona;
import com.app.processor.PersonaItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public FlatFileItemReader<Persona> reader() {
		return new FlatFileItemReaderBuilder<Persona>()
				.name("personaItemReader")
				.resource(new ClassPathResource("sample-data.csv"))
				.delimited()
				.names(new String[] {"nombre", "apellido", "telefono"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Persona>() {{
					setTargetType(Persona.class);
				}})
				.build();				
	}
	
	@Bean
	public PersonaItemProcessor processor() {
		return new PersonaItemProcessor();
	}
	
	@Bean
	public JdbcBatchItemWriter<Persona> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Persona>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Persona>())
				.sql("INSERT INTO persona(nombre, apellido, telefono) VALUES(:nombre, :apellido, :telefono)")
				.dataSource(dataSource)
				.build();
	}
	
	@Bean
	public Job importPerosnaJob(JobListener jobListener, Step step1) {
		return jobBuilderFactory.get("importPerosnaJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener)
				.flow(step1)
				.end()
				.build();
	}
	
	@Bean
	public Step step1(JdbcBatchItemWriter<Persona> writer) {
		return stepBuilderFactory.get("step1")
				.<Persona, Persona> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer)
				.build();
	}
}
