# Beal Ng Zhi Jie - Project Portfolio Page

## Overview

JobSeek is a command-line app for keeping track of job and internship applications. Users can add applications, update their progress, and find them again later. I worked mainly on listing, searching, and sorting applications.

## Summary of contributions

### Code contributed

- [My code on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Bealng&breakdown=true)

### Enhancements implemented

**Listing applications** - [PR #17](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/17)

- I added the first numbered list of applications, so users could see which number to use for commands such as mark or delete.
- I added tests for empty and populated lists. The team later added completion filters to listing.

**Searching and sorting** - [PR #31](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/31)

- I added a search by company or role that ignores letter case.
- I added an alphabetical view sorted by company, then role. It changes only the display. The saved list stays in its original order.
- Both views keep the original application numbers, so commands that use a number still point to the right record. I added tests for search results, display order, and the unchanged stored list.

**Menu and command cleanup** - [PR #63](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/63) (open)

- I put the ten menu options into two columns while keeping their existing numbers.
- I fixed typed commands that accepted extra words, such as `sort extra`, and added tests for those cases.

### Contributions to the User Guide

- I wrote the initial search and sort sections of the [User Guide](../UserGuide.md). I explained case-insensitive search and why application numbers stay the same in sorted results. The team later updated these sections.

### Contributions to the Developer Guide

- I described how search and sort use list indices without changing the `ApplicationList`. I also added an early search sequence diagram and manual test steps to the [Developer Guide](../DeveloperGuide.md). The team later updated parts of the guide.
- I corrected the search and sort manual test examples to match the current app output.

### Other contributions

- I updated my profile link on the About Us page in [PR #4](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/4).
