package sco.co.so.microservices.netflixzuulapigatewayserver;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring managed and extend {@link ZuulFilter} and implement the methods.
 */
@Component
public class ZuulLoggingFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Main part where we implement the logic of the filter.
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        logger.info("Request -> [{}], uri -> {}", request.getMethod(), request.getRequestURI());
        return null;
    }

    /**
     * When should the filter execute?
     * before ("pre" or after "post" request?  Or filter only error ("error").
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * Set priority of filter if have multiple.
     * Set it priority 1.
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * Should the filter execute or not?
     * Can execute some logic to decide whether or not to filter.
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }
}
