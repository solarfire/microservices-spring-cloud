# Microservices & Spring Cloud
NB: Based on Spring Boot 2.2.2 (e.g. dependency management)

# 1. Definitions
*Small autonomous service that work together* - Sam Newman

*In short, the microservice architectural style is an approach to developing a single application as a suite of small services, each running in its own process and communicating with lightweight mechanisms, often an HTTP resource API . These services are built around business capabilities and independently deployable by fully automated deployment machinery. There is a bare minimum of centralised management of these services, which may be written in different programming language and use different data storage technologies.* - James Lewis and Martin Fowler.

` Microservice 1 (2 instances) ` -> ` Microservice 2 (4 instances)` ->  ` Microservice 3 (2 instances)` ->  ` Microservice 4 (1 instances) ` -> ` Microservice n	(n instances) `


### Important
1. REST
2. Small Well Chosen Deployable Units
3. Cloud Enabled (multiple instances of each service e.g. load handling, not much config)

### Challenges
1. Bounded Context - how to identify the boundary of each service.  An evolutionary process, probably won’t get it right first time.
2. Configuration Management - multiple services,  environments, and instances.
3. Dynamic Scale Up and Scale Down - different services need more instances at different times and vice versa.  Dynamic load balancing.
4. Visibility and Monitoring - e.g identify a bug , issues, env issues, in a chain of services.
5. Fault Tolerance (Pack of Cards) - if badly designed, one goes down, then all / application go down. So fault tolerance needed.

# 2. Spring Cloud (High Level)
Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems (e.g. configuration management, service discovery, circuit breakers, intelligent routing, micro-proxy, control bus, one-time tokens, global locks, leadership election, distributed sessions, cluster state).  Coordination of distributed systems leads to boiler plate patterns, and using Spring Cloud developers can quickly stand up services and applications that implement those patterns.  They will work well in any distributed environment, including the developer’s own laptop, bare metal data centres, and managed platforms such as Cloud Foundry.

Spring cloud is not really one project.  There are huge variety of projects.  e.g. Spring Cloud Netflix who were one of the first to play with microservices.

Spring can provide a solution / components to the above challenges.

1. Configuration Management - Spring Cloud Config Server provides an approach to store all the config for all environments/services in a central location (in GIT).  This can expose all config to all services.
2. Dynamic Scale Up and Scale Down - load balancing amongst several instances of the same service.
    1. Naming Server (Eureka) - all instances of all services are registered.
        1. Service Registration
        2. Service Discovery e.g. a Service can ask the Naming Server for the required services, instances it needs and get the URL.
    2. Client Side Load Balancing (Ribbon) - Ensure the load is balanced between the existing instances.
    3. Easier REST Clients (Feign) - to write simple REST clients.
3. Visibility and Monitoring -
    1. Zipkin Distributed Tracing-  trace a request across multiple components.
    2. Netflix (Zuul) API gateway - Services have a lot in common e.g. logging, security, analytics which you don’t want to implement in every service.  So use a API gateway.
4. Fault Tolerance (Hystrix) - If a service down we can configure a default response.

# 3. Microservices Advantages

## 3.1 New Technology & Process Adaption
Allows this easily.  Each service can be build using different technologies c.f. monolith.

## 3.2 Dynamic Scaling
Different loads at different times.  e.g. Black Friday Amazon at holidays.

## 3.3 Faster Release Cycles
Because releasing small components can go faster which is an advantage in modern world.

# 4. configuration

Each service may have multiple environments (e.g. dev, 2 QAs, staging, production) and each service may have multiple instances per environment.  And some orgamisations may have up to 200 services.  So that is a lot of configuration for each service (e.g. db connection details).

We can add all this to a GIT and each service will ask Spring Cloud Config server for the required service.

    CurrencyCalculationService    CurrencyExchangeService     LimitsService
                              ↓             ↓                 ↓
                                  SpringCloudConfigServer
                                            ↓
                                           GIT


# 5. Dynamic Scaling Up & Down
## 5.1. for Restful Client - using Feign
Is a Rest service client.  It makes it easy to call web services.  Before we would could write a RESTTemplate but that is a lot of code.  And especially with microservices where the idea is to make it quick to write.

 Implementation
 - Import the `org.springframework.cloud:spring-cloud-starter-openfeign` dependency
 - Annotate main class with @EnableFeignClients
 - Create a simple interface proxy
  - annotate with the following adding target service name and url  `@EnableFeignClient``(name = "currency-exchange-service", url = "localhost:8000")`  
  - Add a method(s) to call the required method(s)

## 5.2. for Load Balancing - using Ribbon
With feign we hard coded the url (port) but there may be multiple instances of the service e.g. 4 prod instances.  So we use Ribbon to distribute the load.

