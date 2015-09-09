# Spring Cloud Request Correlation

> A Spring Cloud starter for easy setup request correlation

[![Build Status](https://travis-ci.org/jmnarloch/request-correlation-spring-cloud-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/request-correlation-spring-cloud-starter)

## Features

Allows to unique identify and track your request by passing `X-Request-Id` header across remote calls.  

## Setup

Add the Spring Cloud starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>request-correlation-spring-cloud-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

Annotate every Spring Boot / Cloud Application with `@EnableRequestCorrelation` annotation and that's it.

```java
@EnableRequestCorrelation
@SpringBootApplication
public class Application {

}
```

## How it works?


## License

Apache 2.0