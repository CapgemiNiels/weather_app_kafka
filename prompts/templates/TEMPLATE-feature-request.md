# Feature Request Template

> **How to use this file**
> 1. Copy this file and rename it: `feature-[short-slug]-[date].md` (e.g. `feature-export-pdf-2026-05-01.md`).
> 2. Fill in **at minimum**: Section 1 (title + date), Section 2 (user story), Section 3 (problem/opportunity). All other sections are optional — leave them blank and the AI will ask targeted questions to fill in the gaps together.
> 3. Hand the filled-in file to the AI. It will ask clarifying questions, and together you will refine the scope.
> 4. Once mutual understanding is confirmed, the AI produces a **Work-Order prompt** as a new `.md` file.
> 5. You review and approve the work-order before any code is changed.
> 6. When you hand the approved work-order back, the AI follows a **strict two-phase TDD process**:
>    - **Phase 1 — Tests only:** The AI writes all test file(s) and explains every test (purpose + why). It then **stops** and waits for your explicit sign-off.
>    - **Phase 2 — Production code:** Only after you approve the tests does the AI touch any production source files.
>
> ⚠️ The AI must **never** combine Phase 1 and Phase 2 in a single response.

---

## 1 — Request metadata

| Field | Value                          |
|---|--------------------------------|
| **Feature title** | _(short, descriptive name)_    |
| **Date** | YYYY-MM-DD                     |
| **Priority** | Low / Medium / High / Critical |
| **Author** | NH                             |

---

## 2 — User story

> Describe the feature from the perspective of the user who benefits from it. Use the format below or write freely.

```
As a [type of user],
I want to [action / goal],
so that [benefit / value].
```

---

## 3 — Problem / opportunity

> Why is this feature needed? What problem does it solve or what opportunity does it unlock? Include any user pain points, business context, or feedback.

```
[Problem or opportunity description]
```

---

## 4 — Feature description

> Describe what the feature should do in plain language. Walk through the happy path: what does the user do, what does the system do, what does the user see at the end?

```
[Feature description / happy path walkthrough]
```

---

## 5 — Acceptance criteria

> List the conditions that must be true for this feature to be accepted. Write each item as a concrete, testable statement.

- [ ] ___
- [ ] ___
- [ ] ___

---

## 6 — Affected / expected area(s) of the codebase

> List the layers, files, classes, or pages you expect will need to be created or changed. It is okay if this is incomplete.

### New artefacts expected
- Controller: ___
- Service: ___
- Repository: ___
- Model/Entity/DTO: ___
- Template (HTML): ___
- JavaScript: ___
- Other: ___

### Existing artefacts expected to change
- [ ] ___
- [ ] ___

---

## 7 — Out of scope (non-goals)

> Explicitly state what this feature should NOT do. This avoids scope creep and misunderstandings.

```
[Non-goals]
```

---

## 8 — UI / UX expectations (if applicable)

> Describe any UI behaviour, page layout, form fields, validation messages, redirects, or user feedback expected. Attach wireframes or sketches if you have them.

```
[UI / UX description or link to mockup]
```

---

## 9 — Data / persistence expectations

> Describe any new or changed database tables, columns, relationships, or seed data. Note any migration requirements.

```
[Data / persistence description]
```

---

## 10 — Integration / dependency expectations

> Does this feature depend on external services, other features, environment variables, or configuration changes?

```
[Dependencies / integrations]
```

---

## 11 — Non-functional requirements

> Performance targets, security requirements, accessibility requirements, browser compatibility, etc.

```
[Non-functional requirements]
```

---

## 12 — Additional context / references

> Links to designs, prior discussions, documentation, related issues, screenshots, etc.

```
[Links / references / notes]
```

---

## 13 — Notes for the AI (optional)

> Preferred libraries or frameworks, naming conventions, patterns to follow or avoid, level of abstraction expected, etc.

```
[Optional notes]
```

---

## — Clarification phase (AI fills this in)

> The AI will add questions here before producing any artefacts. Neither code nor documentation will be changed until all questions are answered and you have confirmed the scope in writing.

### Questions from the AI

_To be filled in by the AI after receiving this document._

### Your answers

_Fill in after the AI posts questions._

### Confirmed scope

_The AI will summarise the agreed scope here. You sign off before work begins._

> **Sign-off:** [ ] I confirm the scope above is correct and work can proceed.

---

## — Generated Work-Order prompt

> The AI produces this section. It becomes a **new file** (`work-order-[slug]-[date].md`) and is the actual prompt the AI uses to do the implementation work.

### Work-Order structure (what the generated file will contain)

The AI-generated work-order file will include:

1. **Goal statement** — confirmed feature goal in one sentence.
2. **Scope** — exhaustive list of files and classes to create or modify, and what stays untouched.
3. **Design decisions** — architecture choices, patterns, library selections.
4. **Flow diagrams** (Mermaid):
   - High-level feature flow (user action → system response).
   - Sequence diagram for any multi-layer interactions (Controller → Service → Repository → DB).
   - Entity-relationship snippet if new data models are introduced.
5. **API / endpoint contract** (if applicable) — HTTP method, path, request structure, response structure, error responses.
6. **Test plan** — to be written and approved _before_ any production code:
   - Unit tests (service, utility classes)
   - Controller / integration tests (`@WebMvcTest`, `@SpringBootTest`)
   - Edge-case and negative-path tests
   - For each test: name, inputs, expected output/behaviour, test type.
7. **🔴 PHASE 1 — Tests + walkthrough (AI executes this first, then STOPS)**
   - The AI creates all test file(s) in full.
   - Directly below the code, the AI adds a numbered **Test Walkthrough** section. For every test it explains:
     - *What* is being asserted.
     - *Why* this specific case matters (happy path, edge case, contract, security, etc.).
   - The AI ends its response with the line:
     > _"All tests are written. Please review the code and walkthrough above. Reply with **'approved'** — or give feedback — before I write any production code."_
   - **The AI writes no production code in this response.**
8. **🟢 PHASE 2 — Production code (AI executes this only after explicit approval)**
   - Begins only when you reply with an explicit approval (e.g. "approved", "go ahead", "lgtm").
   - Ordered, granular implementation steps (write stub code → make tests green → refactor).
9. **Documentation** — Javadoc, inline comments, README or feature-specific `.md` to create or update.
10. **Definition of done** — checklist derived from acceptance criteria plus code quality, documentation and test coverage checks.

