# Design Choices

## Layered architecture

I've decided to implement a layered architecture, as it provides a clean separation of concerns and responsibilities.
An alternative was to use a feature-based approach but decided against it to keep the codebase easy to navigate.

- Controller
    - Handles HTTP requests and responses, acting as the entry point for API endpoints.
- Exception
    - Holds the custom exceptions and the GlobalExceptionHandler. When exceptions propagate up, we will handle it in the
      GlobalExceptionHandler.
- DTO
    - Encapsulate the data and prevent direct exposure of the entity objects. I've used Java records for TradeRequest.
- Service
    - Contains the business logic of the application. This is where we will fetch the SSI data, and use the TradeRequest
      data to create the MarketSettlementMessage.
- Entity
    - JPA entity classes, which are mapped to the database tables, represents the schema.
- Repository
    - This layer uses Spring Data JPA repositories to provide an easy, abstracted framework and interaction with our
      Postgres database.
- Validator
    - Contains custom validation logic that I've added to annotate the TradeRequest record fields, to handle edge cases.
- Utils
    - Constants, and custom deserialization

## Builder pattern

Really liked the builder pattern, as it was clean and improves readability for creating objects. Hence I've implemented
this creational design pattern in the project, by annotating with Lombok.

## Endpoints

`{{url}}/api/v1/market-settlement-messages`

Added versioning for better maintainability and ease of migration in the future.

Also created swagger / openapi documentation for v1.

For more information, visit `/swagger-ui/v1.html`
``

## Schema

**SSI**

As I was not sure if the code would be changed in the future, I've decided to use an auto incrementing database id
instead as the Primary Key (PK)

**MarketSettlementMessage**

Decided to use the auto generated UUID `messageId` as the PK.