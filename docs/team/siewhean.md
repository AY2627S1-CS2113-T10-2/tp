# Siew Hean - Project Portfolio Page

## Overview

**JobSeek** is a command-line desktop application (CLI) designed for undergraduate students and job seekers to track and manage their internship and job applications. Optimized for keyboard-driven efficiency, it allows users to record opportunities, track recruitment progress through lifecycle stages (`Pending`, `Accepted`, `Rejected`), filter active vs. completed applications, and search/sort entries seamlessly.

### Summary of Contributions

- **Code contributed**: [RepoSense Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=siewhean)

---

### Enhancements Implemented

#### 1. Interactive Numbers-Only Menu Architecture & CLI Flow
- **What it does**: Designed and implemented the numbers-only menu workflow in `Main.java` (`run()`, `parseCommand()`), establishing an intuitive numbered interface where each action corresponds to a specific number (`[0]` to `[10]`).
- **Justification**: Eliminates typing friction and ambiguity for users, drastically reduces input mistakes, and provides structured validation with reprompt loops for invalid entries.
- **Highlights**: Implemented clean separation between menu command parsing and command execution, added graceful EOF termination, and ensured all unexpected inputs are caught with user-friendly error messages.

#### 2. Add Application Feature & Core Domain Model
- **What it does**: Allows users to add job and internship applications by prompting for `Company:` and `Role:`. Rejects blank/whitespace inputs and reprompts until valid.
- **Justification**: Serves as the foundational entry mechanism for the application.
- **Highlights**: Architected the initial `Application` class, `ApplicationList` collection methods, and `AddCommand` execution flow with automated persistence and confirmation feedback.

#### 3. Edit Application Feature (`EditCommand`)
- **What it does**: Enables users to modify specific existing fields (Company, Role, Status, or All) of any tracked application in place without deleting and recreating it.
- **Justification**: Real-world job searching frequently requires updating details due to typos, updated job titles, or advancing interview rounds.
- **Highlights**: Integrated menu option `[10]`, implemented selective non-null field updating (preserving unedited fields), and added strict bounds and format checks.

#### 4. Recruitment Status Tracking (`StatusCommand` & Model Integration)
- **What it does**: Introduces recruitment stage tracking (`Pending`, `Accepted`, `Rejected`) for all applications. Applications default to `Pending` upon creation and can be updated using `StatusCommand` or through the Edit menu flow.
- **Justification**: Allows students to monitor their application pipeline and immediately identify which opportunities resulted in offers or rejections.
- **Highlights**: Added status normalization supporting both numeric shortcuts (`1`, `2`, `3`) and case-insensitive text strings (`pending`, `accepted`, `rejected`), with integration into `view INDEX` detailed cards.

#### 5. UI Pages & Console Presentation Architecture (`Ui.java`)
- **What it does**: Designed and created the core UI pages and visual presentation for JobSeek, including the ASCII welcome banner logo, the formatted main applications dashboard and summary view (`showApplicationsSummary`), the structured menu options interface (`showMenuOptions`), and detailed card views with divider boundaries.
- **Justification**: A clean, structured UI page layout gives users an immediate visual overview of their tracked applications, provides clear visual separation between screens, and significantly improves readability and user experience in a text-based terminal.
- **Highlights**: Architected `Ui.java` with modular screen rendering methods, dynamic pluralization ("1 application tracked" vs "X applications tracked"), ASCII framing dividers, and non-intrusive error banners that don't disrupt screen flow.

#### 6. Defensive Programming, Robustness Checks & System Reliability
- **What it does**: Embedded Java `assert` statements across model, command, and UI layers (`Application`, `ApplicationList`, `AddCommand`, `EditCommand`, `StatusCommand`, `Main`) and added extensive automated test checks covering boundary conditions, abrupt stream termination (EOF), integer overflow, Unicode/emojis, format strings, and repeated reprompt cycles.
- **Justification**: Protects internal invariants, verifies non-null preconditions, prevents unhandled runtime exceptions, and drastically reduces bugs across the team's shared codebase to ensure high system stability and reliability.
- **Highlights**: Expanded test verification to guard against adversarial input edge cases, eliminating subtle edge-case failures and ensuring the team's features interact reliably without regressions.

---

### Contributions to the User Guide (UG)

