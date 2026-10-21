# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}

### Search and alphabetical views

`Main` translates numeric menu input into a `Command`. Option `7` asks for a non-blank search term, then creates `SearchCommand`; option `8` creates `SortCommand`. Both commands operate on an `ApplicationList` but do not mutate it or call `Storage.save`.

The commands collect zero-based indices of matching applications. `SearchCommand` matches a case-insensitive substring against both company and role. `SortCommand` sorts a copy of the indices by company, then role, using a case-insensitive comparator; original index breaks ties. `ApplicationTable` formats either view with one-based numbers derived from the original indices. This keeps subsequent mark and delete commands unambiguous, even when the display order differs from storage order.

```mermaid
sequenceDiagram
    actor User
    participant Main
    participant SearchCommand
    participant ApplicationList
    participant ApplicationTable
    participant Ui
    User->>Main: Select 7 and enter search term
    Main->>SearchCommand: execute(applications, ui, storage)
    loop Every application
        SearchCommand->>ApplicationList: get(index)
        ApplicationList-->>SearchCommand: application
    end
    SearchCommand->>ApplicationTable: format(applications, matching indices)
    ApplicationTable-->>SearchCommand: table with original numbers
    SearchCommand->>Ui: showMessage(table)
    Ui-->>User: Matching applications
```

The alphabetical view follows the same formatting path after sorting the index copy. Keeping the list itself unchanged means there is no save operation and existing application numbers remain stable.


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|job seeker|find applications by company or role|locate an application without scanning the full list|
|v2.0|job seeker|view applications alphabetically|compare applications by company and role more easily|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}

### Search and sort

1. Start JobSeek with an empty application list and add `Zulu / Engineer`, `Acme / Designer`, and `Acme / Analyst` using option `1`.
2. Choose `7` and search for `ENG`. Confirm that only `Zulu / Engineer` appears with number `1`. Search for `acm` and confirm that both Acme applications appear with their original numbers `2` and `3`.
3. Search for a term that does not occur in any company or role. Confirm that a no-matches message appears. Enter a blank term and confirm that the program asks again.
4. Choose `8`. Confirm the view shows `Acme / Analyst` (`3`), `Acme / Designer` (`2`), then `Zulu / Engineer` (`1`).
5. Choose `2`. Confirm the original order is still `Zulu`, `Acme / Designer`, `Acme / Analyst`.
