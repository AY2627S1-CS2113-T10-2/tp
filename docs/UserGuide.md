# JobSeek User Guide

JobSeek is a command-line tracker for job and internship applications. It tracks each application's company, role, completion status, and recruitment progress.

## Quick start

1. Install Java 25 or later.
2. Download the latest `jobseek.jar` and place it in a folder where you have write permission.
3. Open a terminal in that folder and run `java -jar jobseek.jar`.
4. Enter a number from the menu and follow the prompts. You can also type the `list` and `view` commands described below. Enter `0` to exit.

**Note:** Double-clicking the JAR may not open a usable terminal window. JobSeek needs write permission in the launch folder to create and update its data file.

## Menu options

| Number | Action | What it does |
| --- | --- | --- |
| `1` | Add application | Enter company, role, and optional deadline, portal link, email, and notes. Company and role must be non-empty. |
| `2` | List applications | Show active applications with their original numbers and status. |
| `3` | List uncompleted applications | Show active applications with their original numbers. |
| `4` | Delete application | Enter an application number to remove it. |
| `5` | Mark application completed | Enter an application number to mark it complete. |
| `6` | Unmark application | Enter an application number to mark it incomplete. |
| `7` | Help | Show the menu options and command shortcuts. |
| `8` | Search applications | Find applications by company or role. |
| `9` | Sort applications | View applications alphabetically by company, then role. |
| `10` | Edit application | Update company, role, recruitment stage, deadline, portal link, email, notes, or all fields of an application. |
| `0` | Exit | Quit JobSeek. |

---

### Adding an application

Choose `1` to create and track a new job or internship application.

- **Prompt sequence**:
  1. `Company:\n> ` — Enter the name of the company or organization (e.g., `Google`, `Shopee`, `GovTech`).
  2. `Role:\n> ` — Enter the title of the role or internship (e.g., `SWE Intern`, `Backend Engineer`, `Data Analyst`).
  3. `Deadline (YYYY-MM-DD, leave blank if none):\n> ` — Enter a deadline such as `2026-11-03`, or press Enter if no deadline is known. Invalid dates display an error and you will be prompted again.
  4. `Company Portal Link (leave blank if none):\n> ` — Enter the web link to the company's application portal (e.g., `https://careers.google.com`), or press Enter to skip.
  5. `Contact Email (leave blank if none):\n> ` — Enter the recruiter or contact email (e.g., `recruiter@google.com`), or press Enter to skip.
  6. `Notes (leave blank if none):\n> ` — Enter freeform notes or interview details (e.g., `Referred by Alice; OA completed`), or press Enter to skip.
- **Input validation & Optional fields**:
  - Both company and role are **required** and cannot be empty or whitespace-only. If an empty input is entered, JobSeek displays an error message (e.g., `Error: Company cannot be empty.`) and reprompts until valid text is received.
  - The company portal link, recruiter contact email, notes, and deadline are **optional**:
    - Simply press Enter without typing any text to skip any of them.
    - If skipped, these fields remain blank and are excluded from the confirmation message and detail cards.
  - Leading and trailing whitespace is automatically trimmed.
- **Initial attributes**:
  - The new application is assigned the next 1-based number in the list.
  - **Completion status**: Defaults to incomplete (`[ ]`).
  - **Recruitment stage**: Automatically initialized to `Applied`.
- **Confirmation message**:
  When optional fields are omitted (leaving them blank):
  ```text
  Application added successfully.
  Company: Google
  Role: Software Engineering Intern
  ```
  When optional fields are provided:
  ```text
  Application added successfully.
  Company: Google
  Role: Software Engineering Intern
  Deadline: 2026-11-03
  Portal Link: https://careers.google.com
  Email: recruiter@google.com
  Notes: Referred by Alice; OA completed
  ```

---

### Listing applications: `list`

Shows a table of tracked applications with their original list numbers, completion status (`[X]` or `[ ]`), company name, role title, and deadline. By default, only active (uncompleted) applications are displayed.

Format: `list [completed|all]`

Menu shortcuts:
* Choose `2` to list active applications.
* Choose `3` to list uncompleted applications.

Examples:
* `list` – Shows active (uncompleted) applications.
* `list completed` – Shows only completed applications.
* `list all` – Shows all applications regardless of completion status.

Example output:
```text
Here are your applications:
No. | Status | Company   | Role           | Deadline  
----+--------+-----------+----------------+-----------
1   | [ ]    | Google    | SWE Intern     | 2026-11-03
3   | [ ]    | Shopee    | Backend Intern | -         
```

