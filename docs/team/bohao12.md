# Peng Bohao - Project Portfolio Page

## Overview

JobSeek is a command-line desktop application (CLI) designed for undergraduate students and job seekers to track and manage their internship and graduate-role applications, monitor upcoming deadlines, and organize their recruitment pipeline.

## Summary of Contributions

### Code contributed

[RepoSense Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=bohao12&breakdown=true)

### Enhancements implemented

- **Foundational Architecture & Code Skeleton** ([#13](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/13), [#14](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/14)):
  - *What it does*: Bootstrapped the initial JobSeek codebase from scratch, establishing core domain models (`Application`, `ApplicationList`), command pattern hierarchy (`Command`), console interface (`Ui`), and persistence (`Storage`) with Java 25.
  - *Justification & Highlights*: Established package structures early so team members could develop concurrently without merge conflicts. Decoupled business logic from I/O and encapsulated list operations with index bounds protection.
- **One-line Typed Command Parser & Prefix Sanitization** ([#50](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/50), [#78](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/78)):
  - *What it does*: Built `CommandParser` enabling single-line typed commands (`add c/Google r/SWE d/2026-11-30`, `find ...`, `undo`) alongside the numbered menu.
  - *Justification & Highlights*: Eliminates multi-step prompts for power users. Implemented regex prefix tokenization requiring whitespace, preventing false splits on slashes within text (`AT&T/Intel`, `C/C++`), and allowed clearing optional fields via prefix (`edit 1 d/ -`). Backed by 24 unit tests.
- **Multi-criteria Filter Engine (`find`)** ([#51](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/51)):
  - *What it does*: Filters applications across company (`c/`), role (`r/`), stage (`s/`), deadline range (`a/`, `b/`), and status (`m/`) simultaneously.
  - *Justification & Highlights*: Applied Strategy pattern (`ApplicationFilters`) composing `Predicate<Application>` lambdas joined via logical `AND`. Extensible for new filters without modifying `FindCommand` execution (OCP). Results retain master 1-based indexes for direct use with `edit`, `delete`, or `view`.
- **Reversible Undo Architecture & Robust Mutation Handling** ([#52](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/52), [#77](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/77), [#83](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/83)):
  - *What it does*: Added deletion confirmation prompts and the `undo` command to iteratively revert mutating operations (`add`, `delete`, `edit`, `status`, `mark`, `unmark`).
  - *Justification & Highlights*: Protects against accidental data loss. Architected `UndoableCommand` and stack-based `CommandHistory` storing inverse state deltas. Prevented no-op undo entries for unchanged edits or re-marking, supported prompt cancellation (`cancel`), and handled abrupt EOF safely without corrupting history. Backed by 18 unit tests.
- **Storage Reliability, Atomic Saves & Corruption Recovery** ([#45](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/45), [#47](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/47), [#76](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/76), [#82](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/82)):
  - *What it does*: Replaced destructive direct writes with temporary-file atomic replacement to eliminate truncated or empty save files on crash/failure. Added UTF-8 Byte-Order Mark (BOM) handling, automatic backups before skipping corrupted records, and persisted recruitment stages across restarts.
- **Model Encapsulation & Defensive Invariants** ([#84](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/84)):
  - *What it does*: Hardened `Application` to reject blank companies/roles using Unicode-aware validation (`String.isBlank()`). Fully encapsulated `ApplicationList` by exposing only unmodifiable collection views, utilizing defensive copies, and rejecting null entries with `NullPointerException`.
- **Application Details, Listing, and Status** ([#32](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/32), [#34](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/34)):
  - Implemented `view INDEX` details card, filtered `list [completed|all]`, `mark`/`unmark` completion toggles, and `today` deadline dashboard.

### Contributions to the User Guide

- **Authored sections**: One-line typed command syntax and prefix rules, Finding with combined filters, Undoing changes and delete confirmation, Today dashboard, Listing applications, Viewing application details, Marking/unmarking applications, and the Command Summary quick reference table.
- **Updated sections & audit** ([#53](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/53), [#78](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/78), [#80](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/80)): Adding applications (deadlines), Deleting applications (confirmation), Data persistence (status saving, corrupted-line recovery), clearing optional fields, and prompt cancellation.

### Contributions to the Developer Guide

- **Authored design sections**: Typed command parser, Multi-criteria filter engine, Delete confirmation and undo architecture, Today dashboard, View command, Filtered listing, and Mark/unmark features (each with design considerations and alternatives).
- **Documented architectural robustness** ([#82](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/82), [#84](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/84)): Documented storage atomic file replacement guarantees and non-atomic fallback limitations, model encapsulation, and non-null domain invariants.
- **UML diagrams added/updated**: Architecture overview, Domain model and command hierarchy class diagrams, Undo architecture class diagram, Sequence diagrams for Typed command parsing, Multi-criteria `find`, Delete-with-undo, `ViewCommand`, `ListCommand`, `MarkCommand`, deadline handling, status editing, and storage loading.
- **Documentation restructuring & sections filled** ([#53](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/53)): Fixed section corruption, authored product scope, non-functional requirements (NFRs), glossary, user stories, and manual testing procedures.

### Contributions to team-based tasks

- Set up initial codebase skeleton (`Application`, `Command`, `Ui`, `Storage`, `ApplicationList`, `Main`) and configured `build.gradle` for Java 25 ([#13](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/13)).
- Isolated test infrastructure so unit and integration test executions save to temporary files instead of overwriting user data in `data/applications.txt` ([#79](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/79)).
- Updated project branding across README, organized help text into typed-command and menu lists ([#80](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/80)), and configured team member portfolio links in `AboutUs.md`.
- Opened, tracked, and resolved GitHub issues for features and bug fixes (e.g., [#27](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/27), [#46](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/46)).
- Reviewed all documentation written by teammates against actual code implementation.

### Review/mentoring contributions

- **Internal Team Reviews & Mentoring**: Reviewed and merged teammates' PRs (e.g., [PR #1](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/1)), assisted in resolving git merge conflicts across feature branches, aligned command-model interfaces, and audited PRs for Checkstyle and test coverage.
- **Cross-Team DG & UML Review**: Provided 5 detailed review comments on the Developer Guide and UML diagrams of Team TutorLedger (CS2113-W12-2) ([PR #5 Review](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/5/changes#diff-1a95edf069a4136e9cb71bee758b0dc86996f6051f0d438ec2c424557de7160b)), advising on standard PlantUML stereotypes, class compartment notation, instance lifeline headers, dependency arrows, and activity diagram decision nodes.

### Contributions beyond the project team

- **Cross-Team Bug Reporting (V2.0 Jar Dry Run)**:
  - **Team CakeQueue (CS2113-W12-1)** ([Issue #39](https://github.com/AY2627S1-CS2113-W12-1/tp/issues/39)):
    - *Silent Data Loss on Corrupted Save File*: Discovered that when the storage data file contains a malformed entry, any subsequent mutating command (`add`, `edit`, `delete`) silently purges the corrupted row from disk without alerting the user or backing up corrupted records. Recommended implementing defensive loading with line-numbered warnings or backup isolation.
    - *Parser Breakdown on Words Ending with `/`*: Discovered that argument values containing trailing slashes (e.g., `s/o` in names like `John s/o Doe`) cause the command parser to misinterpret the token as a delimiter prefix or fail validation, preventing legitimate inputs containing slashes from being accepted. Advised requiring whitespace boundaries in prefix regex.
  - **Team Business Management System (CS2113-T11-1)** ([Issue #116](https://github.com/AY2627S1-CS2113-T11-1/tp/issues/116)):
    - *Application Crash on Scientific-Notation Prices*: Identified that passing price values in scientific notation (e.g., `product add p/Cola a/1e-3` or `a/1e999999999`) throws an unhandled `ArithmeticException: Rounding necessary` during `BigDecimal` parsing. Because the main loop only catches custom exceptions, the entire application crashes abruptly, causing total loss of unsaved session data.
    - *Integer Overflow on Inventory Quantities*: Discovered that incrementing stock past `Integer.MAX_VALUE` (2,147,483,647) silently overflows to negative quantities (`-2147483648`), violating non-negative stock invariants and causing subsequent commands to display illogical errors (e.g., `Only -2147483648 of Bread in stock`). Suggested using `Math.addExact()` or upper-bound guards.
