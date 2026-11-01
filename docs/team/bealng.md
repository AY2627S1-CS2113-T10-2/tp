# Beal Ng Zhi Jie - Project Portfolio Page

## Overview

JobSeek is a command-line app for keeping track of job and internship applications. Users can add applications, update their progress, and find them again later. I worked mainly on the ways users browse their applications: listing, searching, and sorting them.

## Summary of contributions

### Code contributed

- [My code on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Bealng&breakdown=true)

### Enhancements implemented

**Listing applications** - [PR #17](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/17)

- I added the first numbered table of saved applications, showing each company's name, role, and completion status. The numbers match the application's position in the main list, so users know which number to enter for commands such as mark and delete.
- I handled the empty-list case with a clear message and added tests for both empty and populated lists. Other team members later extended the list with completion filters and deadlines.

**Searching and sorting** - [PR #31](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/31)

- I added search by company or role. It accepts part of a name and ignores letter case, so a short term can find several relevant applications. It also gives a clear result when nothing matches.
- I added a separate alphabetical view, ordered by company and then role. Sorting works on a copy of the application indices. It changes what users see without moving records in the saved list.
- Both views show the original application numbers. This matters because a user can search for an application and then use the number shown to edit, mark, view, or delete the same record. I tested matching, ordering, empty results, and that sorting leaves the underlying list unchanged.

**Menu and command cleanup** - [PR #63](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/63) (open)

- I put the ten menu options into two columns so the menu takes up less space. The existing option numbers and actions stay the same.
- I fixed typed commands that silently accepted extra words, such as `sort extra` and `help extra`. They now show a usage error. I added tests for those inputs and checked the packaged JAR with common commands and invalid input.

### Contributions to the User Guide

- I wrote the initial search and sort sections of the [User Guide](../UserGuide.md), including the command syntax, examples, and what happens when there are no results. I explained that search ignores letter case and that sorting does not change the saved order or the application numbers. The team later updated these sections as the app grew.

### Contributions to the Developer Guide

- I described how `SearchCommand` and `SortCommand` collect application indices and pass them to the table formatter without changing the `ApplicationList`. I added an early search sequence diagram and manual test steps to the [Developer Guide](../DeveloperGuide.md). The team later updated parts of the guide.
- I ran the current JAR and corrected the list, search, and sort manual test examples so the expected text matches the app's actual output.

### Other contributions

- I updated my profile link on the About Us page in [PR #4](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/4).
