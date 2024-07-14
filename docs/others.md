# Others

## Validations

My main goal with validations was to make sure that the type and format is correct for all fields.

**Priority:** Validations on the TradeRequest

- This was the entrypoint and if this is validated properly the chances of errors for the remaining layers of the
  application would be significantly lower.
- Used Lombok annotations and Jakarta Validation Constraint annotations
- Also used custom Json deserializers to enforce the correct type

## Exception Handling

Using a GlobalExceptionHandler, the handling of exceptions are centralized and easy to maintain.

Created a custom ErrorResponse class and made sure that most if not all of the responses are in a consistent format.

## Logging

I wanted to log meaningful places that will be easier to debug in production. So I've kept logging to be minimal and
focused on the important
areas where data was handled. This means in the service class where most of the core business logic lies.

I wanted to explore customisation of the logs so that I can display the important information such as:

- Request
- Response
- Errors/Exceptions

Unfortunately due to the constraint of time, I've decided to use an easier approach, which was to set logging levels to
DEBUG, which provided *too much information but it serves the purpose.

Logging was implemented with the slf4j logger that came with Springboot,

### Actuator

When this is deployed in production, I foresee that health checks are required, especially in microservices and
distributed systems.

By exposing the Springboot Actuator's health check endpoint, this allowed better monitoring.

This can be accessed at `localhost:8080/actuator/health`

## Coding Practices

I wanted to make it easier to setup and run the application on different devices, so I've setup a Dockerfile, and a
Makefile.

This application was coded on a Macbook, but I've tested it on a Windows desktop and it worked well.

## Future enhancements

Some other things I wanted to work on but deprioritized were:

- Pre-commit hooks for linting, formatting and testing
- Configuring test containers on CI
- Configure Spring Security / At least secure our endpoints with an API token
- Deploy it to AWS with a live link (though rate limiting has to be implemented)
    - Wanted to use elastic beanstalk for quick deployment
- Configure logging levels properly and filter out only request, response and error
- Add caching for GET requests
- Database Indexing for TradeId (?)
