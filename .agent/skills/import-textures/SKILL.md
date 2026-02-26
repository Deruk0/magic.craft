---
name: import-textures
description: Import textures from the assets-input folder into the correct resource locations of the mod. Use this skill whenever the user says things like "добавь текстуру", "перенеси мою текстуру", "я добавил текстуру в assets-input", "use my texture", "import textures", or any time a texture file needs to be moved from assets-input/ into the game resources. Also use this skill proactively before adding a new item if there may already be a texture waiting in assets-input/.
---

# Import Textures Skill

This skill scans the `assets-input/` folder at the root of the mod project and copies any found PNG textures to their correct destination inside `src/main/resources/assets/template-mod/textures/`.

## Folder Mapping

| Source folder             | Destination folder                                                                 |
|---------------------------|------------------------------------------------------------------------------------|
| `assets-input/items/`     | `src/main/resources/assets/template-mod/textures/item/`                           |
| `assets-input/blocks/`    | `src/main/resources/assets/template-mod/textures/block/`                          |
| `assets-input/armor/`     | `src/main/resources/assets/template-mod/textures/models/armor/`                   |

## Step-by-Step Instructions

1. **Scan** the `assets-input/` folder using `find_by_name` with pattern `*.png` to discover all PNG files the user has placed there.

2. **Map** each found file to its correct destination using the table above (based on its subfolder).

3. **Copy** each texture to the destination path using `run_command` with PowerShell's `Copy-Item`:
   ```powershell
   Copy-Item -Path "SOURCE_PATH" -Destination "DEST_PATH" -Force
   ```
   Create the destination directory first if it doesn't exist:
   ```powershell
   New-Item -ItemType Directory -Force -Path "DEST_DIR"
   ```

4. **Update the item model JSON** — if a texture name matches an existing item model (e.g. `fire_staff.png` → `models/item/fire_staff.json`), ensure the model's `layer0` is already pointing to `template-mod:item/<name>`. Fix it if it still points to a vanilla placeholder.

5. **Report** to the user exactly which files were found and where they were copied.

## Example

User drops `fire_staff.png` into `assets-input/items/`.

You run:
```powershell
Copy-Item -Path "c:\...\assets-input\items\fire_staff.png" -Destination "c:\...\src\main\resources\assets\template-mod\textures\item\fire_staff.png" -Force
```

Then verify `models/item/fire_staff.json` has:
```json
"layer0": "template-mod:item/fire_staff"
```

## Notes

- The `assets-input/` folder is **not** part of the built mod. It is only a staging area.
- After copying, the original file in `assets-input/` is left in place so it acts as a backup.
- If no PNG files are found, tell the user what to do: place `.png` files matching item IDs into the correct subfolder.
