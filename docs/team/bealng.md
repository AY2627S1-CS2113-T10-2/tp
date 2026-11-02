# Beal Ng Zhi Jie - Project Portfolio Page

## Overview

JobSeek is a command-line app for tracking job and internship applications. Users can record an opportunity, follow its progress, and find it again as their list grows. I worked mainly on listing, searching, and sorting applications, along with a later menu and input cleanup.

## Summary of contributions

### Code contributed

- [My code on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Bealng&breakdown=true)

### Enhancements implemented

**Numbered application listing** - [PR #17](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/17)

- I built the first `ListCommand` view. It showed each saved application's number, completion status, company, and role in a readable table. The numbers came from the main list, so users could use them with commands such as mark and delete.
- I handled an empty list with a clear message and tested both empty and populated lists. This was the basic listing view; the team later added completion filters and deadlines.

**Search and alphabetical views** - [PR #31](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/31)

- I added a case-insensitive search across company and role. It accepts part of a name, making it useful when the user remembers only a few letters. The alphabetical view sorts by company, then role.
- I introduced a shared result-table formatter for these two views. Each result keeps its original application number, even if search hides other records or sort displays them in a different order. Users can take that number straight to view, edit, mark, or delete. I kept the existing list display separate when its columns differed from the new views.
- Sorting copies the application indices instead of rearranging the saved list. I tested partial matches, no results, alphabetical order, ties, and that the stored order stays unchanged. Menu options `8` and `9` open the search and sort views.

**Menu and command cleanup** - [PR #63](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/63)

- I put the ten menu options into two columns so the home screen takes up less space. The numbers and their actions did not change.
- I fixed `sort`, `help`, `today`, `undo`, and `exit` silently accepting extra words. Inputs such as `sort extra` now show a usage error instead of running the command. I added parser and menu tests, then checked the packaged JAR with common commands and invalid input.
- Before the PR was merged, I brought it up to date with the team's newer menu and parser changes. I resolved the overlapping tests and ran the full Gradle check.

### Contributions to the User Guide

- I wrote the initial search and sort sections of the [User Guide](../UserGuide.md), with command examples, no-result behavior, and an explanation of why displayed numbers stay the same. The team later updated those sections as the app grew.
- In [PR #68](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/68), I documented the shorter home menu and the usage error shown when a no-argument command has extra text.

### Contributions to the Developer Guide

- I explained in the [Developer Guide](../DeveloperGuide.md) how search and sort use application indices and the result-table formatter without changing `ApplicationList`. I added an early search sequence diagram and manual test steps; the diagrams were later refined by the team.
- I ran the current JAR and corrected the list, search, and sort examples so the expected text matched the output ([PR #64](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/64)). I also documented the two-column menu and the parser's extra-text check, with manual steps for testing both ([PR #68](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/68)).

### Other contributions

- I updated my profile link on the About Us page in [PR #4](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/4).
