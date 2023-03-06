package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// es una configuracion inicial para poder trabajar con el programa y que este evite buscar la conneccion a la BD
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)

@SpringBootApplication
public class SpringEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringEcommerceApplication.class, args);
	}

}
