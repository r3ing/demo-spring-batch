package com.app.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.app.model.Persona;

@Component
public class JobListener extends JobExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(JobListener.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public JobListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.info("Finalizo el job!!! Verificando resultados...");

			jdbcTemplate
					.query("SELECT nombre, apellido, telefono FROM persona",
							(rs, rw) -> new Persona(rs.getString(1), rs.getString(2), rs.getString(3)))
					.forEach(p -> logger.info("Registro " + p));
			;
		}
	}

}
