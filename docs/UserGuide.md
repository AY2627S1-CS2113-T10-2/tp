# JobSeek User Guide

JobSeek is a command-line tracker for job and internship applications. It tracks each application's company, role, completion status, and recruitment progress.

## Quick start

1. Install Java 25 or later.
2. Download the latest `jobseek.jar` and place it in a folder where you have write permission.
3. Open a terminal in that folder and run `java -jar jobseek.jar`.
4. Enter a number from the menu and follow the prompts, or type a one-line command such as `add c/Google r/SWE Intern` (see [Typed commands](#typed-commands-one-line-shortcuts)). Enter `0` or type `exit` to quit.

**Note:** Double-clicking the JAR may not open a usable terminal window. JobSeek needs write permission in the launch folder to create and update its data file.

## Menu options

At any prompt that follows a numbered menu option, type `cancel` to abandon that action without changing anything.

The home menu shows the options in two columns. The numbers below are the ones to enter; they work the same way as before.

| Number | Action | What it does |
| --- | --- | --- |
| `1` | Add application | Enter company, role, and optional deadline, portal link, email, and notes. Company and role must be non-empty. |
| `2` | List all applications | Show all applications in a numbered table with their original numbers and status. |
| `3` | List uncompleted applications | Show active / uncompleted applications with their original numbers. |
| `4` | Delete application | Enter an application number to remove it. |
| `5` | Mark application as completed | Enter an application number to mark it complete. |
| `6` | Unmark application | Enter an application number to mark it incomplete. |
| `7` | Search applications | Find applications by company or role. |
| `8` | Sort applications alphabetically | View applications alphabetically by company, then role. |
| `9` | Edit application | Update company, role, recruitment stage, deadline, portal link, email, notes, or all fields of an application. |
| `10` | Help | Show the menu options and command shortcuts. |
| `0` | Exit | Quit JobSeek. |

---

### Typed commands (one-line shortcuts)

Instead of answering prompts one by one, you can type a whole command on one line. The numbered menu still works.

| Action | Format | Example |
| --- | --- | --- |
| Add | `add c/COMPANY r/ROLE [d/YYYY-MM-DD] [p/PORTAL] [e/EMAIL] [n/NOTES]` | `add c/Google r/SWE Intern d/2026-11-30` |
| Edit | `edit INDEX [c/COMPANY] [r/ROLE] [s/STAGE] [d/DATE] [p/PORTAL] [e/EMAIL] [n/NOTES]` | `edit 2 r/Backend Intern s/Interview` |
| Delete | `delete INDEX` | `delete 3` |
| Mark / unmark | `mark INDEX`, `unmark INDEX` | `mark 1` |
| Search | `search KEYWORD` | `search google` |
| Find (combined filters) | `find [c/..] [r/..] [s/..] [b/..] [a/..] [m/..]` | `find c/google s/interview` |
| Sort | `sort` | `sort` |
| List | `list [completed\|all]` | `list all` |
| View details | `view INDEX` | `view 2` |
| Today dashboard | `today` | `today` |
| Undo | `undo` | `undo` |
| Help / exit | `help`, `exit` | `exit` |

`sort`, `help`, `today`, `undo`, and `exit` do not take extra words. For example, `sort extra` shows `Error: Unexpected text after 'sort'. Usage: sort` instead of sorting.

- `c/` is company, `r/` is role, `d/` is deadline, `s/` is recruitment stage (e.g. `Interview`, or shortcut `OA`), `p/` is portal link, `e/` is email, and `n/` is notes.
- Field prefixes are case-insensitive (`c/` or `C/`). Fields can be given in any order. In `add`, `c/` and `r/` are required. In `edit`, at least one field is required.
- Command words are not case-sensitive. `INDEX` is the number shown by `list`.
- A field value ends where the next prefix starts. Only the prefix letters above (`c r s d p e n`, plus `b a m` in `find`) count, so text such as `w/ recruiter` or `I/O Team` is safe inside a value. Avoid typing a real prefix such as ` r/` inside a company name or note.
- In `edit`, give `d/`, `p/`, `e/` or `n/` with nothing after it to clear that field. For example, `edit 2 n/` removes the notes of application 2 and `edit 2 d/` removes its deadline.
- If a command is wrong, JobSeek shows what is wrong and the correct format, and nothing is changed.

---

### Finding with combined filters: `find`

Narrows the list using several conditions at once. An application is shown only if it satisfies **every** filter you give.

- **Format**: `find [c/COMPANY] [r/ROLE] [s/STAGE] [b/DATE] [a/DATE] [m/yes|no]` (give at least one filter, in any order)

| Filter | Meaning |
| --- | --- |
| `c/TEXT` | Company contains the text (ignores upper/lower case). |
| `r/TEXT` | Role contains the text (ignores upper/lower case). |
| `s/STAGE` | Recruitment stage equals `STAGE`, such as `Interview` or the shortcut `OA`. |
| `b/YYYY-MM-DD` | Deadline is on or before the date. |
| `a/YYYY-MM-DD` | Deadline is on or after the date. |
| `m/yes` or `m/no` | The application is (or is not) marked as completed. |

Applications without a deadline never match `b/` or `a/`. Use both together to find a date range.

Examples:

- `find c/google s/interview` shows Google applications that are at the interview stage.
- `find r/intern a/2026-11-01 b/2026-11-30` shows internships with a deadline in November 2026.
- `find m/no s/offer` shows offers you have not marked as completed yet.

Results keep the numbers from the full list, so you can use them directly with `view`, `edit` and `delete`. If nothing matches, JobSeek says so and shows the conditions it searched for. `find` only displays results and never changes your data. For a quick single-word lookup, `search KEYWORD` is still available.

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
- **Typed alternative**: `add c/Google r/Software Engineering Intern d/2026-11-30` does the same in one line.

---

### Listing applications: `list`

Shows a table of tracked applications with their original list numbers, completion status (`[X]` or `[ ]`), company name, role title, and deadline. By default, only active (uncompleted) applications are displayed.

Format: `list [completed|all]`

Menu shortcuts:
* Choose `2` to list all applications.
* Choose `3` to list uncompleted (active) applications.

Examples:
* `list` – Shows active (uncompleted) applications.
* `list completed` – Shows only completed applications.
* `list all` – Shows all applications regardless of completion status.

Example output:
```text
Here are your applications:
No. | Completed | Company   | Role           | Deadline
----+-----------+-----------+----------------+-----------
1   | [ ]       | Google    | SWE Intern     | 2026-11-03
3   | [ ]       | Shopee    | Backend Intern | -
```

> **Notes on list filtering:**
> * The `No.` column always displays each application's original 1-based index from the main list. You can safely use this index in commands such as `view`, `edit`, `delete`, or `mark`.
> * If no applications match the chosen filter, JobSeek displays an informative message:
>   * `No active applications found.`
>   * `No completed applications found.`
>   * `No applications found.`

---

### Viewing application details: `view`

Displays a comprehensive, structured information card for a single job application at the specified index. The card presents all recorded details including company, role, recruitment stage, completion state, deadline, portal link, contact email, and notes.

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
Recruitment stage: Accepted
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
>   `Error: Please provide a valid positive application number (e.g. 'view 1').`
> * If `<application_number>` is greater than the total number of applications, JobSeek displays:
>   `Error: Application index X does not exist. (Total applications: Y)`

---

### Editing an application

Choose `9` to update details of an existing application in place without deleting and recreating it.

- **Prompt sequence**:
  1. `Enter application index to edit:\n> ` — Enter the 1-based index of the application to update (as displayed in `list`).
  2. JobSeek displays the edit field selection menu and prompts for your choice:
     ```text
     ____________________________________________________________
     [1] Company               [2] Role
     [3] Recruitment stage     [4] Deadline
     [5] Portal Link           [6] Email
     [7] Notes                 [8] All
     ____________________________________________________________
     Enter field to edit:
     > 
     ```
     Select which field(s) you wish to modify:
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
  - Blank index input is rejected with `Error: Please specify a valid application index.` and reprompted.
  - Non-numeric index input is rejected with `Error: Invalid application index. Please enter a valid number.` and reprompted.
  - Out-of-bounds indices are rejected (`Error: Invalid application index: X. Please enter a number between 1 and Y.`) and reprompted.
  - Empty company or role values are rejected and reprompted.
  - Invalid stage choices are rejected and reprompted (`Error: Invalid recruitment stage: '...'. Enter a stage name or a number from 1 to 7.`).
  - Entering an invalid field choice (anything other than 1 through 8) displays `Error: Invalid field choice: '<choice>'. Enter a number from 1 to 8.` and returns to the menu.
  - Unedited fields preserve their current values and completion flags (`[ ]` or `[X]`).
- **Typed alternative**: `edit INDEX [c/COMPANY] [r/ROLE] [s/STAGE] [d/DATE] [p/PORTAL] [e/EMAIL] [n/NOTES]` changes any combination of fields you give, for example `edit 2 r/Backend Intern s/Interview d/2026-12-01`.
- **Confirmation message**:
  For field choices updating company, role, or where optional fields are cleared / omitted:
  ```text
  Application updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Recruitment stage: Applied
  ```
  When optional fields (deadline, portal link, email, notes) are set or updated:
  ```text
  Application updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Recruitment stage: Applied
  Deadline: 2026-11-20
  Portal Link: https://careers.google.com
  Email: recruiter@google.com
  Notes: Technical interview round completed
  ```
  For field choice `3` (Recruitment stage only):
  ```text
  Application recruitment stage updated successfully.
  Company: Alphabet Inc.
  Role: Senior SWE Intern
  Recruitment stage: Accepted
  ```

---

### Recruitment stage tracking

Each application has one of these stages: `Applied`, `Online Assessment`, `Interview`, `Offer`, `Accepted`, `Rejected`, or `Withdrawn`. `Accepted`, `Rejected`, and `Withdrawn` are terminal stages. You can still edit a stage at any time to correct a mistake or reflect a company process; stages do not enforce a fixed transition order.

#### Updating a stage
Under menu option `9`, select field `3` (Recruitment stage) or `8` (All). You can specify the stage in three flexible ways:
1. **By number (1 to 7)**:
   - `1`: `Applied`
   - `2`: `Online Assessment`
   - `3`: `Interview`
   - `4`: `Offer`
   - `5`: `Accepted`
   - `6`: `Rejected`
   - `7`: `Withdrawn`
2. **By stage name**: Case-insensitive names are accepted, with spaces, hyphens, or underscores ignored (e.g. `Interview`, `online assessment`, `online-assessment`, `withdrawn`).
3. **By shortcut or alias**:
   - `OA` or `oa` for `Online Assessment`
   - `pending` for `Applied` (migrates legacy files)
   - `offer accepted` for `Accepted`

You can also update the stage directly using the typed command `edit INDEX s/STAGE`, for example `edit 1 s/oa` or `edit 2 s/Interview`.

#### Inspecting an application stage
Use the `view INDEX` command to view the comprehensive card for any application, including its recruitment stage:
```text
view 1
==================================================
Application Details [Index: 1]
--------------------------------------------------
Company:   Google
Role:      SWE Intern
Recruitment stage: Accepted
Completed: No
==================================================
```

---

### Search and sort applications

#### Search applications
Choose `7`, then enter part of a company name or role. Search ignores letter case. For example, `eng` matches both `Engineer` in a role and `Engineering Co` in a company name. An empty search term is rejected and you will be prompted again. If nothing matches, JobSeek tells you so.

You can also type `search KEYWORD` (for example `search eng`) or, to combine several conditions, `find` (see [Finding with combined filters](#finding-with-combined-filters-find)).

Search results show the same application numbers as the normal list. For example, if the matching record is application `3`, you can still enter `3` when marking or deleting it. Search does not change or save application order.

#### Sort applications alphabetically
Choose `8` or type `sort` to view applications ordered by company name, then by role when company names match. Sorting ignores letter case. The number beside each application is its original number in the normal list. This is a display-only view: it does not rearrange the saved applications. If you later type `list all`, the original order is shown again.

---

### Deleting an application

Choose `4` to remove an application. JobSeek asks you to confirm first, and you can reverse the deletion with `undo`.

1. Enter `list all` to find the number of the application you want to delete.
2. Enter `4` at the main menu.
3. Enter the application number when prompted. (Or type `delete INDEX`, for example `delete 2`, to skip the prompt for the number.)
4. JobSeek shows the application and asks `Delete ... ? (y/n)`. Enter `y` or `yes` to delete it. Any other answer cancels the deletion and nothing changes.

For example, entering `2` at the deletion prompt and `y` at the confirmation prompt removes application number `2`.

Application numbers after the deleted entry shift down by one, so check the list again before deleting another application.

If the list is empty or the application number is invalid, JobSeek displays an error without deleting anything.

---

### Undoing a change: `undo`

Reverses the most recent change to your applications, such as an accidental deletion.

- **Usage**: Enter `undo` at the main menu prompt.
- **What can be undone**: adding, deleting, editing any application field(s) (company, role, recruitment stage, deadline, portal link, email, notes, or all fields), and marking or unmarking an application.
- Enter `undo` again to step further back, newest change first.
- Commands that only display information (`list`, `view`, `search`, `today`) and cancelled or failed commands are not recorded.
- The history is kept in memory only, so it is cleared when you exit JobSeek.
- If there is nothing to undo, JobSeek shows `Error: Nothing to undo.`.
- If saving the undo fails, the application list stays as it was before the attempt and the history entry is retained. Resolve the storage problem and enter `undo` again to retry.

```text
> undo
____________________________________________________________
Undone: deleted [ ] Google | SWE Intern
____________________________________________________________
```

---

### Marking an application as completed: `mark`

Marks the application at the specified index as completed (`[X]`). Completed applications represent concluded application pipelines (such as accepted offers or finalized cycles) and are hidden from the default active list view to keep your workspace clutter-free.

Format: Choose `5` from the menu, then enter `<application_number>`, or type `mark <application_number>`.

Examples:
* Choose `5`, then enter `1` – Marks application number 1 as completed.
* `mark 1` – Does the same in one line.

Example output:
```text
Nice! I've marked this application as completed:
  [X] Google | SWE Intern
```

> **Notes on marking applications:**
> * If the application is already completed, JobSeek says so and makes no change (so it is also not added to the `undo` history).
> * If your application list is currently empty, JobSeek notifies you:
>   `Error: Your application list is currently empty.`
> * If the entered index is blank, non-numeric, or out of range, JobSeek rejects it with an error and reprompts:
>   * `Error: Please specify a valid application index.`
>   * `Error: Invalid application index. Please enter a valid number.`
>   * `Error: Invalid application index: X. Please enter a number between 1 and Y.`

---

### Unmarking an application: `unmark`

Reverts the completion status of the application at the specified index back to incomplete (`[ ]`). The application is reactivated and will appear in active application listings.

Format: Choose `6` from the menu, then enter `<application_number>`, or type `unmark <application_number>`.

Examples:
* Choose `6`, then enter `1` – Reverts application number 1 back to incomplete.
* `unmark 1` – Does the same in one line.

Example output:
```text
OK, I've marked this application as not completed yet:
  [ ] Google | SWE Intern
```

> **Notes on unmarking applications:**
> * If your application list is currently empty, JobSeek notifies you:
>   `Error: Your application list is currently empty.`
> * If the entered index is blank, non-numeric, or out of range, JobSeek rejects it with an error and reprompts:
>   * `Error: Please specify a valid application index.`
>   * `Error: Invalid application index. Please enter a valid number.`
>   * `Error: Invalid application index: X. Please enter a number between 1 and Y.`

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

If no applications require attention, no warning summary appears. Warnings are checked once at startup and do not automatically refresh while JobSeek remains open. To see the same information at any time, use the [`today` dashboard](#today-dashboard-today).

---

### Today dashboard: `today`

Shows what needs your attention right now, without restarting JobSeek.

- **Usage**: Enter `today` at the main menu prompt.
- **Sections**: `OVERDUE`, `DUE TODAY` and `UPCOMING` (deadlines within the next seven days), followed by a `SUMMARY` of active and completed applications and a count of active applications per recruitment stage.
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

If saving a change fails, JobSeek reports the error and restores the previous in-memory application list. The failed change is not added to undo history. If saving an `undo` fails, its history entry is retained so you can retry after resolving the storage problem. Success messages appear only after saving succeeds.

JobSeek automatically saves application data to `data/applications.txt` when commands modify application records. Saved records are loaded when you next start JobSeek.

- Company, role, completion status, recruitment stage, optional deadline, company portal link, contact email, and notes are preserved between sessions.
- Data files saved by older versions still load. Missing stages and legacy `Pending` values become `Applied`.
- Company names beginning with `#`, such as `#Acme`, are saved and restored normally.
- If the data file does not exist, JobSeek starts with an empty application list.
- **Corrupted-line recovery**: If any line in the data file is corrupted (e.g., malformed format, invalid boolean flag, invalid deadline, or empty required field), JobSeek displays an informative warning, skips the corrupted record, and successfully loads all remaining valid records. Because saving rewrites the file, the skipped lines would be lost, so JobSeek first copies your original file to `data/applications.txt.bak` (before your first change in that session) and tells you when it starts. Copy any line you want to repair from the `.bak` file.
- Files saved with a leading byte-order mark (added by some text editors) are read normally.
- If loading completely fails due to an unrecoverable file-reading error, JobSeek displays an error and an empty list. Changes and saving are disabled to protect the original file. Repair or restore the data file, then restart JobSeek before adding or changing applications. Help, viewing commands and exit remain available.
- `search`, `find`, `sort`, `list`, `view`, `today` and `help` only display information and do not modify saved data.
- `undo` history is not saved: it is cleared when you exit JobSeek.

The data file location is relative to the folder from which you launch JobSeek. Launch the application from the same folder each time to access the same saved records.

---

## Help instructions

Choose `10` to view the comprehensive reference of all menu options and textual command shortcuts.

- **Usage**: Enter `10` at the main menu prompt.
- **Output**:
  ```text
  Available menu options:
    list [completed|all]            - List active, completed, or all applications
    undo                            - Undo the last add, delete, edit, stage or mark change
    add c/COMPANY r/ROLE [d/DATE]   - Add an application in one line
    edit INDEX [c/..] [r/..] [s/..] - Edit company, role or stage in one line
    delete|mark|unmark INDEX        - Delete, complete or reopen an application
    find c/.. r/.. s/.. b/.. a/..   - Combine filters (company, role, stage, deadline)
    search KEYWORD | sort           - Search by company/role, or view alphabetically
    today                           - Show overdue, due-today and upcoming deadlines
    view INDEX                      - View details for application at INDEX
    [1] Add application             - Add a new internship or job application
    [2] List all applications       - View all applications in a numbered table
    [3] List uncompleted applications - View active / uncompleted applications
    [4] Delete application          - Delete an application by index
    [5] Mark application as completed - Mark an application as completed
    [6] Unmark application          - Mark an application as not completed
    [7] Search applications         - Find applications by company or role
    [8] Sort applications alphabetically - View applications alphabetically
    [9] Edit application            - Edit an existing application's details
    [10] Help                       - Show this help message
    [0] Exit                        - Exit JobSeek
  ```

---

## Command Summary

| Action | Format | Examples / Usage |
|---|---|---|
| **Add application** | `1` or `add c/COMPANY r/ROLE [d/DATE] [p/PORTAL] [e/EMAIL] [n/NOTES]` | Choose `1` (prompts for required company/role and optional deadline, portal link, email, notes), or `add c/Google r/SWE Intern d/2026-11-30` |
| **List active applications** | `list` or `3` | `list` or choose `3` |
| **List completed applications** | `list completed` | `list completed` |
| **List all applications** | `list all` or `2` | `list all` or choose `2` |
| **View application details** | `view INDEX` | `view 1` |
| **Delete application** | `4` or `delete INDEX` | Choose `4`, or `delete 2`; then confirm with `y` |
| **Undo last change** | `undo` | `undo` |
| **Mark application as completed** | `5` or `mark INDEX` | Choose `5`, then enter application index (e.g. `1`), or `mark 1` |
| **Unmark application** | `6` or `unmark INDEX` | Choose `6`, then enter application index (e.g. `1`), or `unmark 1` |
| **Today dashboard** | `today` | `today` |
| **Search applications** | `7` or `search KEYWORD` | Choose `7`, then enter search term (e.g. `Google`), or `search google` |
| **Find with combined filters** | `find [c/..] [r/..] [s/..] [b/..] [a/..] [m/yes\|no]` | `find c/google s/interview` |
| **Sort applications alphabetically** | `8` or `sort` | Choose `8`, or `sort`, to view applications alphabetically |
| **Edit application** | `9` or `edit INDEX [c/..] [r/..] [s/..] [d/..] [p/..] [e/..] [n/..]` | Choose `9` (select field 1-8 to modify), or `edit 2 s/Interview` |
| **Help instructions** | `10` or `help` | `10` or `help` |
| **Exit JobSeek** | `0` or `exit` | `0` or `exit` |
