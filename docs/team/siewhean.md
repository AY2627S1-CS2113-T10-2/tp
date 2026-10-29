# Siew Hean — Project Portfolio Page

## Overview

**JobSeek** is a command-line desktop application (CLI) designed for undergraduate students and job seekers to track and manage their internship and job applications. Optimized for keyboard-driven efficiency, it allows users to record opportunities, track recruitment progress through lifecycle stages (`Pending`, `Applied`, `Interview`, `Offered`, `Accepted`, `Rejected`), filter active vs. completed applications, and search/sort entries seamlessly.

## Summary of Contributions

- **Code contributed**: [tP Code Dashboard (RepoSense)](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=siewhean)

### Enhancements implemented

- **Interactive Numbers-Only Menu Architecture & CLI Flow (`Main.java`)**:
  - *What it does*: Architected the numbers-only menu workflow in `Main.java` (`run()`, `parseCommand()`), establishing an intuitive numbered interface where each action corresponds to a specific number (`[0]` to `[10]`).
  - *Justification*: Eliminates command syntax friction and ambiguity, drastically reduces input mistakes, and provides structured validation with reprompt loops for invalid entries.
  - *Highlights*: Implemented clean separation between menu command dispatch and command execution, added graceful EOF termination, and ensured unexpected inputs are caught with user-friendly error messages.

- **Add Application Feature & Core Domain Model (`AddCommand`, `Application`, `ApplicationList`)**:
  - *What it does*: Foundational feature enabling users to add job applications with mandatory company and role fields, plus optional fields (deadline, portal link, email, notes).
  - *Justification*: Core input mechanism for the entire application.
  - *Highlights*: Architected the `Application` domain entity, `ApplicationList` collection methods, and `AddCommand` execution flow with automated persistence and confirmation feedback.

- **Edit Application Feature & Multi-Field Updates (`EditCommand`)**:
  - *What it does*: Enables users to modify specific existing fields (company, role, stage, deadline, portal link, email, notes, or all fields) of any tracked application in place without deleting and recreating it, including clearing optional fields via blank inputs.
  - *Justification*: Job hunting details constantly change (interview rounds, updated portal links, recruiter contacts, or notes).
  - *Highlights*: Designed field selection menu choices `[1]`–`[8]`, selective non-null field updating preserving unedited values, and implemented complete `undo()` state restoration across all 8 fields.

