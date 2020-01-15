package sco.co.so.microservices.currencyconversionservice;

import brave.sampler.Sampler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient // Register with the Naming Server.  Configure its location in application.properties
public class CurrencyConversionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionServiceApplication.class, args);
	}

	/**
	 * Sleuth/Zipkin
	 * Which requests you want to trace?
	 * @return
	 */
	@Bean
	public Sampler defautlSampler() {
		return Sampler.ALWAYS_SAMPLE;

	}

}