- Authored the user documentation for core features:
  - **`### Adding an application`**: Detailed prompt sequences, whitespace trimming, input validation, and initial default attributes.
  - **`### Listing applications`**: Detailed menu options `[2]` and `[3]`, text filter commands (`list`, `list completed`, `list all`), and detailed card view (`view INDEX`).
  - **`### Editing an application`**: Documented field selection choices (`1: Company, 2: Role, 3: Status, 4: All`), bounds validation, and sample confirmation outputs.
  - **`### Recruitment status tracking`**: Documented recruitment stages, numeric/text input methods, and status inspection via `view INDEX`.
  - **`## Help instructions`**: Added the complete command and menu options cheat sheet.
- Structured and refined the User Guide into a logical end-to-end user journey sequence: **Add $\rightarrow$ List $\rightarrow$ Edit $\rightarrow$ Status $\rightarrow$ Search $\rightarrow$ Data $\rightarrow$ Help**.

---

### Contributions to the Developer Guide (DG)

- **Architecture & Command Pattern**: Authored the architecture overview and created a comprehensive Mermaid Class Diagram mapping out the `Command` inheritance hierarchy, command implementations (`AddCommand`, `EditCommand`, `StatusCommand`, `HelpCommand`, `ListCommand`), `ApplicationList`, and `Application`.
- **Feature Design & Sequence Diagrams**:
  - Authored the **Add application feature** design and implementation section with an accompanying Mermaid Sequence Diagram.
  - Authored the **Edit application and recruitment status tracking** section, documenting selective field updating, status normalization, assertion checks, and an accompanying Mermaid Sequence Diagram.
  - Authored the **Help feature** implementation overview.
- **User Stories**: Formulated and documented key user stories for **v1.0** (adding applications, viewing help instructions) and **v2.0** (editing details, tracking recruitment stages).
- **Manual Testing Instructions**: Authored detailed test procedures covering adding applications, viewing help, editing fields, and managing recruitment statuses.

---

### Contributions to Team-Based Tasks

- **Milestone & Project Management**:
  - Closed **Milestone 1 (`v1.0`)** and created/configured **Milestone 2 (`v2.0`)** on GitHub.
  - Actively maintained and organized the team's issue tracker across both milestones, assigning issues, setting labels, and ensuring deliverables were prioritized.
  - Packaged and released the official standalone JAR distribution file for Milestone 1 (`v1.0`).
- **UI Architecture, Pages & Alignment**:
  - Designed and created the core UI pages, layouts, and console menu presentation.
  - Aligned the team's UI by planning the user flow and ensuring the menu page is organized and uncluttered. As the menu expanded to 10 options, suggested to teammates to merge related features and create dedicated sub-menu pages to keep navigation intuitive.
- **Team Leadership & Coordination**:
  - Led and initiated team discussions for milestone task planning and scope division.
  - Conducted weekly check-ins with team members to track progress, resolve blockers, and keep the team aligned with deadlines.
- **Testing, Bug Reduction & Quality Assurance**:
  - Implemented extensive automated test checks across the entire system to ensure the team's code is exceptionally robust and to drastically reduce bugs across all application flows.
  - Authored and maintained comprehensive unit and integration test suites: `AddCommandTest`, `EditCommandTest`, `StatusCommandTest`, `MainTest`, `HelpCommandTest`, `ExitCommandTest`, `ApplicationTest`, `ApplicationListTest`, `ViewCommandTest`, `ListCommandTest`, `SearchCommandTest`, `SortCommandTest`, and `UiTest`.
  - Added targeted edge-case coverage for prompt stream interruptions (EOF handling), 64-bit integer boundary overflows, multi-byte Unicode/emoji rendering, format specifiers, and reprompt endurance loops.
  - Ensured all 141+ automated tests pass cleanly with zero Checkstyle violations and full compliance with Java 25 standards.
- **Git & PR Management**:
  - Managed feature branches and PR lifecycle: PR #10, #11, #12, #18, #20, #37 (`feature-edit-command`), and #38 (`feature-status-command`).
  - Resolved complex merge conflicts when reconciling `feature-edit-command` and `feature-status-command` against upstream master, re-aligning menu options without breaking teammate features or tests.
- **Documentation Leadership**:
  - Authored and updated [`README.md`](file:///Users/Siew%20Hean/Documents/GitHub/tp/README.md), transitioning it from the default Duke starter template into a comprehensive product overview.
  - Co-authored [`docs/AboutUs.md`](file:///Users/Siew%20Hean/Documents/GitHub/tp/docs/AboutUs.md) and maintained documentation alignment across User Guide, Developer Guide, and project deliverables.
