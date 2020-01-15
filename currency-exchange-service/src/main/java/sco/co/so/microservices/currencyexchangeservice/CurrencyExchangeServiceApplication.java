package sco.co.so.microservices.currencyexchangeservice;

import brave.sampler.Sampler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * Two run configurations for this service have been set up running on different ports.
 * Under the Run Configurarion the JVM argument -Dserver.port=8001 was added.
 * You can run both on different ports.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CurrencyExchangeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeServiceApplication.class, args);
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
