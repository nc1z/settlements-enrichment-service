IMAGE_NAME=settlements-enrichment-service
COMPOSE_FILE=compose.dev.yaml
ENV_FILE=.env

# Load environment variables from the .env file
include $(ENV_FILE)
export $(shell sed 's/=.*//' $(ENV_FILE))

# Build the Docker image
.PHONY: build
build:
	@echo "Building the Docker image..."
	docker build -t $(IMAGE_NAME) .

# Run the application
.PHONY: run
run:
	@echo "Starting the application with Docker Compose..."
	docker-compose -f $(COMPOSE_FILE) up --build -d settlements-enrichment-service postgres
	@echo "Removing dangling images..."
	docker image prune -f

# Run tests
.PHONY: test
test: build
	@echo "Running tests..."
	docker-compose -f $(COMPOSE_FILE) run --rm tests
	@echo "Removing dangling images..."
	docker image prune -f

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