> **Notes on list filtering:**
> * The `No.` column always displays each application's original 1-based index from the main list. You can safely use this index in commands such as `view`, `edit`, `delete`, or `mark`.
> * If no applications match the chosen filter, JobSeek displays an informative message:
>   * `No active applications found.`
>   * `No completed applications found.`
>   * `No applications found.`

---

### Viewing application details: `view`

Displays a comprehensive, structured information card for a single job application at the specified index. The card presents all recorded details including company, role, recruitment status, completion state, deadline, portal link, contact email, and notes.

Format: `view <application_number>`

Examples:
* `view 1` – Displays the detailed card for application number 1.
* `view 3` – Displays the detailed card for application number 3.

Example output:
```text
==================================================
Application Details [Index: 1]
--------------------------------------------------
Company:   Google
Role:      SWE Intern
Status:    Accepted
Completed: No
Deadline:  2026-11-03
Portal:    https://careers.google.com
Email:     recruiter@google.com
Notes:     Passed round 1 OA
==================================================
```

> **Notes on viewing applications:**
> * `<application_number>` must be a valid positive integer corresponding to an existing entry in your application list (e.g., `1`, `2`).
> * Optional fields (`Deadline:`, `Portal:`, `Email:`, `Notes:`) only appear if recorded.
> * If `<application_number>` is missing, non-numeric, or `<= 0`, JobSeek notifies you:
>   `Error: Please provide a valid positive application index. Usage: view INDEX`
> * If `<application_number>` is greater than the total number of applications, JobSeek displays:
>   `Error: Application index X does not exist. (Total applications: Y)`

---

### Editing an application

Choose `10` to update details of an existing application in place without deleting and recreating it.

- **Prompt sequence**:
  1. `Enter application index to edit:\n> ` — Enter the 1-based index of the application to update (as displayed in `list`).
  2. `Enter field to edit (1: Company, 2: Role, 3: Recruitment stage, 4: Deadline, 5: Portal Link, 6: Email, 7: Notes, 8: All):\n> ` — Select which field(s) you wish to modify:
     - `1`: Edit Company name only.
     - `2`: Edit Role title only.
     - `3`: Edit Recruitment stage only (`Applied`, `Online Assessment`, `Interview`, `Offer`, `Accepted`, `Rejected`, or `Withdrawn`).
     - `4`: Edit Deadline only (enter date `YYYY-MM-DD` or leave blank to clear).
     - `5`: Edit Company Portal Link only (enter URL or leave blank to clear).
     - `6`: Edit Contact Email only (enter email or leave blank to clear).
     - `7`: Edit Notes only (enter text or leave blank to clear).
     - `8`: Edit All fields in sequence (Company, Role, Recruitment Stage, Deadline, Portal Link, Email, Notes).
  3. JobSeek will then prompt for the respective replacement value(s):
     - `Enter new company:\n> ` (if editing company or all)
     - `Enter new role:\n> ` (if editing role or all)
     - `Enter recruitment stage (1: Applied, 2: Online Assessment, 3: Interview, 4: Offer, 5: Accepted, 6: Rejected, 7: Withdrawn):\n> ` (if editing stage or all)
     - `Deadline (YYYY-MM-DD, leave blank if none):\n> ` (if editing deadline or all)
     - `Enter new company portal link (leave blank to clear):\n> ` (if editing portal link; or `Company Portal Link (leave blank to clear):` in all)
     - `Enter new contact email (leave blank to clear):\n> ` (if editing email; or `Contact Email (leave blank to clear):` in all)
     - `Enter new notes (leave blank to clear):\n> ` (if editing notes; or `Notes (leave blank to clear):` in all)
- **Input validation & error handling**:
  - If your application list is currently empty, JobSeek notifies you with `Error: Your application list is currently empty.` and returns to the menu.
  - Blank index input is rejected with `Error: Please specify a valid application index.`.
  - Non-numeric index input is rejected with `Error: Invalid application index. Please enter a valid number.`.
  - Out-of-bounds indices are rejected (`Error: Invalid application index: X. Please enter a number between 1 and Y.`).
  - Empty company or role values are rejected and reprompted.
  - Invalid stage choices are rejected and reprompted (`Error: Invalid recruitment stage: '...'. Enter a number from 1 to 7 or a known stage name.`).
  - Entering an invalid field choice (anything other than 1 through 8) displays `Error: Invalid field choice: '<choice>'. Enter a number from 1 to 8.` and returns to the menu.
  - Unedited fields preserve their current values and completion flags (`[ ]` or `[X]`).
