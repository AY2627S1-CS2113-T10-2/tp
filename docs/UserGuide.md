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
| `1` | Add application | Enter a company, role and optional deadline. Company and role must be non-empty. |
| `2` | List applications | Show active applications with their original numbers and status. |
| `3` | List uncompleted applications | Show active applications with their original numbers. |
| `4` | Delete application | Enter an application number to remove it. |
| `5` | Mark application completed | Enter an application number to mark it complete. |
| `6` | Unmark application | Enter an application number to mark it incomplete. |
| `7` | Help | Show the menu options and command shortcuts. |
| `8` | Search applications | Find applications by company or role. |
| `9` | Sort applications | View applications alphabetically by company, then role. |
| `10` | Edit application | Update company, role, or recruitment status (Pending, Accepted, Rejected) of an application. |
| `0` | Exit | Quit JobSeek. |

---

### Adding an application

Choose `1` to create and track a new job or internship application.

- **Prompt sequence**:
  1. `Company:\n> ` — Enter the name of the company or organization (e.g., `Google`, `Shopee`, `GovTech`).
  2. `Role:\n> ` — Enter the title of the role or internship (e.g., `SWE Intern`, `Backend Engineer`, `Data Analyst`).
  3. `Deadline (YYYY-MM-DD, leave blank if none):` — Enter a deadline such as `2026-11-03`, or press Enter if no deadline is known. Invalid dates display an error and you will be prompted again.
- **Input validation**:
  - Both company and role are required fields and cannot be empty or whitespace-only.
  - If an empty input is submitted, JobSeek displays an error message (e.g., `Error: Company cannot be empty.`) and reprompts until a valid input is received.
  - Leading and trailing whitespace is automatically trimmed.
- **Initial attributes**:
  - The new application is assigned the next 1-based number in the list.
  - **Completion status**: Defaults to incomplete (`[ ]`).
  - **Recruitment status**: Automatically initialized to `"Pending"`.
- **Confirmation message**:
  ```text
  Application added successfully.
  Company: Google
  Role: Software Engineering Intern
  ```

---

### Listing applications

JobSeek provides flexible options to display and inspect your tracked applications:

- **Menu options**:
  - **`[2] List applications`**: Shows active applications in a numbered table with their original index and completion status.
  - **`[3] List uncompleted applications`**: Displays active / uncompleted applications.
- **Textual filter commands**:
  - `list`: Displays active applications.
  - `list completed`: Displays only applications that have been marked completed (`[X]`).
  - `list all`: Displays every tracked application regardless of completion status.
- **Detailed card view**:
  - `view INDEX`: Displays the full information card for the application at the specified 1-based index (e.g., `view 1`), including company, role, recruitment status, and completion state.

---

### Editing an application

Choose `10` to update details of an existing application in place without deleting and recreating it.

- **Prompt sequence**:
  1. `Enter application index to edit:\n> ` — Enter the 1-based index of the application to update (as displayed in `list`).
  2. `Enter field to edit (1: Company, 2: Role, 3: Status, 4: All):\n> ` — Select which field(s) you wish to modify:
     - `1`: Edit Company name only.
     - `2`: Edit Role title only.
     - `3`: Edit Recruitment Status only (`Pending`, `Accepted`, or `Rejected`).
     - `4`: Edit All three fields (Company, Role, and Status).
  3. JobSeek will then prompt for the respective replacement value(s):
     - `Enter new company:\n> ` (if editing company or all)
     - `Enter new role:\n> ` (if editing role or all)
     - `Enter recruitment status (1: Pending, 2: Accepted, 3: Rejected):\n> ` (if editing status or all)
- **Input validation & error handling**:
  - If your application list is currently empty, JobSeek notifies you with `Error: Your application list is currently empty.` and returns to the menu.
  - Blank index input is rejected with `Error: Please specify a valid application index.`.
  - Non-numeric index input is rejected with `Error: Invalid application index. Please enter a valid number.`.
  - Out-of-bounds indices are rejected (`Error: Invalid application index: X. Please enter a number between 1 and Y.`).
  - Empty company or role values are rejected and reprompted.
  - Invalid status choices are rejected and reprompted (`Error: Invalid status choice: '...'. Enter 1 (Pending), 2 (Accepted), or 3 (Rejected).`).
  - Entering an invalid field choice (anything other than 1, 2, 3, or 4) displays `Error: Invalid field choice: '<choice>'. Enter 1, 2, 3, or 4.` and returns to the menu.
  - Unedited fields preserve their current values and completion flags (`[ ]` or `[X]`).
- **Confirmation message**:
  For field choices `1`, `2`, or `4` (Company, Role, or All):
  ```text
  Application updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Status: Pending
  ```
  For field choice `3` (Status only):
  ```text
  Application status updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Status: Accepted
  ```

---

### Recruitment status tracking

JobSeek provides first-class recruitment stage indicators for tracking your application progress:
- **`Pending`**: Default initial state. Indicates the application is submitted, pending review, or in interview stages.
- **`Accepted`**: Indicates you have received and accepted an internship or job offer.
- **`Rejected`**: Indicates the application was unsuccessful.

#### Updating Status
Under menu option `10`, selecting field `3` (Status) or `4` (All) allows you to set the status using either convenient numeric shortcuts or case-insensitive text names:
- Numeric inputs: `1` for Pending, `2` for Accepted, `3` for Rejected.
- Text inputs: `pending`, `accepted`, `rejected` (letter case is ignored).
- Any unrecognized input triggers `Error: Invalid status choice: '...'. Enter 1 (Pending), 2 (Accepted), or 3 (Rejected).`

#### Inspecting Application Status
Use the `view INDEX` command to view the comprehensive card for any application, including its recruitment status:
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

## Data

JobSeek automatically saves application data to `data/applications.txt` when commands modify application records. Saved records are loaded when you next start JobSeek.

- Company, role, completion status and optional deadline are preserved between sessions.
- Recruitment status (`Pending`, `Accepted` or `Rejected`) is currently not saved and resets to `Pending` when records are loaded.
- If the data file does not exist, JobSeek starts with an empty application list.
- If loading fails, JobSeek displays an error and starts with an empty list. Back up the existing data file before making changes, as subsequent saves may overwrite it.
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
