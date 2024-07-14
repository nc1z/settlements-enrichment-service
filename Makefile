IMAGE_NAME=settlements-enrichment-service
COMPOSE_FILE=compose.dev.yaml

# Build the Docker image
.PHONY: build
build:
	@echo "Building the Docker image..."
	docker build -t $(IMAGE_NAME) .

# Run the application
.PHONY: run
run:
	@echo "Starting the application with Docker Compose..."
	docker-compose -f $(COMPOSE_FILE) up --build -d

# Run tests
.PHONY: test
test: build
	@echo "Running tests..."
	docker-compose -f $(COMPOSE_FILE) run --rm tests

# Stop the application and remove containers
.PHONY: stop
stop:
	@echo "Stopping the application..."
	docker-compose -f $(COMPOSE_FILE) down

# Clean up
.PHONY: clean
clean: stop
	@echo "Cleaning up Maven build artifacts..."
	./mvnw clean

# Remove Docker images
.PHONY: remove-images
remove-images:
	@echo "Removing Docker images..."
	docker rmi $(IMAGE_NAME) || true
