package br.com.fiap.reserva_Sovrano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(info = @Info(title = "Reservas Sovrano", version = "v1", description = "API do sistema de reservas do restaurante sovrano"))
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
