package sco.co.so.microservices.limitsservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sco.co.so.microservices.limitsservice.bean.LimitsConfiguration;

/**
 * Enable Hystrik fault tolerance.  This will be enabled on all {@code Controller}s.
 */
@EnableHystrix
@RestController
public class LimitsConfigurationController {

    @Autowired
    private Configuration configuration;

    @GetMapping(value = "/limits")
    public LimitsConfiguration retrieveLimitsFromConfiguration() {
        return new LimitsConfiguration(configuration.getMaximum(), configuration.getMinimum());
    }

    @GetMapping(value = "/limits-2")
    public LimitsConfiguration test() {
        return new LimitsConfiguration(1, 123456);
    }


    @GetMapping(value = "/limits-fault-tolerance")
    @HystrixCommand(fallbackMethod = "fallBackConfiguration")
    public LimitsConfiguration retrieveLimitsFromConfigurationFaultTolerance() {
        // If this service call threw an exception, then all services dependent on it woukd be fail.
        // With the  fallback method defined in the annotation, we can return some default value.
        throw new RuntimeException("Just to demo the Fault Tolerance by triggering annotation.");
    }

    public LimitsConfiguration fallBackConfiguration() {
        return new LimitsConfiguration(9, 999);
    }


}
