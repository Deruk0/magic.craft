---
name: write-changelog
description: Tracks what changed in the mod and writes a versioned changelog entry. Always use this skill when the user mentions versioning, changelog, "what changed", "log the changes", "write the release notes", or asks to document what was done. Also trigger it automatically whenever a significant feature, fix, or refactor was just completed — even if the user hasn't asked — because recording progress is a mandatory step before any commit. Trigger when: multiple files were changed, a new system was added, bugs were fixed, a feature was finished, or any substantial mod work was done in the current session.
---

# Write Changelog

This skill records what changed in the current working session and appends a proper versioned entry to the project changelog. Run it before committing to keep the history readable and the version number accurate.

---

## Step 1 — Determine What Changed

Run:
```bash
git status
git diff --stat
```

Read the output carefully. Categorise the changes:
- **Added** — new files, new features, new commands, new items
- **Changed** — modified behaviour, refactored code, updated configs
- **Fixed** — bug fixes, corrected logic

**Why:** The changelog entry must describe real changes, not invented ones. Read the actual diff, don't assume.

---

## Step 2 — Determine the New Version

Open `versions/changelog.txt`. Find the most recent version header (e.g., `## v1.0.3`).

Increment by:
| Change type | Bump |
|---|---|
| Bug fixes only | PATCH: `v1.0.3 → v1.0.4` |
| New features (backward-compatible) | MINOR: `v1.0.3 → v1.1.0` |
| Breaking changes | MAJOR: `v1.0.3 → v2.0.0` |

If `versions/changelog.txt` doesn't exist yet, create it and start with `v1.0.0`.

---

## Step 3 — Write the Entry

Prepend (add at the **top** of the file) a new entry:

```
## v1.0.4 — 2026-02-26

### Added
- Mana stat and leveling UI screen

### Changed
- Progression commands prefixed with `magiccraft`

### Fixed
- GUI Scale 4 layout overflow in StatsScreen
```

Rules:
- Only include sections that have items (skip empty `### Fixed` etc.)
- Use plain language — readable by a non-developer
- One bullet = one logical change (don't bundle unrelated things)

---

## Step 4 — Confirm with User

Show the user the new entry and the version number, and ask: **"Does this look right, or should I adjust anything?"**

Wait for confirmation before finishing. The user may want to reword entries or adjust the version bump.
