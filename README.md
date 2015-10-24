# Spring Cloud Request Correlation

> A Spring Cloud starter for easy setup request correlation

[![Build Status](https://travis-ci.org/jmnarloch/request-correlation-spring-cloud-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/request-correlation-spring-cloud-starter)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/request-correlation-spring-cloud-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/request-correlation-spring-cloud-starter?branch=master)

## Features

Allows to uniquely identify and track your request by passing `X-Request-Id` header across remote calls.  

## Setup

Add the Spring Cloud starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>request-correlation-spring-cloud-starter</artifactId>
  <version>1.2.0</version>
</dependency>
```

## Usage

Annotate every Spring Boot / Cloud Application with `@EnableRequestCorrelation` annotation. That's it.

```java
@EnableRequestCorrelation
@SpringBootApplication
public class Application {

}
```

## Properties

You can configure fallowing options:

```
request.correlation.header-name=X-Request-Id # sets the header name to be used for request identification (X-Request-Id by default)
request.correlation.client.http.enabled=true  # enables the RestTemplate header propagation (true by default)
request.correlation.client.feign.enabled=true # enables the Fegin header propagation (true by default)
```

## How it works?

The annotation will auto register servlet filter that will process any inbound request and correlate it with
unique identifier.

## Retrieving the request identifier

You can retrieve the current request id within any request bound thread through 
`RequestCorrelationUtils.getCurrentCorrelationId`.

## Propagation

Besides that you will also have transparent integration with fallowing:

* RestTemplate - any Spring configured `RestTemplate` will be automatically populated with the request id.
* Feign clients - similarly a request interceptor is being registered for Feign clients
* Zuul proxy - any configured route will be also 'enriched' with the identifier

## Applications

The extension itself simply gives you means to propagate the information. How you going to use it is up to you.

For instance you can apply this information to your logging MDC map. You can achieve that by registering 
`RequestCorrelationInterceptor` bean. The `RequestCorrelationInterceptor` gives you only an entry point so that
any fallowing operation would be able to access the correlation identifier. You may also use Spring's
[HandlerInterceptor](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html)
and set the value there.

```java
@Bean
public RequestCorrelationInterceptor correlationLoggingInterceptor() {
    return new RequestCorrelationInterceptor() {
        @Override
        public void afterCorrelationIdSet(String correlationId) {
            MDC.put("correlationId", correlationId);
        }
    };
}
```

If your are using Vnd.errors you can use that as your logref value

```java
@ExceptionHandler
public ResponseEntity error(Exception ex) {

    final VndError vndError = new VndError(RequestCorrelationUtils.getCurrentCorrelationId(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.error+json")
            .body(vndError);
}
```

Another use case is to save that with your Spring Boot Actuator's audits when you implement custom `AuditEventRepository`.

## Migrating to 1.1

The properties enable has been renamed to enabled to match the Spring convention, besides that there are active by default

## License

Apache 2.0