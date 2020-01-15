package sco.co.so.microservices.limitsservice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
/**
 * Spring Boot annotation to get properties.  Any property starting with "limits-service" will be used; see
 * application.properties which has limits-service.maximum and limits-service.minimum.
 *
 * Note we renamed the application.properties to connect to the Spring Cloud Server instead
 */
@ConfigurationProperties("limits-service")
public class Configuration {

    private int maximum;
    private int minimum;

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }
}
