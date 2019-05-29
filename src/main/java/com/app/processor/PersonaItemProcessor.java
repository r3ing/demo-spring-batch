package com.app.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.app.model.Persona;

public class PersonaItemProcessor implements ItemProcessor<Persona, Persona> {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonaItemProcessor.class);

	@Override
	public Persona process(Persona item) throws Exception {
		
		String nombre = item.getNombre().toUpperCase();
		String apellido = item.getApellido().toUpperCase();
		String telefono = item.getTelefono();
		
		Persona persona = new Persona(nombre, apellido, telefono);
		
		logger.info("Convirtiendo " + item  + " ::: " + persona);
		
		return persona;
	}

}
