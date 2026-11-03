# JobSeek

[![CI Status](https://github.com/AY2627S1-CS2113-T10-2/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2113-T10-2/tp/actions)

**JobSeek** is a command-line desktop application (CLI) built for undergraduate students and job seekers to efficiently track, manage, and monitor their internship and job applications. 

Designed for keyboard-driven efficiency, JobSeek helps students keep all application opportunities organized in one central place, monitor recruitment stages, and evaluate application outcomes without the overhead of heavy GUI applications.

---
## Key Features

- **Application Tracking (`Add`)**: Record opportunities by company and role with mandatory input validation, plus optional deadlines, portal links, contact emails, and notes. New entries start incomplete (`[ ]`) at the `Applied` recruitment stage.
- **List & Card Views (`List`, `View`)**: Display active, completed, or all applications in clean numbered tables, or inspect comprehensive detail cards for any application using `view INDEX`.
- **In-Place Field Updates (`Edit`)**: Correct typos or update details (Company, Role, recruitment stage, or all fields) directly in place without deleting and recreating records.
- **Recruitment Stage Tracking**: Track application progress across standard recruitment stages (`Applied`, `Online Assessment`, `Interview`, `Offer`, `Accepted`, `Rejected`, `Withdrawn`).
- **Search, Filters & Sort**: Find applications by company or role, combine filters for company, role, stage, deadline, and completion status, or display entries alphabetically without changing their saved order.
- **Completion Tracking (`Mark`, `Unmark`)**: Toggle whether an application is complete, independently of its recruitment stage.
- **Deadline Overview (`Today`)**: Review overdue, due today, and upcoming application deadlines. JobSeek also displays deadline warnings when it starts.
- **Undo (`undo`)**: Reverse recent add, delete, edit, stage, and completion changes.
- **Numbers-Only Menu Interface**: An intuitive, structured CLI menu where options are executed with single numbers (`[0]` to `[10]`) with reprompt loops for invalid inputs.

---

## Quick Start

### Prerequisites
- **JDK 25**. Confirm the active Java version with `java -version`. On macOS with SDKMAN, select Java 25 with `sdk use java 25.0.3.fx-zulu`.
- A terminal to run the application. IntelliJ IDEA or another Java IDE is optional.

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
   On Windows PowerShell, use `.\gradlew.bat shadowJar`.
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

### Using JobSeek

JobSeek opens a numbered menu in the terminal. Enter a menu number and follow the prompts, or enter a one-line command at the prompt. For example:

```text
add c/Google r/SWE Intern d/2026-11-30
list
edit 1 s/Interview
view 1
```

The `add` command requires a company (`c/`) and role (`r/`). Deadline (`d/`), portal link (`p/`), contact email (`e/`), and notes (`n/`) are optional. New applications begin at the `Applied` stage and are marked incomplete. Use `help` to see command shortcuts, `undo` to reverse the latest change, and `exit` or menu option `0` to quit.

JobSeek creates and updates its data file in the folder from which it is launched. Run it from a folder where you have write permission, and use a terminal so you can interact with the menu and prompts. See the [User Guide](UserGuide.md) for all commands, filtering options, and examples.

---

## Documentation

- **[User Guide (UG)](UserGuide.md)**: Detailed feature walkthroughs, menu options cheat sheet, input sequences, and edge-case handling.
- **[Developer Guide (DG)](DeveloperGuide.md)**: Architecture documentation, Command pattern design, Mermaid UML class and sequence diagrams, user stories, and manual testing guides.
- **[About Us](AboutUs.md)**: Meet the development team behind JobSeek.

---

## Development & Testing

Run these commands from the repository root. On Windows PowerShell, replace `./gradlew` with `.\gradlew.bat`.

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

- **Build the runnable JAR**:
  ```bash
  ./gradlew shadowJar
  ```

---
