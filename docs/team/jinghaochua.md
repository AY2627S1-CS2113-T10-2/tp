# Chua Jinghao — Project Portfolio Page

## Overview

JobSeek is a command-line application that helps students and job seekers track internship and job applications. Users can record application details, monitor deadlines and manage their application progress.

## Summary of contributions

### Code contributed

- [View my merged pull requests](https://github.com/AY2627S1-CS2113-T10-2/tp/pulls?q=is%3Apr+is%3Amerged+author%3Ajinghaochua).

### Enhancements implemented

**Deleting applications** — [PR #16](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/16)

- Implemented `DeleteCommand` to remove an application using its application number and save the updated list.
- Added checks for empty lists and invalid indices to prevent unintended deletion.
- Added automated tests covering successful deletion, invalid indices and empty lists.
- This feature allows users to remove records they no longer need.

**Startup deadline warnings and local storage** — [PR #24](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/24)

- Added optional application deadlines using `LocalDate`, with validation of user-entered dates.
- Implemented startup warnings for overdue applications, applications due today and applications due within seven days.
- Excluded completed applications and applications without deadlines from the warning summary.
- Implemented saving and loading of company, role, completion status and optional deadline in a local UTF-8 text file.
- Added escaping and decoding of special characters so that text fields can be stored without interfering with record separators.
- Added validation of stored records and tests covering persistence, optional deadlines, headers and malformed data.
- These enhancements help users identify approaching deadlines and retain application records between sessions.

### Contributions to the User Guide

- Documented optional deadline input and startup deadline warnings.
- Explained application deletion and its effect on application numbering.
- Updated the Data section to describe persistence and its limitations.
- Improved the terminal launch instructions in Quick start.

### Contributions to the Developer Guide

- Documented the implementation and design considerations of deadline warnings.
- Added class and sequence diagrams for deadline warnings.
- Documented the storage format, saving/loading workflows and error handling.
- Added a sequence diagram illustrating the loading of saved applications.