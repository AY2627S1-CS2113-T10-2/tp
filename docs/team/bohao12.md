# Peng Bohao - Project Portfolio Page

## Overview

JobSeek is a command-line application that helps university students keep track of their internship and graduate-role applications. Users record each application's company, role, recruitment stage and deadline, see at a glance what needs attention, and find applications quickly as the list grows.

I worked across the whole product: the initial code skeleton, most of the commands that change or display data, the one-line command parser, persistence fixes, and a full audit of the User Guide and Developer Guide.

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=bohao12&breakdown=true)

### Enhancements implemented

**Typed command parser** ([#50](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/50))
- Added `CommandParser`, so users can type a whole command on one line (`add c/Google r/SWE Intern d/2026-11-30`, `edit 2 s/oa`, `delete 3`) instead of answering several prompts. The numbered menu still works as a fallback.
- Fields use prefixes (`c/`, `r/`, `d/`, `s/`) and can be given in any order, with spaces inside values. A regular expression only treats a letter and slash *after whitespace* as a prefix, so names such as `AT&T/Intel` are read correctly.
- Rejects unknown, repeated and empty fields, invalid dates and unknown stages with a message that shows the correct usage, so a bad command never changes data.
- Kept parsing out of `Main` so it can be unit-tested without simulating console input (24 new tests).

**Combined filters: `find`** ([#51](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/51))
- `find c/google s/interview a/2026-11-01 b/2026-11-30 m/no` shows applications that match *every* condition (company, role, stage, deadline range, completion).
- Designed with the strategy pattern: `ApplicationFilters` returns one `Predicate<Application>` per condition, and the parser joins them with `and`. Adding a new filter only needs one factory method and one prefix; `FindCommand` itself does not change.
- Results keep the original list numbers, so they can be used directly with `edit` and `delete`.

**Delete confirmation and undo** ([#52](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/52))
- `delete` now asks for confirmation. A new `undo` command reverses the last add, delete, edit, stage change or mark/unmark, and can be repeated to step further back.
- Introduced an abstract `UndoableCommand` layer in the command hierarchy and a stack-based `CommandHistory`. Each command stores only what it needs to reverse itself (the affected object and its old values), so undo is exact even when two applications look identical. Deleted applications return to their original position.
- Cancelled or invalid commands are never recorded, so `undo` cannot reverse something that did not happen. This touched 5 existing commands; 18 new tests.

**Today dashboard and storage fixes** ([#45](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/45))
- Added `today`, which shows overdue, due-today and next-7-day deadlines plus a pipeline summary at any time, instead of only at startup. The date can be injected so the boundaries are tested deterministically.
- Fixed two data-loss bugs: deadlines entered when adding were silently discarded, and recruitment status was never saved, so it reset after every restart. Added a status column while keeping older four-column data files loadable.

**Corrupted-file recovery** ([#47](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/47))
- Previously one bad line in the data file made the whole file fail to load. Loading now skips only the corrupted record (bad format, flag, date, empty field or unknown stage), reports the line number, and keeps every valid record.

**Earlier features**
- `view INDEX` details card with input validation ([#32](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/32), tests in [#36](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/36)).
- `list [completed|all]` filtering that keeps original numbering ([#34](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/34)).
- `mark` / `unmark` completion ([#15](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/15)).
- Fixed the UI reprinting the title and full list after every command ([#35](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/35)).

### Contributions to the User Guide

- Wrote: Typed commands, Finding with combined filters, Undoing a change, Today dashboard, Listing applications, Viewing application details, Marking/Unmarking, and the Command Summary.
- Updated: Deleting (confirmation), Data (status saving, corrupted-line recovery), Adding (deadline).
- Audited the whole guide against the code ([#53](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/53)) and fixed outdated outputs and missing command forms.

### Contributions to the Developer Guide

- Wrote the sections on the Typed command parser, Combined filters, Delete confirmation and undo, Today dashboard, View, Filtered listing, and Mark/Unmark, each with design considerations and alternatives.
- UML diagrams added: architecture overview, command hierarchy and data-model class diagrams; undo class diagram; sequence diagrams for View, List, Mark, typed parsing, `find`, and delete-then-undo.
- UML diagrams updated: Add sequence diagram (deadline), Edit sequence diagram (stage edits go through `StatusCommand`), Storage loading sequence diagram (corrupted-line recovery).
- Repaired a section that earlier merges had mixed up (undo text inside the `find` section), and filled in product scope, non-functional requirements, glossary, user stories and manual-testing steps for the newer features ([#53](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/53)).

### Contributions to team-based tasks

- Set up the initial code skeleton (`Application`, `Command`, `Ui`, `Storage`) that the team built on, cleaned up the template's legacy `seedu.duke` files, and updated `build.gradle` ([#13](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/13), [#14](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/14)).
- Renamed the product in the README and updated the About Us page.
- Opened and tracked issues for my features and a UI bug ([#8](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/8), [#25](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/25), [#27](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/27), [#33](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/33), [#46](https://github.com/AY2627S1-CS2113-T10-2/tp/issues/46)).
- Checked documentation written by all members against the code, including acknowledgements, the edit flow and storage sections.

### Review/mentoring contributions

- Merged a teammate's PR ([#1](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/1)).
- *To be added: PRs reviewed, with links.*

### Contributions beyond the project team

- *To be added: e.g. bugs reported in other teams' products during the PE dry run, forum posts.*
