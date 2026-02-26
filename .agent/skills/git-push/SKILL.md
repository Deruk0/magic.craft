---
name: git-push
description: Stages all project files, commits them with a meaningful message, and pushes to the GitHub remote. Always use this skill when the user says anything like "push", "commit", "save to GitHub", "sync", "deploy", "upload changes", or "save my work". Also trigger it automatically after completing any feature, fix, or refactor — even if the user hasn't explicitly asked to push — because saving progress to GitHub is the correct ending step for any significant code change session. Trigger when the user says: "done", "ready", "all good", "let's save", "back it up", "finish up", or after finishing any implementation task where files were modified.
---

# Git Push — Commit & Push to GitHub

This skill stages changes, creates a well-formatted commit, and pushes to the remote repository. It picks up where `write-changelog` leaves off, but can also run standalone.

---

## Before You Start

Read the repository URL from `.agent/skills/update-version/repo_link.txt`.

- If the file **does not exist or is empty**: stop and ask the user for the repository URL before proceeding.
- If the file exists, note the URL — you'll use it if the remote isn't configured yet.

---

## Step 1 — Stage All Changes

```bash
git add .
```

`git add .` captures everything: modified files, new files, deleted files, and new assets. If something should be excluded, it belongs in `.gitignore` — not in manual selective staging.

---

## Step 2 — Generate a Commit Message

Write a message using conventional commits format:

```
<type>(<scope>): <short summary>
```

| Type | When to use |
|---|---|
| `feat` | New feature or mechanic |
| `fix` | Bug fix |
| `refactor` | Code restructure without behaviour change |
| `docs` | Documentation or changelog only |
| `chore` | Build, config, or tooling change |
| `style` | Formatting only |

**Example:** `feat(progression): add mana stat and leveling screen`

If the user already provided a commit message, use it exactly as given.

---

## Step 3 — Commit

```bash
git commit -m "<message>"
```

---

## Step 4 — Verify Remote and Push

Check whether a remote is configured:
```bash
git remote -v
```

If no remote is set:
```bash
git remote add origin <URL from repo_link.txt>
```

Then push:
```bash
git push -u origin main
```

**If push fails, diagnose the error:**

| Error message | Cause | Fix |
|---|---|---|
| `src refspec main does not match any` | Branch is `master`, not `main` | Run `git push -u origin master` |
| `Authentication failed` | No credentials configured | User must set up a GitHub PAT or SSH key |
| `rejected (non-fast-forward)` | Remote has commits the local branch lacks | Run `git pull --rebase`, then push again |

Always show the full error message to the user — never fail silently.

---

## Step 5 — Confirm

Tell the user the push succeeded. Include:
- The commit message used
- The branch pushed to
- The remote URL
