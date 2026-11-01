# Beal Ng Zhi Jie - Project Portfolio Page

## Overview

JobSeek is a command-line application for tracking job and internship applications. It lets users record opportunities, check their progress, and find applications as the list grows. I worked mainly on listing, searching, and sorting applications.

## Summary of contributions

### Code contributed

[My code on the CS2113 tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Bealng&breakdown=true)

### Enhancements implemented

**List applications** ([PR #17](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/17)). I added the initial numbered `ListCommand` and tests for empty and populated lists. The numbering gives users a way to identify an application when they use commands such as mark or delete. The team later extended listing with completion filters.

**Search and sort** ([PR #31](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/31)). I added a case-insensitive search across company names and roles, plus an alphabetical view ordered by company and then role. Both views keep the original application numbers. Sorting changes only what is displayed, so it does not rearrange or save the underlying list. I connected both commands to the menu and added tests for matching, display order, and unchanged stored order.

**Menu and input cleanup** ([PR #63](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/63), pending review). I shortened the home menu to two columns without changing its option numbers. I also fixed typed commands such as `sort extra` being accepted despite not taking arguments, and added regression tests.

### Documentation and testing

I wrote the initial search and sort sections of the [User Guide](../UserGuide.md), covering the commands, case-insensitive matching, and stable application numbers. In the [Developer Guide](../DeveloperGuide.md), I documented how search and sort operate on indices rather than changing the `ApplicationList`, and added an initial search sequence diagram and manual test steps. The team has since updated those guides and diagrams.

My tests covered empty and multiple-entry listing, search results, alphabetical display, and keeping the original order after sorting. I also updated the About Us page with my profile link ([PR #4](https://github.com/AY2627S1-CS2113-T10-2/tp/pull/4)).
