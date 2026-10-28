# Peng Bohao — Project Portfolio Page

## Overview

JobSeek is a command-line application that helps students and job seekers track internship and job applications. Users can record application details, monitor deadlines, track recruitment stages, and manage their application progress.

## Summary of contributions

### Code contributed

- [View my merged pull requests](https://github.com/AY2627S1-CS2113-T10-2/tp/pulls?q=is%3Apr+is%3Amerged+author%3Abohao12).

### Enhancements implemented

**Viewing application details (`ViewCommand`)**
- Implemented `ViewCommand` to display an individual application's details in a clean, visual card format bordered by dividers.
- The card displays the company name, role title, recruitment status (`Pending`, `Accepted`, `Rejected`), and completion status (`Yes` or `No`).
- Implemented robust index parsing and boundary validation (`ViewCommand.parse()` and `execute()`) with helpful error messages.
- Added comprehensive unit tests covering parsing (overflow, zero, negatives, non-numeric, padding) and execution (valid, empty list, negative, out-of-bounds).

**Filtered application listing (`ListCommand`)**
- Implemented completion status filtering with `FilterType` (`ACTIVE`, `COMPLETED`, `ALL`).
- Added support for both command-line arguments (`list`, `list completed`, `list all`) and menu shortcuts (menu `[2]` and menu `[3]`).
- Ensured table formatting preserves each application's original 1-based index from the main list so subsequent commands (`view`, `mark`, `edit`, `delete`) remain consistent and unambiguous.
- Added empty-state handling for filtered queries (`No active applications found.`, `No completed applications found.`).
- Added unit tests covering all filter types, empty lists, and invalid filter arguments.

**Marking and unmarking application completion (`MarkCommand`)**
- Implemented `MarkCommand` to toggle application completion state between active (`[ ]`) and completed (`[X]`).
- Integrated with menu option `[5]` (`Mark as completed`) and menu option `[6]` (`Unmark`).
- Added list validation, bounds checking, and immediate file persistence via `Storage.save(applications)`.
- Added unit tests covering successful marking, unmarking, invalid indices, and empty lists.

### Contributions to the User Guide

- Documented `list [completed|all]` with format, menu shortcuts, examples, table output, and filter notes.
- Documented `view <application_number>` with format, card output, and index validation notes.
- Documented `mark` (menu `[5]`) and `unmark` (menu `[6]`) with step-by-step instructions and example outputs.
- Added a comprehensive Command Summary table at the end of the guide.

### Contributions to the Developer Guide

- Updated the Architecture section's class diagram to incorporate `ViewCommand`, `MarkCommand`, and related dependencies.
- Documented the design and implementation of the View Application feature with a Mermaid sequence diagram and design considerations.
- Documented the Filtered Listing feature with a Mermaid sequence diagram and design considerations.
- Documented the Mark and Unmark Completion feature with a Mermaid sequence diagram and design considerations.
- Added user stories for View, Mark/Unmark, and List filtering.
- Added step-by-step instructions for manual testing of viewing, filtering, and marking/unmarking applications.
