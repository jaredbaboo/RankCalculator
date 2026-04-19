# RankCalculator

A Spring Boot application that calculates and displays league standings based on match results. The application can load match results from a file or accept manual input.

## Prerequisites

- Java 21 or later
- Gradle (included via Gradle Wrapper)

## Building the Application

### Using Gradle Wrapper (Recommended)

On **Windows**:
```bash
.\gradlew.bat build
```

On **macOS/Linux**:
```bash
./gradlew build
```

### Using Gradle (if installed globally)

```bash
gradle build
```

### Build Output

The compiled JAR file will be located at:
```
build/libs/RankCalculator-0.0.1-SNAPSHOT.jar
```

## Running the Application

### Using Gradle

On **Windows**:
```bash
.\gradlew.bat bootRun
```

On **macOS/Linux**:
```bash
./gradlew bootRun
```

### Using the JAR File

```bash
java -jar build/libs/RankCalculator-0.0.1-SNAPSHOT.jar
```

## Application Usage

Once the application starts, you will be presented with the following options:

1. **Load league results from a file** - Load match results from a file (format: `TeamA score, TeamB score`)
2. **Manually enter match results** - Enter match results interactively
3. **Cancel and exit** - Exit the application

After entering match results, the application will:
- Calculate the league standings
- Display results to the console
- Optionally save results to a file

### Input Format

Match results should be in the following format:
```
TeamA score, TeamB score
```

Example:
```
Manchester United 3, Liverpool 2
Arsenal 1, Chelsea 1
```

## Running Tests

On **Windows**:
```bash
.\gradlew.bat test
```

On **macOS/Linux**:
```bash
./gradlew test
```

## Test Coverage Reports

This project uses **JaCoCo** (Java Code Coverage) to generate comprehensive test coverage reports.

### Generating Coverage Reports

On **Windows**:
```bash
.\gradlew.bat test jacocoTestReport
```

On **macOS/Linux**:
```bash
./gradlew test jacocoTestReport
```

### Viewing Coverage Reports

After running the test coverage command, view the HTML report at:
```
build/reports/jacoco/test/html/index.html
```

The report includes:
- **Line Coverage** - Percentage of executable lines covered by tests
- **Branch Coverage** - Percentage of conditional branches covered
- **Class Coverage** - Breakdown of coverage by class

### Coverage Verification

The build is configured with a minimum coverage threshold of 50% for all classes. To verify coverage meets the minimum requirements:

On **Windows**:
```bash
.\gradlew.bat jacocoTestCoverageVerification
```

On **macOS/Linux**:
```bash
./gradlew jacocoTestCoverageVerification
```

The test task automatically generates the report after running all tests.

## Project Structure

```
src/
├── main/
│   ├── java/com/jaredbaboo/rankcalculator/
│   │   ├── RankCalculatorApplication.java    (Main entry point)
│   │   ├── LeagueRunner.java                 (CLI interface)
│   │   ├── application/                       (Use cases)
│   │   ├── domain/                            (Domain models & exceptions)
│   │   └── infrastructure/                    (Infrastructure implementations, technology specific items)
│   └── resources/
│       └── application.yaml                   (Spring Boot configuration)
└── test/
    └── java/com/jaredbaboo/rankcalculator/   (Test files)
```

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.5**
- **Gradle** (Build tool)
- **JUnit 5** (Testing)
- **JaCoCo** (Code Coverage)
- **Lombok** (Code generation)
- **Apache Commons Lang 3**

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.5/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.5/gradle-plugin/packaging-oci-image.html)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