- **Confirmation message**:
  For field choices updating company, role, or where optional fields are cleared / omitted:
  ```text
  Application updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Status: Applied
  ```
  When optional fields (deadline, portal link, email, notes) are set or updated:
  ```text
  Application updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Status: Pending
  Deadline: 2026-11-20
  Portal Link: https://careers.google.com
  Email: recruiter@google.com
  Notes: Technical interview round completed
  ```
  For field choice `3` (Status only):
  ```text
  Application status updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Status: Accepted
  ```

---

### Recruitment stage tracking

Each application has one of these stages: `Applied`, `Online Assessment`, `Interview`, `Offer`, `Accepted`, `Rejected`, or `Withdrawn`. `Accepted`, `Rejected`, and `Withdrawn` are terminal stages. You can still edit a stage at any time to correct a mistake or reflect a company process; stages do not enforce a fixed transition order.

#### Updating a stage
Under menu option `10`, select field `3` (Recruitment stage) or `8` (All). Enter one of the numbered choices shown by the prompt (`1` for Applied, `2` for Online Assessment, `3` for Interview, `4` for Offer, `5` for Accepted, `6` for Rejected, `7` for Withdrawn), or type a stage name such as `Interview`. The shortcut `OA` is also accepted for `Online Assessment`. Inputs are case-insensitive. Any unrecognized input triggers `Error: Invalid recruitment stage: '<input>'. Enter a number from 1 to 7 or a known stage name.`

#### Inspecting an application stage
Use the `view INDEX` command to view the comprehensive card for any application, including its recruitment stage:
```text
view 1
==================================================
Application Details [Index: 1]
--------------------------------------------------
Company:   Google
Role:      SWE Intern
Status:    Accepted
Completed: No
==================================================
```

---

### Search and sort applications

#### Search applications
Choose `8`, then enter part of a company name or role. Search ignores letter case. For example, `eng` matches both `Engineer` in a role and `Engineering Co` in a company name. An empty search term is rejected and you will be prompted again. If nothing matches, JobSeek tells you so.

Search results show the same application numbers as the normal list. For example, if the matching record is application `3`, you can still enter `3` when marking or deleting it. Search does not change or save application order.

#### Sort applications alphabetically
Choose `9` to view applications ordered by company name, then by role when company names match. Sorting ignores letter case. The number beside each application is its original number in the normal list. This is a display-only view: it does not rearrange the saved applications. If you later type `list all`, the original order is shown again.

---

### Deleting an application

Choose `4` to permanently remove an application.

1. Enter `list all` to find the number of the application you want to delete.
2. Enter `4` at the main menu.
3. Enter the application number when prompted.

For example, entering `2` at the deletion prompt removes application number `2`.

There is no additional confirmation prompt or undo feature. Application numbers after the deleted entry shift down by one, so check the list again before deleting another application.

If the list is empty or the application number is invalid, JobSeek displays an error without deleting anything.

---

### Marking an application as completed: `mark`

Marks the application at the specified index as completed (`[X]`). Completed applications represent concluded application pipelines (such as accepted offers or finalized cycles) and are hidden from the default active list view to keep your workspace clutter-free.

Format: Choose `5` from the menu, then enter `<application_number>`.

Examples:
* Choose `5`, then enter `1` – Marks application number 1 as completed.

Example output:
```text
Nice! I've marked this application as completed:
  [X] Google | Role: SWE Intern
```

> **Notes on marking applications:**
> * If your application list is currently empty, JobSeek notifies you:
>   `Error: Your application list is currently empty.`
> * If the entered index is blank, non-numeric, or out of range, JobSeek rejects it with an error:
>   * `Error: Please specify a valid application index.`
>   * `Error: Invalid application index. Please enter a valid number.`

---

### Unmarking an application: `unmark`

Reverts the completion status of the application at the specified index back to incomplete (`[ ]`). The application is reactivated and will appear in active application listings.

Format: Choose `6` from the menu, then enter `<application_number>`.

Examples:
* Choose `6`, then enter `1` – Reverts application number 1 back to incomplete.

Example output:
```text
OK, I've marked this application as not completed yet:
  [ ] Google | Role: SWE Intern
```

> **Notes on unmarking applications:**
> * If your application list is currently empty, JobSeek notifies you:
>   `Error: Your application list is currently empty.`
> * If the entered index is blank, non-numeric, or out of range, JobSeek rejects it with an error:
>   * `Error: Please specify a valid application index.`
>   * `Error: Invalid application index. Please enter a valid number.`

---

### Deadline warnings

JobSeek automatically checks deadlines when it starts and displays warnings before the main menu.

