# JobSeek Documentation

**JobSeek** is a command-line desktop application (CLI) built for undergraduate students and job seekers to efficiently track, manage, and monitor their internship and job applications. 

Designed for keyboard-driven efficiency, JobSeek helps students keep all application opportunities organized in one central place, monitor recruitment stages, track upcoming deadlines, and evaluate application outcomes without the overhead of heavy GUI applications.

---

## Documentation Links

* **[User Guide (UG)](UserGuide.md)**: Comprehensive user manual covering setup, menu and typed command formats, field specifications, examples, and troubleshooting.
* **[Developer Guide (DG)](DeveloperGuide.md)**: Technical guide detailing system architecture, object structures, component interactions, sequence diagrams, implementation decisions, and manual testing procedures.
* **[About Us](AboutUs.md)**: Project team members and individual portfolio links.

---

## Key Features

- **Application Tracking (`Add`)**: Record opportunities with company, role, application date, deadline, recruitment stage, priority, and optional notes.
- **List & Card Views (`List`, `View`)**: Display active, completed, or all applications in formatted tables, or inspect comprehensive detail cards for individual entries.
- **In-Place Field Updates (`Edit`)**: Update company, role, stage, deadline, priority, or notes directly in place with full multi-field support.
- **Recruitment Stage Progression**: Track applications across 7 standard stages (`Applied`, `Online Assessment`, `Interview`, `Offer`, `Accepted`, `Rejected`, `Withdrawn`).
- **Search & Sort**: Filter applications by company name or role, or sort entries alphabetically or chronologically.
- **Completion Tracking (`Mark`, `Unmark`)**: Toggle completed status independently of recruitment stages.
- **Deletion & Undo Protection (`Delete`, `Undo`)**: Safe deletion workflow with interactive confirmation and single-step undo support.
- **Persistent Storage**: Automatic local file persistence with automated corrupt-record recovery and warning alerts.
