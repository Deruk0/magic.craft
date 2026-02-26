---
name: update-guide
description: Updates guides/guide.txt when mod content changes — new mechanics, items, stats, systems, or design decisions. Always use this skill when the user says "обнови гайд", "запиши в гайд", "добавь в документацию", "обнови концепт". Also trigger it automatically whenever: a new item type or mechanic was added to the mod, an existing mechanic was significantly changed, a new stat or progression system was designed, or any content that belongs in the project concept document was modified — even if the user didn't ask to update the guide. The guide is the project's single source of truth — it must stay in sync with the actual mod.
---

# Update Guide Skill

This skill keeps `guides/guide.txt` up to date as the mod evolves. The file is **the project's single source of truth** — it should always reflect what the mod actually contains, not what was originally planned.

**What `guide.txt` is:**
- A structured design document describing the mod's mechanics, items, progression, and systems
- The reference that ALL other AI work (code generation, naming, balancing) is based on
- Written in plain language — not code, not changelogs — just clear design descriptions

**What `guide.txt` is NOT:**
- A changelog (that's `changelog.txt`)
- A developer log (that's `devlog.txt`)
- A TODO list

---

## When to Update guide.txt

Update guide.txt when **content** changes, not just code:

| Trigger | Update guide.txt? |
|---|---|
| New item type or mechanic added | ✅ Yes |
| Existing mechanic changed or balanced | ✅ Yes |
| New stat / progression system added | ✅ Yes |
| New UI feature that changes gameplay | ✅ Yes |
| Bug fix with no gameplay change | ❌ No |
| Refactor with identical gameplay | ❌ No |
| Code cleanup | ❌ No |

---

## Step 1 — Read the Current Guide

Open and read `guides/guide.txt` in full.

Understand:
- What is already documented
- What sections exist
- Where the new content fits

---

## Step 2 — Identify What Changed

Look at what was implemented in the current session. Determine:

- **New content** → needs a new section or subsection
- **Changed content** → find the existing section and update it
- **Removed content** → delete or strike through the outdated description

Do not add content that isn't actually implemented yet. The guide describes what exists, not what is planned.

If something is planned but not implemented, add it under a `## Planned / Future` section at the bottom — clearly marked.

---

## Step 3 — Write the Changes

Follow the existing style and structure of `guide.txt`:
- Russian language (as established in the file)
- Markdown headers with `##` and `###`
- Bullet lists for enumerated features
- Emoji markers for categories where they already exist (🔥 💧 ⚡ 🌍)

**Adding a new mechanic** — create a new `###` subsection under the appropriate `##` section.

**Changing a value or rule** — find the relevant bullet and update it in-place.

**Adding a new top-level system** — add a new `##` section.

Keep the existing structure:
```
## Основная идея
## Механики
  ### Посохи
  ### Кольца
  ### Мантии
## Предметы и блоки
## Прогрессия игрока
## Уникальные фишки
## Планируемое (optional)
```

---

## Step 4 — Review with User

Before writing the final version to the file, show the user the proposed changes:

- If it's a new section: show the full text
- If it's an edit: show a brief diff ("Changed: old text → new text")

Ask: **"Всё верно? Добавить что-то ещё?"**

Write to the file only after confirmation.

---

## Step 5 — Write the File

Apply the confirmed changes to `guides/guide.txt`. Prefer **surgical edits** (add/modify the specific section) over rewriting the entire file.

After writing, summarise in one line what was updated:
> "guide.txt обновлён: добавлена секция 'Маги огня' и обновлена механика маны."
