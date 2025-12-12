package com.universidad.elecciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EleccionesUniApplication {

	public static void main(String[] args) {
		SpringApplication.run(EleccionesUniApplication.class, args);
	}

}
