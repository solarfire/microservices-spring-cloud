package sco.co.so.microservices.springcloudconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Spring Config Server.
 * This is connected to a git repository (configured in application.properties.) to pick up all service configuration properties.
 * For example, in the repo there is limits-service.properties
 * to access, localhost:8888/limits-service/default - the context is taken from the property file name
 *
 * You can override (all or some of ) the default properties for environments
 *
 * e.g. limits-service-dev.properties   localhost:8888/limits-service/dev
 * e.g. limits-service-qa.properties   	localhost:8888/limits-service/qa
 *
 * These will respond with the a list or properties in the order of priority. e.g. for dev, the dev then default properties
 */
@EnableConfigServer //We need to enable to the Spring Config Server.
@SpringBootApplication
public class SpringCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}

}
