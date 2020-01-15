package sco.co.so.microservices.currencyconversionservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sco.co.so.microservices.currencyconversionservice.bean.CurrencyConversionBean;

/**
 * This will use Feign to call an external microservice.
 */
//@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
//@FeignClient(name = "currency-exchange-service")          // Don't need url as Ribbon will have them.
@FeignClient(name = "netflix-zuul-api-gateway-server")      // Point to the API gateway Zuul
@RibbonClient(name = "currency-exchange-service")
public interface CurrencyExchangeServiceProxy {

    //@GetMapping(value="/currency-exchange/from/{from}/to/{to}")
    @GetMapping(value="/currency-exchange-service/currency-exchange/from/{from}/to/{to}")  // Change URI to include the service name for Zuul as Zuul uses the service name
    CurrencyConversionBean retrieveValue(@PathVariable String from, @PathVariable String to);
}