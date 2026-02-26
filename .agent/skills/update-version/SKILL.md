---
name: update-version
description: DEPRECATED — this skill has been split into two focused skills. Use 'write-changelog' to record what changed and bump the version number. Use 'git-push' to stage, commit, and push to GitHub. When the user asks to commit, push, save, or deploy, use 'git-push'. When the user asks to log changes or update the version, use 'write-changelog'.
---

# update-version (Deprecated)

This skill has been split into two focused skills for better separation of concerns:

| Task | Skill to use |
|---|---|
| Record what changed, bump version, write changelog | **`write-changelog`** |
| Stage, commit, and push to GitHub | **`git-push`** |

For a full save-and-push session, run **`write-changelog`** first, then **`git-push`**.

> Read `.agent/skills/write-changelog/SKILL.md` and `.agent/skills/git-push/SKILL.md` for the actual instructions.
