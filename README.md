# JobSeek

[![CI Status](https://github.com/AY2627S1-CS2113-T10-2/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2113-T10-2/tp/actions)

**JobSeek** is a command-line desktop application (CLI) built for undergraduate students and job seekers to efficiently track, manage, and monitor their internship and job applications. 

Designed for keyboard-driven efficiency, JobSeek helps students keep all application opportunities organized in one central place, monitor recruitment stages, and evaluate application outcomes without the overhead of heavy GUI applications.

---

## Key Features

- **Application Tracking (`Add`)**: Record opportunities by company and role with mandatory input validation. New entries automatically initialize with completion status `[ ]` and recruitment status `Pending`.
- **List & Card Views (`List`, `View`)**: Display active, completed, or all applications in clean numbered tables, or inspect comprehensive detail cards for any application using `view INDEX`.
- **In-Place Field Updates (`Edit`)**: Correct typos or update details (Company, Role, Status, or All) directly in place without deleting and recreating records.
- **Recruitment Lifecycle Management (`Status`)**: Track application progress across standard recruitment stages (`Pending`, `Accepted`, `Rejected`) using intuitive numeric shortcuts (`1`, `2`, `3`) or text inputs.
- **Search & Sort**: Find applications instantly by partial company name or role (case-insensitive), or display entries alphabetically without disturbing the underlying storage order.
- **Completion Tracking (`Mark`, `Unmark`)**: Toggle applications between completed and active status.
- **Numbers-Only Menu Interface**: An intuitive, structured CLI menu where options are executed with single numbers (`[0]` to `[10]`) with reprompt loops for invalid inputs.

---

## Quick Start

### Prerequisites
- **JDK 25** (Ensure Java 25 is installed and active; on macOS with SDKMAN, run `sdk use java 25.0.3.fx-zulu`).
- Intellij IDEA or any preferred IDE.

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/AY2627S1-CS2113-T10-2/tp.git
   cd tp
   ```

2. **Build the executable JAR using Gradle**:
   ```bash
   ./gradlew shadowJar
   ```
   The fat JAR will be generated at `build/libs/jobseek.jar`.

3. **Run the application**:
   ```bash
   java -jar build/libs/jobseek.jar
   ```
   *(To run with assertion checking enabled, use `java -ea -jar build/libs/jobseek.jar`)*.

4. **Running directly via Gradle**:
   ```bash
   ./gradlew run --console=plain
   ```

---

## Documentation

- **[User Guide (UG)](docs/UserGuide.md)**: Detailed feature walkthroughs, menu options cheat sheet, input sequences, and edge-case handling.
- **[Developer Guide (DG)](docs/DeveloperGuide.md)**: Architecture documentation, Command pattern design, Mermaid UML class and sequence diagrams, user stories, and manual testing guides.
- **[About Us](docs/AboutUs.md)**: Meet the development team behind JobSeek.

---

## Development & Testing

- **Run all automated tests**:
  ```bash
  ./gradlew test
  ```
- **Run Checkstyle quality analysis**:
  ```bash
  ./gradlew checkstyleMain checkstyleTest
  ```
- **Execute complete build and verification**:
  ```bash
  ./gradlew check
  ```

---
