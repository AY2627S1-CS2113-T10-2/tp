# JobSeek User Guide

JobSeek is a command-line tracker for job and internship applications. It tracks each application's company, role, and completion status.

## Quick start

1. Install Java 25 or later.
2. Run the latest `jobseek.jar` using `java -jar jobseek.jar`.
3. Enter a number from the menu and follow the prompts. Enter `0` to exit.

## Menu options

| Number | Action | What it does |
| --- | --- | --- |
| `1` | Add application | Enter a company and role. Both must be non-empty. |
| `2` | List applications | Show every application with its number and status. |
| `3` | Delete application | Enter an application number to remove it. |
| `4` | Mark application completed | Enter an application number to mark it complete. |
| `5` | Unmark application | Enter an application number to mark it incomplete. |
| `6` | Help | Show the menu options. |
| `7` | Search applications | Find applications by company or role. |
| `8` | Sort applications | View applications alphabetically by company, then role. |
| `0` | Exit | Quit JobSeek. |

### Search applications

Choose `7`, then enter part of a company name or role. Search ignores letter case. For example, `eng` matches both `Engineer` in a role and `Engineering Co` in a company name. An empty search term is rejected and you will be prompted again. If nothing matches, JobSeek tells you so.

Search results show the same application numbers as the normal list. For example, if the matching record is application `3`, you can still enter `3` when marking or deleting it. Search does not change or save application order.

### Sort applications alphabetically

Choose `8` to view applications ordered by company name, then by role when company names match. Sorting ignores letter case. The number beside each application is its original number in the normal list. This is a display-only view: it does not rearrange the saved applications. If you later choose `2`, the original order is shown again.

## Data

Search, sort, list, and help only display information. They do not change application data. In the current team `master`, file persistence is not yet implemented, so applications are kept only for the current run. A separate storage task is in progress.
