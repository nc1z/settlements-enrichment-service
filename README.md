# Settlements Enrichment Service

A SpringBoot REST server that receives trade requests, performs enrichment using SSI reference data, and returns market
settlement messages.

Made by Neil :smile:

## Before we start...

This `README.md` consists of the instructions to get the application up and running.

For more information on the below topics, please refer to the `/docs` folder:

1. Approaches and assumptions
2. Design choices
3. Testing approaches and coverage
4. Others (Validations, Exception handling, Logging, Coding Practices)

## Getting Started

For the purpose of the project, `.env` and all required configuration files would have been included in the zip file.

### Pre-requisites

Before you can set up and run the Settlements Enrichment Service application, make sure you have the following installed
on your machine:

1. **Docker**
    - Ensure that Docker is installed and running on your machine. You can download and install Docker
      from [here](https://www.docker.com/products/docker-desktop).

2. **Environment Variables**
    - Create an `.env` file in the root directory of the project, refer to `.env.example`. This should be already
      included in
      the zip folder.

3. **Make**
    - Typically available on Unix-like operating systems, including macOS and Linux
    - For windows you will need to run `choco install make`
    - This is optional if you want to run the project using manual commands, required if you want to run quick commands
      using the `Makefile` provided

### A. Running the application with Docker

1. Make sure docker is running on your machine
2. You can either use make or docker-compose directly to build and run the application:

    ```bash
   make run
   ```
   or
    ```bash
   docker-compose -f compose.dev.yaml up --build -d settlements-enrichment-service postgres
   ```
3. Once the containers are up and running, refer to `Using the service` section below.

### B. Running the application locally

1. To run the application locally, you will need these additionally:
    - IntelliJ IDEA (or any other Java IDE)
    - Java Development Kit (JDK) 17
        - Verify the installation by running the following command in your terminal:
        ```
      java -version
      ```
2. Open the SettlementsEnrichmentServiceApplication class.
3. Right-click on the class and select Run 'SettlementsEnrichmentServiceApplication'
4. The application should start, and you should see logs in the run window.

### Using the service

Once the containers have started, here are some references for you to explore:

- Access the API documentation at: `http://localhost:8080/swagger-ui/v1.html`
- Use the postman collection to test endpoints and make
  requests: `settlements_enrichment_service.postman_collection.json`
- Run tests - 3 options
    - `make test`
    - `docker-compose -f compose.dev.yaml run --rm tests`
    - Run it locally via the IDE (e.g. IntelliJ)
- Get test coverage report (It has good coverage!):
   ```
   mvn clean test                    
   mvn jacoco:report
  ```
  View coverage report in browser: `http://localhost:63342/settlements-enrichment-service/target/site/jacoco/index.html`

### Quick Start

Once the server starts, execute the below commands to test the application out

`GET`

```cURL
curl -X GET "http://localhost:8080/api/v1/market-settlement-messages/16846548" -H "Content-Type: application/json"
```

`CREATE`

```cURL
curl --location 'localhost:8080/api/v1/market-settlement-messages' \
--header 'Content-Type: application/json' \
--data '{
  "TradeId": "16846548",
  "SSI Code": "OCBC_DBS_1",
  "Amount": 12894.65,
  "Currency": "USD",
  "Value Date": "20022020"
}'
```

### Debugging

`./mvnw: not found`

- This should not be a concern when running directly from the unzipped project file. This error occurs when using
  windows, and cloning the repository. Make sure that the line ending of
  the mvnw is LF, when you clone the repository, git for Windows may change the line
  endings to CRLF. This can be resolved by running `git config --global core.autocrlf input` before cloning.

`Docker commands are stuck during execution`

- Restart docker