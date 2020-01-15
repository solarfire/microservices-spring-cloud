package sco.co.so.microservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sco.co.so.microservices.currencyconversionservice.bean.CurrencyConversionBean;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Enable Feign.
 */
@EnableFeignClients("sco.co.so.microservices.currencyconversionservice")
@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeServiceProxy currencyExchangeProxy;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Using {@link RestTemplate}.
     */
    @GetMapping(value="/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean get(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {

        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversionBean.class,
                new HashMap<String, String>() {{
                    put("from", from);
                    put("to", to);
                }});

        CurrencyConversionBean response = responseEntity.getBody();
        response.setQuantity(quantity);
        response.setTotalCalculatedAmnount(quantity.multiply(response.getConversionMultiple()));
        return response;
    }

    /**
     * Using Feign (and Ribbon)
     * Less code.
     */
    @GetMapping(value="/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean getWithFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
        CurrencyConversionBean response = currencyExchangeProxy.retrieveValue(from, to);
        logger.info("{}", response);
        response.setQuantity(quantity);
        response.setTotalCalculatedAmnount(convert(quantity, response.getConversionMultiple()));
        return response;
    }

    private BigDecimal convert(BigDecimal quantity, BigDecimal exchange) {
        return quantity.multiply(exchange);
    }
}
