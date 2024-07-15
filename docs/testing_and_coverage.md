# Testing Approaches and Coverage

## Approach

With the time constraint, I did not choose to use Test Driven Development (TDD) in the creation of this project.

These were my priorities during my scoping of the effort + time:

1. Service Unit Tests
    - These unit tests are the most important, as they cover the core purpose and business logic of our application.
    - As long as the input and output was correct and covered for all edge cases, our application has served as a
      minimum viable product
2. Controller Unit Tests
    - This does not require the applicationContext as well, and was quick to implement with mocking.
    - This is valuable as we can test validations on the TradeRequest.
3. Integration Tests
    - These required a bit of setup and took the most time. However this provided good coverage of tests end-to-end by
      simulating http requests to our controllers and interacting with a test database.
    - Given these are the most realistic, it provided confidence that the application was working as intended

## Coverage

Ignored these in the coverage report

- Main class
- Lombok generated methods

Some that I've not yet replicated:

- Simulating internal server error (Covered many cases such that it was hard to find edge cases to trigger a 500)

Get test coverage report, run this command:

   ```
   mvn clean test                    
   mvn jacoco:report
  ```

View coverage report in browser: `http://localhost:63342/settlements-enrichment-service/target/site/jacoco/index.html`
