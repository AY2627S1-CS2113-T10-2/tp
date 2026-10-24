# JobSeek User Guide

JobSeek is a command-line tracker for job and internship applications. It tracks each application's company, role, and completion status.

## Quick start

1. Install Java 25 or later.
2. Run the latest `jobseek.jar` using `java -jar jobseek.jar`.
3. Enter a number from the menu and follow the prompts. You can also type the `list` and `view` commands below. Enter `0` to exit.

## Menu options

| Number | Action | What it does |
| --- | --- | --- |
| `1` | Add application | Enter a company and role. Both must be non-empty. |
| `2` | List applications | Show active applications with their original numbers and status. |
| `3` | List uncompleted applications | Show active applications with their original numbers. |
| `4` | Delete application | Enter an application number to remove it. |
| `5` | Mark application completed | Enter an application number to mark it complete. |
| `6` | Unmark application | Enter an application number to mark it incomplete. |
| `7` | Help | Show the menu options. |
| `8` | Search applications | Find applications by company or role. |
| `9` | Sort applications | View applications alphabetically by company, then role. |
| `10` | Edit application | Update specific fields (company, role, or both) of an application. |
| `0` | Exit | Quit JobSeek. |

You can also type `list` for active applications, `list completed` for completed applications, or `list all` for every application. Type `view INDEX` to see the details of an application at its original number.

### Search applications

Choose `8`, then enter part of a company name or role. Search ignores letter case. For example, `eng` matches both `Engineer` in a role and `Engineering Co` in a company name. An empty search term is rejected and you will be prompted again. If nothing matches, JobSeek tells you so.

Search results show the same application numbers as the normal list. For example, if the matching record is application `3`, you can still enter `3` when marking or deleting it. Search does not change or save application order.

### Sort applications alphabetically

Choose `9` to view applications ordered by company name, then by role when company names match. Sorting ignores letter case. The number beside each application is its original number in the normal list. This is a display-only view: it does not rearrange the saved applications. If you later type `list all`, the original order is shown again.

## Data

Search, sort, list, and help only display information. They do not change application data. In the current team `master`, file persistence is not yet implemented, so applications are kept only for the current run. A separate storage task is in progress.
