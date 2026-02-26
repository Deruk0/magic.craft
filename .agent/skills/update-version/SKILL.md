---
name: update-version
description: Commits and pushes all modified files to the corresponding GitHub repository. Use this skill when the user asks to commit, push, save changes to GitHub, update versions, deploy code, bump version, save progress, or sync with remote. Even if the user simply says "push it" or "save my work", this skill should be used.
---

# Update Versions

This skill automates the process of committing and pushing project files to GitHub.

## Execution Steps

1. Read the target GitHub repository URL from the `repo_link.txt` file located in this skill's directory (`.agent/skills/update-version/repo_link.txt`).
2. Analyze the current mod structure, focusing on categories like magic statistics, additions (weapons, staves, rings, mantles, etc.), and their properties.
3. Write a high-level, human-readable structural description of the mod (not a changelog, but an overview of what the mod consists of) to `guides/guide.txt`.
4. Run `git status` and `git diff` to understand what changes were made in the codebase since the last commit.
5. Create a new version entry (e.g., `v1.0.1` etc. based on the previous version) and append a full list of these changes into `versions/changelog.txt`.
6. Stage all changes by running `git add .` (this will include the updated `guides/guide.txt` and `versions/changelog.txt`).
7. Generate a concise, descriptive commit message based on the recent changes made to the codebase (or ask the user for a specific commit message if preferred).
8. Commit the changes: `git commit -m "<message>"`
9. Push the changes to the remote repository: `git push -u origin main`
10. Notify the user that the code has been successfully pushed.
