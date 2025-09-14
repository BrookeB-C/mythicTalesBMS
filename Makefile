# Simple Makefile for Mythic Tales BMS (Taplist)

APP_NAME := taplist
VERSION  := 0.1.0
JAR_FILE := target/$(APP_NAME)-$(VERSION).jar
IMAGE    := mythictales/$(APP_NAME):latest
PORT     := 8080
JAVA_OPTS ?=

.PHONY: help run build test package clean format check-format docker-build docker-run docker-stop docker-rm spotbugs spotbugs-strict

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
