### Links

### 1. Currency Conversion Service
##### Direct

- 8100 http://localhost:8100/currency-converter/from/GBP/to/NZD/quantity/1000

##### Direct Feign
- 8100 http://localhost:8100/currency-converter-feign/from/GBP/to/NZD/quantity/1000

##### Zuul and Feign (service name / uri)
- 8100 http://localhost:8765/currency-conversion-service/currency-converter-feign/from/GBP/to/NZD/quantity/1000


### 2. Currency Exchange Service
##### Direct

- 8000 http://localhost:8000/currency-exchange/from/GBP/to/EUR
- 8001 http://localhost:8001/currency-exchange/from/GBP/to/EUR
- 8002 http://localhost:8002/currency-exchange/from/GBP/to/EUR

##### Zuul (service name / URI)
- 8765 http://localhost:8765/currency-exchange-service/currency-exchange/from/GBP/to/EUR

### 3. Limits Conversion Service
- 8100 http://localhost:8080/limits

### 4. H2 Console
- 8000 http://localhost:8000/h2-console/login.jsp

### 5. Naming Server [Eureka]
- 8761 http://localhost:8761

### 6. Spring Cloud Configuration Server
- 8888 http://localhost:8888/limits-service/default

### 7. Zipkin
- 9411 http://localhost:9411/zipkin/
