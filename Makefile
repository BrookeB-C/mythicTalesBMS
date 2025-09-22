# Simple Makefile for Mythic Tales BMS (Taplist)

APP_NAME := taplist
VERSION  := 0.1.0
JAR_FILE := target/$(APP_NAME)-$(VERSION).jar
IMAGE    := mythictales/$(APP_NAME):latest
PORT     := 8080
JAVA_OPTS ?=

.PHONY: help run build test package clean format check-format docker-build docker-run docker-stop docker-rm spotbugs spotbugs-strict setup-commit-template db-up-staging db-stop-staging db-up-prod db-stop-prod kafka-up kafka-down kafka-logs staging_up prod_up staging_stop prod_stop

help:
	@echo "Targets:"
	@echo "  run           - Run Spring Boot locally"
	@echo "  build         - Compile and package (skip tests)"
	@echo "  test          - Run tests"
	@echo "  package       - Package application jar"
	@echo "  clean         - Clean build artifacts"
	@echo "  format        - Auto-format code with Spotless"
	@echo "  check-format  - Verify formatting (Spotless check)"
	@echo "  docker-build  - Build Docker image ($(IMAGE))"
	@echo "  docker-run    - Run Docker container on port $(PORT)"
	@echo "  docker-stop   - Stop running container"
	@echo "  docker-rm     - Remove stopped container"
	@echo "  spotbugs      - Generate SpotBugs report (non-fatal)"
	@echo "  spotbugs-strict - Fail on HIGH severity SpotBugs issues"
	@echo "  db-up-staging - Start Postgres (staging) via compose"
	@echo "  db-stop-staging - Stop Postgres (staging)"
	@echo "  db-up-prod    - Start Postgres (prod) via compose"
	@echo "  db-stop-prod  - Stop Postgres (prod)"
	@echo "  kafka-up      - Start dev Kafka stack"
	@echo "  kafka-down    - Stop dev Kafka stack"
	@echo "  kafka-logs    - Tail Kafka broker logs"
	@echo "  staging_up    - Start staging Postgres and Kafka"
	@echo "  staging_stop  - Stop staging Postgres and Kafka"
	@echo "  prod_up       - Start prod Postgres and Kafka"
	@echo "  prod_stop     - Stop prod Postgres and Kafka"
	@echo "  setup-commit-template - Configure git commit.template to .gitmessage.txt"
	@echo "  ui-build      - Install deps and build the ui-library bundle"
	@echo "  ui-clean      - Remove ui-library build artifacts"

run:
	mvn spring-boot:run

build:
	mvn -q -DskipTests package

test:
	mvn test

package:
	mvn package

clean:
	mvn clean
	rm -rf src/main/resources/static/ui/

format:
	mvn -q spotless:apply

check-format:
	mvn -q spotless:check

docker-build:
	docker build -t $(IMAGE) .

docker-run:
	docker run --rm -it -p $(PORT):8080 -e JAVA_OPTS="$(JAVA_OPTS)" --name $(APP_NAME) $(IMAGE)

docker-stop:
	-@docker stop $(APP_NAME) >/dev/null 2>&1 || true

docker-rm:
	-@docker rm $(APP_NAME) >/dev/null 2>&1 || true

spotbugs:
	mvn -B -ntp -Dspotbugs.failOnError=false -Dspotbugs.threshold=Low spotbugs:spotbugs

spotbugs-strict:
	mvn -B -ntp -Dspotbugs.failOnError=true -Dspotbugs.threshold=High spotbugs:check

# Docker Compose helpers for Postgres
db-up-staging:
	@[ -f .env ] || (echo "Missing .env. Copy .env.example to .env and set passwords." && exit 1)
	docker compose --profile staging up -d postgres-staging

db-stop-staging:
	docker compose stop postgres-staging || true
	docker compose rm -f postgres-staging || true

db-up-prod:
	@[ -f .env ] || (echo "Missing .env. Copy .env.example to .env and set passwords." && exit 1)
	docker compose --profile prod up -d postgres-prod

db-stop-prod:
	docker compose stop postgres-prod || true
	docker compose rm -f postgres-prod || true

kafka-up:
	docker compose -f docker/compose/kafka-dev.yml up -d

kafka-down:
	docker compose -f docker/compose/kafka-dev.yml down

kafka-logs:
	docker compose -f docker/compose/kafka-dev.yml logs -f kafka

staging_up: db-up-staging kafka-up
	@echo "Staging Postgres and Kafka stack started"

prod_up: db-up-prod kafka-up
	@echo "Prod Postgres and Kafka stack started"

staging_stop: kafka-down db-stop-staging
	@echo "Staging Postgres and Kafka stack stopped"

prod_stop: kafka-down db-stop-prod
	@echo "Prod Postgres and Kafka stack stopped"

setup-commit-template:
	git config commit.template .gitmessage.txt

ui-build:
	cd ui-library && export PATH="/opt/homebrew/opt/node@18/bin:$$PATH" && npm install && npm run build

ui-clean:
	rm -rf ui-library/node_modules ui-library/package-lock.json src/main/resources/static/ui/