Implementation
 - Import the `org.springframework.cloud:spring-cloud-starter-netflix-ribbon dependecy`
 - Annotate the proxy with @RibbonCLient and remove the url from the @EnableFeignClient.
 - Add the list of instances of the service you are calling/consuming to application.properties (NB: use the service name that matches that one defined in the annotation e.g. @RibbonClient("**currency-exchange-service**")

> e.g. **currency-exchange-service**.ribbon.listOfServers=http://localhost:8000, http://localhost:8001

## 5.3. for Naming Server - using Eureka from Netflix
Above, if we wanted to add (or remove) new instance of a service, then we would have to run a new instance on a different port, and add a new entry to the client's properties e.g. hardcode.  This would be a maintenance nightmare.

Also, Eureka has a interface (http://localhost:8761) to view all registered services for discovery.

> e.g. **currency-exchange-service**.ribbon.listOfServers=http://localhost:8000, http://localhost:8001, **http://localhost:8002**

## 5.3.1 Service Registration
So use a naming server where all instances of microservices are registered. Whenever an instance of a ms comes up, it would register itself with the naming server.

## 5.3.2 Service Discovery
If a client is looking for a service, it will ask the naming server.

Implementation

- Create a new project Naming Server
 - Dependency :`org.springframework.cloud:spring-cloud-starter-netflix-eureka-server`
 - Add @EnableEurekaServer to the main class
 - `application.properties server.port=8761`  // This is the typical port for a naming server.
- Register the services with a the new Naming server
 - Add dependency 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-**client**'
 - Add @EnableDiscoveryClient to Register with the Naming Server.  
 - Configure the naming server location in application.properties `eureka.client.service-url.default-zone=http://localhost:8761/eureka`
 - Remove the property e.g. **currency-exchange-service**.ribbon.listOfServers=http://localhost:8000, http://localhost:8001, **http://localhost:8002**
 There are no loner any hardcoded urls.  

 So, the proxy class asks Ribbon for the list of the service it wants (from the annotation e.g. `@RibbonClient(name = "currency-exchange-service")` and the naming returns a list of the intances available (registered with it), then Feign ? will call it.

## 5.4 Conclusion
Any new instances created (or existing ones killed) which are registered, will automatically be detected by the Naming servers, and the consumer (via Ribbon) will not be affected.

So for lots of microservices, this is good.


# 6. API Gateways
In a microservices architecture, there many microservices talking to each other; 100s possibley.  And there are common feature we would need to implement for all these microservives.

## 6.1 Common Features

### 6.1.1 Authentication, Authorisation, and security
Make sure every call is authenticated, and the user is authorised for the resource.

### 6.1.2 Rate Limits
Allow a client to make only a certain number of call per hour or per day.

### 6.1.3 Fault tolerance
If  service isn't up, should give back a default response back.

### 6.1.4 Service Aggregation.
A consumer may need to call a number of ms as part of one process.  So better to aggregate the service to become 1 call.

### 6.1.5 Logging

### 6.1.6 Central Point for Debugging

So route all calls through an API gateway. e.g. logging too.  which also makes a good place for debugging and analytics.

## 6.2 Implementation

Netflix provides an implementation called **Zuul**.  Which involves 3 steps.

1. Create a Component
 - org.springframework.boot:**spring-cloud-starter-netflix-zuul**
 - org.springframework.boot:**spring-cloud-starter-netflix-eureka-client**
 - @EnableZuulProxy this is the Zuul server
 - @EnableDiscoveryClient discover clients.  Register with the Naming Server.
    - application.properties `eureka.client.service-url.default-zone=http://localhost:8761/eureka`


2. Decide what should it do when it intercepts a request
 - New Filter class e.g. ZuulLoggingFilter that extends `ZuulFilter` and implement the 4 methods.
 - The key method is `run()` which is where the logic is implemented. e.g. Security, Logging.
3. Make sure the right requests go through the API.
 - To access the server it was
 > http://localhost:8000/currency-exchange/from/EUR/to/GBP

 - Now go via Zull  via Zull with url :
 > Format : http://localhost:8765/spring.application.name/uri  
 > e.g.     http://localhost:8765/CURRENCY-EXCHANGE-SERVICE/currency-exchange/from/EUR/to/GBP

 4. Update paths for service to service to all go through API gateway
 - Update Feign Client in Proxy in the Currency Conversion to connect to Zuul and not directly (via the naming server).  Use application name of Zull e.g.
 -- from `@FeignClient(name = "currency-exchange-service")`  
 -- to `@FeignClient(name = "netflix-zuul-api-gateway-server)`  
 - Change the Proxy method to have the target service e.g.
 -- from `@GetMapping(value="/currency-exchange/from/{from}/to/{to}")`  
 -- to `@GetMapping(value="/currency-exchange-service/currency-exchange/from/{from}/to/{to}")`

 - And can put API gateway before the first service call
 -- http://localhost:8765/currency-conversion-service/currency-converter-feign/from/GBP/to/NZD/quantity/1000

 So we invoke the first microservice using the service name with the uri, it goes via the api gateway and performs the logic. The API then calls the next service using lookup and feign .  The fiegn clent in the first service's proxy is set to the next service name which goes via the API gateway.

# 7.  Distributed Tracing
With multiple inter service calls, and going between the API gateway and naming server, it becomes quite complex.  So if there is an bug or an error, we introduce distributed tracing to have centralised location to see the complete chain of requests.


## 7.1 Spring Cloud Sleuth
We assign a unique ID to a request to trace it accross components.

### 7.1.1 Implementation
Add Spring Cloud Sleuth to the components you wish. e.g. api-gateway, CurrenyConversionService, CurrencyExchangeService

- Add dependency `org.springframework.boot:**spring-cloud-starter-netflix-zuul**`
- In main class return a bean to say which requets you want to sample.

  `@Bean
  public Sampler defautlSampler() {  return Sampler.ALWAYS_SAMPLE; }`

- Add logging where you wish e.g. after a service call.
- Sleuth will add an id to each of those logs.

Problem - although each log will have the same id to match the request accroos logs, the logs are on different applications/servers/places which is pain to line up.  So we need a centralised point to store them.

## 7.2 Distributed Tracing System we use Zipkin
To consolidate / centralise this logs.  This will take from the MQ.  And zipkin is connected to a db
Others available like elastic search
- Download.  Zipkin Server as it was removed from Spring boot in 2.0.0 release.
- UI http://localhost:9411/zipkin/
- start rabbit mq
  > rabbitmq-server
- run zipkin.jar with rabbit argument (uses the amqp queueing protocol)
  > RABBIT_URI=amqp://localhost java -jar zipkin.jar

- Now Rabbit and Zipkin are connected

## 7.3 Connect Our Services to Rabbit MQ (use RabbitMQ)
All logs from these services we would be put on a queue and zipkin will pick it up.

- To services to send to Rabbit MQ and Zipkin, add :
- Add dependency `org.springframework.cloud:**spring-cloud-sleuth-zipkin**`  
-- So when sleuth logs messages, this dependency will log them in a format that will be understood by zipkin.
- Add dependency `org.springframework.cloud:**spring-cloud-starter-bus-amqp**`
-- Establish a connection to the amqp bus ; which is RabbitMQ; to put a zipkin message.
- Check - UI http://localhost:9411/zipkin/ to see all services zipkin has connected.


      CurrencyConversionService   CurrencyExchangeService   LimitsService
                            ↓             ↓                    ↓
                                        RabbitMQ
                                          ↓
                              ZipkinDistributedTracingServer
                                          ↓
                                          DB


# 8. Spring Cloud Bus
The limits-service had it's configuration in a git repository which the Spring Cloud Config server managed.  

If wanted to change the properties we would have to
1. change them in git.
2. add and commit.
3. restart the limit-service instance OR ping the limits instance with `curl -X POST http://locahost:PORT/actuator/refresh`.

If we had 100 instances, then is 100 restarts or pings.  So we want one url to refresh all instances.

There are a variety of options to use with Spring Cloud Bus (kafka, rabbit).  Here Spring Cloud bus is running over RabbitMQ (amqp is the protocal it uses)

Implementation

- spring-cloud-config/pom.xml `org.springframework.cloud:spring-cloud-starter-bus-amqp`
- limits-service/pom.xml `org.springframework.cloud:spring-cloud-starter-bus-amqp`


Explanation
- When the instances of limit-service are started, they registered with the Spring Cloud bus.
- When a refresh is called in any of these instances the ms instance would send a event to the spring cloud bus.
-- `curl -X POST http://locahost:PORT/actuator/bus-refresh`.
- Spring Cloud Bus would propogate to all instances that registered with it.

# 9. Fault Tolerance
One of the risks of a ms architecture is that one service can bring it all down.

If  service does go down, we would want to handle it gracefully and return some default behaviour that allows the request to continue.

Implementation (use Hystrix)

-- limits-service/pom.xml add dependency `org.springframework.cloud:spring-cloud-starter-netflix-hystrix`
-- Application @EnableHystrix This enable fault tolerance on all {@code Controller}s.
-- Annotate the Controller method with the fall back method e.g.

`@GetMapping(value = "/limits-fault-tolerance")`  
`@HystrixCommand(fallbackMethod = "fallBackConfiguration")`  
`public LimitsConfiguration retrieveLimitsFromConfigurationFaultTolerance() {`  
// If this service call threw an exception, then all services dependent on it would be fail.With the  fallback method defined in the annotation, we can return some default value.  
`throw new RuntimeException("Just to demo the Fault Tolerance by triggering annotation");
 }`

 `public LimitsConfiguration fallBackConfiguration() {
     return new LimitsConfiguration(9, 999);
 }`
