---
name: dev-log
description: Writes a detailed developer log (devlog) with granular technical notes about every change made to the mod. Use this skill whenever the user says "запиши в devlog", "залоггируй", "запиши что сделали", "ведём devlog", or "записывай изменения". Also trigger it automatically during and after any implementation session — even if the user didn't ask — because good developer logs capture implementation decisions, architecture choices, bug root causes, and workarounds that would otherwise be lost. Trigger when: a complex bug was fixed, an architectural decision was made, a refactor was done, a new system was designed, or any non-obvious implementation choice was made.
---

# Dev Log Skill

This skill maintains `versions/devlog.txt` — a detailed **developer-facing** history of the mod, separate from the user-facing `changelog.txt`.

**Difference between the two files:**

| `changelog.txt` | `devlog.txt` |
|---|---|
| High-level, user-readable | Low-level, developer-readable |
| "Added mana stat" | "Implemented ManaComponent using Cardinal Components API, stored in ServerPlayerEntity via `ModComponents.MANA`. Sync via `ManaS2CPacket` on `PlayerJoinCallback` and after every level-up." |
| One bullet per feature | Full technical context, file references, design rationale |

---

## When to Write a Devlog Entry

Write an entry whenever **any** of the following happened:

- A new system, mechanic, or packet was designed or implemented
- A bug was found and fixed — include the root cause and what the fix was
- An architectural decision was made (e.g., "chose Cardinal Components over custom NBT because...")
- A workaround was used for a Minecraft/Fabric limitation
- Existing code was refactored with a meaningful structural change
- A feature was intentionally left incomplete or deferred — log why

If in doubt, log it. Devlogs are cheap to write and invaluable to read a month later.

---

## Entry Format

Each entry starts with a date/version header and can contain multiple subsections.

```
═══════════════════════════════════════════════
[v1.0.3] 2026-02-26
═══════════════════════════════════════════════

## [System/Feature Name]

### What was done
A precise technical description of what was implemented or changed.
Reference specific class names, methods, files, and packages.

### Why this approach
Explain the design decision. What alternatives were considered?
Why was this solution chosen over others?

### Files affected
- src/.../ClassName.java — brief note on what changed

### Known issues / TODO
- Any remaining edge cases, deferred work, or fragile assumptions
```

Omit sections that don't apply. An entry for a minor fix may only need "What was done" and "Files affected".

---

## Step 1 — Collect Technical Context

Before writing, gather:

1. Run `git diff` (not just `--stat`) to read the actual code changes
2. Note: which classes changed, what methods were added/modified, any new packets or events
3. Identify any design decisions made during the session (data storage, sync strategy, event hooks, etc.)
4. If a bug was fixed: what was the root cause? What was the symptom?

---

## Step 2 — Read the Existing Devlog

Open `versions/devlog.txt`. Read the last 2–3 entries to understand the current context and avoid repeating already-documented information.

If the file doesn't exist yet, create it with this header:

```
# Magic Craft — Developer Log
# Format: [vX.Y.Z] YYYY-MM-DD > technical notes per system/feature
═══════════════════════════════════════════════
```

---

## Step 3 — Write the Entry

Prepend the new entry at the **top** of the file (after the header), so newest entries appear first.

Use the entry format from above. Be specific:

- ❌ "Fixed a bug in the UI"
- ✅ "Fixed NPE in `ProgressionScreen.render()` — `client.player` was null during the first render tick before the player entity was fully initialized. Added a null guard at the top of `render()`."

---

## Step 4 — Confirm

Show the entry to the user and ask: **"Should I add anything or adjust the technical details?"**

Do not write to the file until the user confirms or says "looks good".
