# Improvement / Change Request Template

> **How to use this file**
> 1. Copy this file and rename it: `improvement-[short-slug]-[date].md` (e.g. `improvement-validation-2026-05-01.md`).
> 2. Fill in every section below as completely as you can.
> 3. Hand the filled-in file to the AI. It will ask clarifying questions, and together you will refine the scope.
> 4. Once mutual understanding is confirmed, the AI produces a **Work-Order prompt** as a new `.md` file.
> 5. You review and approve the work-order before any code is changed.
> 6. When you hand the approved work-order back, the AI follows a **strict two-phase TDD process**:
>    - **Phase 1 — Tests only:** The AI writes the test file(s) and explains every test (purpose + why). It then **stops** and waits for your explicit sign-off.
>    - **Phase 2 — Production code:** Only after you approve the tests does the AI touch any production source files.
>
> ⚠️ The AI must **never** combine Phase 1 and Phase 2 in a single response.

---

## 1 — Request metadata

| Field | Value                                       |
|---|---------------------------------------------|
| **Title** | _(one-line description of the improvement)_ |
| **Date** | YYYY-MM-DD                                  |
| **Priority** | Low / Medium / High / Critical              |
| **Author** | NH                                          |

---

## 2 — What do you want to improve or change?

> Describe in plain language what you want to be different. No need for technical detail yet — describe the outcome you want.

```
[Your description here]
```

---

## 3 — Why? (motivation / problem statement)

> What is wrong with the current situation? What risk, bug, performance issue or code quality concern is driving this?

```
[Your motivation here]
```

---

## 4 — Affected area(s)

> List the files, classes, methods, or pages you believe are involved. It is okay if the list is incomplete.

- [ ] Controller: ___
- [ ] Service: ___
- [ ] Repository: ___
- [ ] Model/Entity: ___
- [ ] Template (HTML): ___
- [ ] JavaScript: ___
- [ ] Config / Properties: ___
- [ ] Tests: ___
- [ ] Other: ___

---

## 5 — Current behaviour

> Describe what happens today. Include steps to reproduce if it is a bug.

```
[Current behaviour]
```

---

## 6 — Desired behaviour

> Describe what should happen after the improvement is applied. Be as specific as possible.

```
[Desired behaviour]
```

---

## 7 — Acceptance criteria

> List the conditions that must be true for this improvement to be considered done. Write them as testable statements.

- [ ] ___
- [ ] ___
- [ ] ___

---

## 8 — Constraints and non-goals

> List anything that must NOT change, patterns you want to keep, backward-compatibility requirements, performance limits, etc.

```
[Constraints / non-goals]
```

---

## 9 — Additional context / references

> Links to prior discussions, related code review findings, external docs, screenshots, etc.

```
[Links / references / notes]
```

---

## 10 — Notes for the AI (optional)

> Any specific preferences: libraries to prefer or avoid, naming conventions, level of refactoring allowed, etc.

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

1. **Goal statement** — confirmed in one sentence.
2. **Scope** — exhaustive list of files and methods to change, and what stays untouched.
3. **Design decisions** — chosen approach, patterns, libraries.
4. **Flow diagram** — Mermaid diagram showing control/data flow of the changed logic.
5. **Test plan** — list of unit and integration tests to write _first_ (TDD):
   - Each test: name, input, expected result, type (unit / integration / e2e).
6. **🔴 PHASE 1 — Tests + walkthrough (AI executes this first, then STOPS)**
   - The AI creates all test file(s) in full.
   - Directly below the code, the AI adds a numbered **Test Walkthrough** section. For every test it explains:
     - *What* is being asserted.
     - *Why* this specific case matters (edge case, happy path, contract, etc.).
   - The AI ends its response with the line:
     > _"All tests are written. Please review the code and walkthrough above. Reply with **'approved'** — or give feedback — before I write any production code."_
   - **The AI writes no production code in this response.**
7. **🟢 PHASE 2 — Production code (AI executes this only after explicit approval)**
   - Begins only when you reply with an explicit approval (e.g. "approved", "go ahead", "lgtm").
   - Ordered, granular implementation steps to make all tests green.
8. **Documentation updates** — which comments, Javadoc, README sections or other docs to update.
9. **Definition of done** — checklist derived from acceptance criteria above.