- **Optional Portal Link, Email, and Notes Tracking (PR #56)**:
  - *What it does*: Extended the domain model, `AddCommand`, `EditCommand`, `ViewCommand`, and `Storage` to support optional company portal URLs, recruiter/contact emails, and interview notes.
  - *Justification*: Gives students a central repository for application links and contact points, avoiding scattered bookmarks and spreadsheets.
  - *Highlights*: Engineered full backward compatibility in `Storage` to seamlessly load legacy 4-column and 5-column files while writing the new 8-column tab-separated format, backed by comprehensive unit and integration tests.

- **Recruitment Stage Tracking (`RecruitmentStageCommand` / `StatusCommand`)**:
  - *What it does*: Introduces recruitment stage tracking (`Pending`, `Applied`, `Interview`, `Offered`, `Accepted`, `Rejected`) for all applications.
  - *Justification*: Allows students to monitor their application pipeline and immediately identify which opportunities resulted in offers or rejections.
  - *Highlights*: Added stage normalization supporting numeric shortcuts and case-insensitive text strings, with integration into `view INDEX` detailed cards.

- **UI Pages & Console Presentation Architecture (`Ui.java`)**:
  - *What it does*: Designed and built JobSeek's console visual presentation: ASCII welcome banner, main summary dashboard (`showApplicationsSummary`), menu options layout (`showMenuOptions`), and structured card views with divider boundaries.
  - *Justification*: Provides clear visual separation between screens and significantly improves readability and user experience in a text-based terminal.
  - *Highlights*: Architected modular screen rendering with dynamic pluralization ("1 application" vs "X applications"), ASCII framing dividers, and non-intrusive error banners.

- **Defensive Programming, Robustness & Quality Assurance**:
  - *What it does*: Embedded Java assertions across model, command, and UI layers, and built extensive test suites covering edge cases, EOF handling, integer boundary overflows, multi-byte Unicode/emojis, format specifiers, and reprompt endurance loops.
  - *Justification*: Protects internal invariants, verifies non-null preconditions, and eliminates subtle runtime regressions across the team's shared codebase.

### Contributions to the User Guide (UG)

- Authored end-to-end user documentation for core features:
  - **Adding an application**: Documented prompt sequences, whitespace trimming, input validation, and optional fields.
  - **Listing applications**: Documented menu options `[2]` and `[3]`, text filter commands (`list`, `list completed`, `list all`), and detailed card view (`view INDEX`).
  - **Editing an application**: Documented field selection choices (`[1]`–`[8]`), bounds validation, optional field clearing, and sample confirmation outputs.
  - **Recruitment status/stage tracking**: Documented recruitment stages, numeric/text input methods, and status inspection via `view INDEX`.
  - **Help instructions**: Added the complete command and menu options cheat sheet.
- Structured and refined the User Guide into a logical end-to-end user journey sequence: **Add $\rightarrow$ List $\rightarrow$ Edit $\rightarrow$ Status $\rightarrow$ Search $\rightarrow$ Data $\rightarrow$ Help**.

### Contributions to the Developer Guide (DG)

- **Architecture Overview & Standardization**:
  - Authored the Architecture Overview section and created the Architecture Diagram mapping interactions between User, UI, Logic, Model, and Storage.
  - Overhauled and standardized all Mermaid diagrams across the DG, eliminating label truncations, overlapping lines, and ensuring theme consistency.
- **Class & Sequence Diagrams**:
  - Authored the Overall Architecture Class Diagram with full multiplicities across commands, UI, storage, model, and parser.
  - Authored the Add Application feature design section and accompanying Mermaid Sequence Diagram.
  - Authored the Edit Application and Recruitment Status tracking section with accompanying Mermaid Sequence Diagram and design considerations.
  - Authored the Help feature implementation overview.
- **Manual Testing Instructions**: Authored comprehensive manual testing procedures with exact expected console output code blocks covering adding, listing, editing, viewing, and status updates.
- **User Stories**: Formulated and documented key user stories for v1.0 and v2.0.

### Contributions to team-based tasks

- **Release Management**:
  - **Created the release for Milestone 2 (`v2.0.0`)**, publishing the release notes and standalone JAR on GitHub.
  - **Packaged and created the release for Milestone 1 (`v1.0.0`)**.
- **Milestone & Project Management**:
  - Set up, configured, and tracked deliverables for Milestone 1 (`v1.0`) and Milestone 2 (`v2.0`) on the team's GitHub repository.
  - Actively maintained the issue tracker across milestones, triaging tasks, creating milestone issues, assigning responsibilities, and setting labels.
- **UI Architecture & Layout Alignment**:
  - Established unified console layout conventions and designed the numbered menu system.
  - Coordinated menu scalability as features expanded to 10 options, ensuring a clean and consistent CLI experience.
- **Quality Assurance & Testing Leadership**:
  - Expanded automated test coverage across the application (authored/maintained test classes including `AddCommandTest`, `EditCommandTest`, `RecruitmentStageCommandTest`, `MainTest`, `HelpCommandTest`, `ExitCommandTest`, `ApplicationTest`, `ApplicationListTest`, `ViewCommandTest`, `ListCommandTest`, `SearchCommandTest`, `SortCommandTest`, and `UiTest`).
  - Enforced strict Java 25 and Checkstyle compliance across PRs, keeping the build warning-free.
- **Product Documentation**:
  - Authored and restructured [`README.md`](../README.md), transforming it from the starter template into a product-focused overview.
  - Co-authored [`docs/AboutUs.md`](AboutUs.md).

### Review/mentoring contributions

- Reviewed teammate PRs and provided constructive code quality and design feedback (e.g., PR #16, PR #24, PR #44, PR #45, PR #48, PR #51, PR #52, PR #54).
- Resolved complex merge conflicts across shared files (`Main.java`, `Application.java`, `Storage.java`, `UserGuide.md`, `DeveloperGuide.md`) when reconciling feature branches against upstream master, ensuring zero regressions to teammates' features.
- Assisted teammates with Checkstyle alignment, Java 25 compatibility, and Gradle build issues.

### Contributions beyond the project team

- Participated in class discussions and peer review activities.
- Shared troubleshooting solutions and development insights with peers regarding Git branching workflows and Checkstyle rule resolution.
