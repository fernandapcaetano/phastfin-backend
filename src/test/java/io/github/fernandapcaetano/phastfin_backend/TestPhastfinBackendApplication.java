package io.github.fernandapcaetano.phastfin_backend;

import org.springframework.boot.SpringApplication;

public class TestPhastfinBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(PhastfinBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