| Warning | Meaning |
| --- | --- |
| `OVERDUE` | The deadline is earlier than today. |
| `DUE TODAY` | The deadline is today. |
| `UPCOMING` | The deadline is within the next seven days, including exactly seven days away. |

Only applications that have not been marked as completed are included. Applications without deadlines do not produce warnings.

For example, if today is `2026-10-28`, the following warnings may appear:

```text
Deadline warnings:
OVERDUE: Google - SWE Intern (deadline 2026-10-27)
DUE TODAY: Shopee - Backend Intern (deadline 2026-10-28)
UPCOMING: GovTech - Data Analyst (deadline 2026-11-03)
```

If no applications require attention, no warning summary appears. Warnings are checked once at startup and do not automatically refresh while JobSeek remains open.

---

### Today dashboard: `today`

Shows what needs your attention right now, without restarting JobSeek.

- **Usage**: Enter `today` at the main menu prompt.
- **Sections**: `OVERDUE`, `DUE TODAY` and `UPCOMING` (deadlines within the next seven days), followed by a `SUMMARY` of active and completed applications and a count of active applications per status.
- Only applications that are not marked as completed appear in the deadline sections. Each entry keeps the number you use with `view`, `edit` and `delete`.

```text
Today: 2026-10-28

OVERDUE (1)
  1. Google | SWE Intern - 2026-10-27 (1 day overdue)

DUE TODAY (0)
  none

UPCOMING (next 7 days) (1)
  3. GovTech | Analyst - 2026-11-04 (in 7 days)

SUMMARY
  3 active, 1 completed, 1 active without a deadline
  Active by recruitment stage - Accepted: 1, Applied: 2
```

---

## Data

JobSeek automatically saves application data to `data/applications.txt` when commands modify application records. Saved records are loaded when you next start JobSeek.

- Company, role, completion status, optional deadline and recruitment stage are preserved between sessions.
- Data files saved by older versions still load. Missing stages and legacy `Pending` values become `Applied`.
- If the data file does not exist, JobSeek starts with an empty application list.
- **Corrupted-line recovery**: If any line in the data file is corrupted (e.g., malformed format, invalid boolean flag, invalid deadline, or empty required field), JobSeek displays an informative warning, skips the corrupted record, and successfully loads all remaining valid records.
- If loading completely fails due to an unrecoverable system I/O error, JobSeek displays an error and starts with an empty list.
- Search, sort, list and help only display information and do not modify saved data.

The data file location is relative to the folder from which you launch JobSeek. Launch the application from the same folder each time to access the same saved records.

---

## Help instructions

Choose `7` to view the comprehensive reference of all menu options and textual command shortcuts.

- **Usage**: Enter `7` at the main menu prompt.
- **Output**:
  ```text
  Available menu options:
    list [completed|all]            - List active, completed, or all applications
    today                           - Show overdue, due-today and upcoming deadlines
    view INDEX                      - View details for application at INDEX
    [1] Add application             - Add a new internship or job application
    [2] List applications           - View active applications in a numbered table
    [3] List uncompleted apps       - View active / uncompleted applications
    [4] Delete application          - Delete an application by index
    [5] Mark application completed  - Mark an application as completed
    [6] Unmark application          - Mark an application as not completed
    [7] Help                        - Show this help message
    [8] Search applications         - Find applications by company or role
    [9] Sort applications           - View applications alphabetically
    [10] Edit application           - Edit an existing application's details
    [0] Exit                        - Exit JobSeek
  ```

---

## Command Summary

| Action | Format | Examples / Usage |
|---|---|---|
| **Add application** | `1` | Choose `1`, then enter company, role, optional deadline, portal link, email, notes |
| **List active applications** | `list` or `2` or `3` | `list` or choose `2` or `3` |
| **List completed applications** | `list completed` | `list completed` |
| **List all applications** | `list all` | `list all` |
| **View application details** | `view <application_number>` | `view 1` |
| **Delete application** | `4` | Choose `4`, then enter application index (e.g. `2`) |
| **Mark application completed** | `5` | Choose `5`, then enter application index (e.g. `1`) |
| **Unmark application** | `6` | Choose `6`, then enter application index (e.g. `1`) |
| **Today dashboard** | `today` | `today` |
| **Help instructions** | `7` | `7` |
| **Search applications** | `8` | Choose `8`, then enter search term (e.g. `Google`) |
| **Sort applications** | `9` | Choose `9` to view applications alphabetically |
| **Edit application** | `10` | Choose `10`, enter index, and select field 1-8 to modify |
| **Exit JobSeek** | `0` | `0` |
