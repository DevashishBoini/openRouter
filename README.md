# Spring Boot Backend

A simple Spring Boot REST API application.

## Prerequisites

- Java 17+
- IntelliJ IDEA

## Setup

1. Open project in IntelliJ
2. Click **"Setup SDK"** if prompted → Select Java 17
3. Wait for Maven to download dependencies

## Run

### In IntelliJ:
1. Open `src/main/java/backend/MainApplication.java`
2. Click green play button ▶️ next to `main()` method
3. Wait for: `Started MainApplication in X seconds`

### Or use keyboard:
- **Mac:** `⌃ R` or `⇧ F10`
- **Windows/Linux:** `Shift + F10`

## Test Endpoints

Once running, open browser:

```
http://localhost:8080/hello
http://localhost:8080/hello?name=YourName
http://localhost:8080/
```

Or use curl:
```bash
curl http://localhost:8080/hello
```

## Project Structure

```
src/
├── main/
│   ├── java/backend/
│   │   ├── MainApplication.java          # Entry point
│   │   └── controller/
│   │       └── HelloController.java      # REST endpoints
│   └── resources/
│       └── application.properties        # Configuration
└── test/
    └── java/backend/
        └── controller/
            └── HelloControllerTest.java  # Tests
```

## Run Tests

- Right-click test file → **"Run"**
- Or: **Ctrl + Shift + F10** (Mac: `⌃ ⇧ R`)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
server.port=8080                    # Server port
spring.application.name=app         # App name
logging.level.backend=DEBUG         # Logging level
```

## Build

**Maven panel** → **Lifecycle** → Double-click **package**

Creates: `target/backend-0.0.1-SNAPSHOT.jar`

Run JAR:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Stop

- Click red stop button ⏹️
- Or: **Ctrl + F2** (Mac: `⌘ F2`)
