# Peng Bohao - Project Portfolio Page

## Overview

JobSeek is a command-line desktop application (CLI) designed for undergraduate students and job seekers to track and manage their internship and graduate-role applications. Users record each application's company, role, recruitment stage, and deadline, see at a glance what needs attention, and find applications quickly as the list grows.

I contributed across the full product lifecycle: bootstrapped the initial code skeleton and base OOP architecture that the team built upon, implemented the one-line typed command parser, designed the multi-criteria `find` filter and undoable command history architecture, resolved critical storage persistence bugs with corrupted-data recovery, developed application view/list/mark commands, and conducted a comprehensive audit of the User Guide and Developer Guide.

## Summary of Contributions

### Code contributed

[RepoSense Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=bohao12&breakdown=true)

### Enhancements implemented

**Base skeleton and foundational architecture** ([#13](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/13), [#14](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/14))
- *What it does*: Bootstrapped the initial JobSeek codebase from scratch, transitioning away from the generic Duke starter template. Implemented the core domain models (`Application`, `ApplicationList`), command pattern hierarchy (`Command`), console interface (`Ui`), and persistence layer (`Storage`), and configured `build.gradle` for Java 25.
- *Justification*: Established the foundational OOP architecture and package structure (`jobseek.model`, `jobseek.command`, `jobseek.storage`, `jobseek.ui`) early in the project, enabling all four team members to build their respective features concurrently without merge bottlenecks or structural conflicts.
- *Highlights*: Designed clean separation of concerns decoupling business logic from I/O and storage, encapsulated list operations and index bounds checks inside `ApplicationList`, and purged template legacy files.

**One-line typed command parser** ([#50](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/50))
- *What it does*: Implemented `CommandParser` enabling users to type full commands on a single line (`add c/Google r/SWE Intern d/2026-11-30`, `edit 2 s/oa`, `delete 3`, `mark 1`, `find ...`) alongside the menu fallback.
- *Justification*: Eliminates tedious multi-step prompts for power users while preserving backwards compatibility.
- *Highlights*: Utilized regex prefix tokenization (`c/`, `r/`, `d/`, `s/`, etc.) that identifies prefixes only when preceded by whitespace, preventing false-positive splits on names containing slashes (e.g. `AT&T/Intel`, `C/C++`). Rejects unknown flags, duplicated fields, empty values, invalid dates, and out-of-range indexes with instructive error messages without corrupting application state. Kept parsing decoupled from `Main` and console I/O (backed by 24 unit tests).

**Multi-criteria filter engine: `find`** ([#51](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/51))
- *What it does*: `find c/google s/interview a/2026-11-01 b/2026-11-30 m/no` filters applications matching company, role, stage, deadline range (`a/` after, `b/` before), and completion status simultaneously.
- *Justification*: Empowers students tracking dozens of applications to pinpoint specific subsets instantly.
- *Highlights*: Applied the Strategy pattern via `ApplicationFilters` producing composable `Predicate<Application>` lambdas joined via logical `AND`. Adding future filter criteria requires only a new predicate factory method and prefix mapping without modifying `FindCommand` execution (Open-Closed Principle). Filtered results retain their master list 1-based indexes for direct use with `edit`, `delete`, or `view`.

**Delete confirmation and reversible undo architecture** ([#52](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/52))
- *What it does*: Added an interactive deletion confirmation prompt and built the `undo` command to iteratively revert mutating actions (`add`, `delete`, `edit`, `status`, `mark`, `unmark`).
- *Justification*: Protects against accidental data loss and provides a safety net when users make typos or unintended operations.
- *Highlights*: Architected an abstract `UndoableCommand` layer and a stack-based `CommandHistory`. Each command encapsulates the minimal state delta needed to invert itself (e.g., deleted applications are restored with exact fields to their original list position). Cancelled or invalid commands are never recorded, preventing history corruption. Re-engineered 5 existing commands; backed by 18 unit tests.

**Today dashboard and storage reliability fixes** ([#45](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/45))
- *What it does*: Built `today` to display overdue, due-today, and 7-day upcoming deadlines alongside recruitment pipeline totals at any time.
- *Highlights*: Resolved two silent data-loss bugs where deadlines entered during addition were discarded and recruitment stages were not saved across restarts. Added backwards compatibility to load older 4-column files while saving updated 5-column records. Designed with clock injection for deterministic date-boundary testing.

**Corrupted-file recovery & defensive storage parsing** ([#47](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/47))
- *What it does*: Hardened `Storage` loading so that invalid data lines (bad format, illegal date, missing fields, unknown stage) are safely skipped with an informative warning and line number, loading all valid records instead of crashing or discarding the entire save file.

**Earlier features and UI improvements**
- `view INDEX` details card with input validation ([#32](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/32), tests in [#36](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/36)).
- `list [completed|all]` filtering maintaining original index numbering ([#34](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/34)).
- `mark` / `unmark` application completion status ([#15](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/15)).
- Fixed UI defect where the title logo and full application list repeatedly reprinted after every command ([#35](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/35)).

### Contributions to the User Guide

- **Authored feature sections**: One-line typed command syntax and prefix rules, Finding with combined filters, Undoing changes and delete confirmation, Today dashboard, Listing applications, Viewing application details, Marking/unmarking applications, and the Command Summary quick reference table.
- **Updated sections**: Adding applications (deadline support), Deleting applications (confirmation prompt), Data persistence (status saving and corrupted-line isolation).
- **Full documentation audit** ([#53](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/53)): Audited the entire User Guide against code behavior, correcting outdated output snippets and syntax discrepancies.

### Contributions to the Developer Guide

- **Authored design sections**: Typed command parser, Multi-criteria filter engine, Delete confirmation and undo architecture, Today dashboard, View command, Filtered listing, and Mark/unmark features, each with design considerations and alternatives.
- **UML diagrams added**: Architecture overview, Domain model and command hierarchy class diagrams, Undo architecture class diagram, and Sequence diagrams for Typed command parsing, Multi-criteria `find`, Delete-with-undo, `ViewCommand`, `ListCommand`, and `MarkCommand`.
- **UML diagrams updated**: Add sequence diagram (deadline handling), Edit sequence diagram (`StatusCommand` integration), and Storage loading sequence diagram (corrupted line isolation).
- **Documentation restructuring & sections filled**: Repaired corrupted sections from earlier merges (undo text misplaced in `find`), and authored product scope, non-functional requirements (NFRs), glossary, user stories, and manual testing procedures ([#53](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/53)).

### Contributions to team-based tasks

- Set up the initial code skeleton (`Application`, `Command`, `Ui`, `Storage`, `ApplicationList`, `Main`) and configured `build.gradle` for Java 25 ([#13](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/13), [#14](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/14)).
- Updated project branding across README and configured team member portfolio links in `AboutUs.md`.
- Opened, tracked, and resolved GitHub issues for features and bug fixes ([#8](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/8), [#25](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/25), [#27](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/27), [#33](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/33), [#46](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/46)).
- Reviewed all documentation written by teammates against actual code implementation (acknowledgements, edit flows, storage format).

### Review/mentoring contributions

- Reviewed and merged teammates' pull requests, including [PR #1](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/1) (Delete application feature).
- Assisted teammates in resolving git merge conflicts across feature branches and aligning command-model interfaces.
- Reviewed and audited team PRs for code consistency, Checkstyle compliance, and test coverage.

### Contributions beyond the project team

- **Cross-Team Developer Guide & UML Review**:
  - Provided extensive, detailed review feedback on the Developer Guide and UML diagrams of Team TutorLedger (CS2113-W12-2) on the central cohort repository ([PR #5 Review](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/5/changes#diff-1a95edf069a4136e9cb71bee758b0dc86996f6051f0d438ec2c424557de7160b)).
  - Contributed 5 detailed review comments on standard UML notation and diagram readability:
    1. **PlantUML Notation Conformance** (DG Line 108): Advised hiding PlantUML circle stereotypes `(C)` in Figures 2, 4, 15, and 19 to adhere strictly to textbook UML class diagram standards.
    2. **Class Box Compartments & Note Connectors** (DG Line 283): Advised displaying all three standard compartments in class boxes and replacing speech-bubble pointers with standard dashed connector lines according to CS2113 reference standards.
    3. **Lifeline Header Notation** (DG Line 645): Corrected sequence diagram lifeline syntax in Figures 3 and 20, clarifying instance `:TutorLedger` lifelines versus static class utility calls (`<<class>> TutorLedger`).
    4. **Dependency Arrow Simplification** (DG Line 109): Recommended consolidating multiple curved dashed arrows between `Storage` and `TutorLedgerData` in Figure 2 into a single straight dependency relationship to enhance visual clarity.
    5. **Activity Diagram Decision Nodes** (DG Line 249): Identified non-standard hexagon condition shapes in Figure 6, recommending standard diamond decision/merge nodes with bracketed guard expressions (`[...]`) along output branches.
