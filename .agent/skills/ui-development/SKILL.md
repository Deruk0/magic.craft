---
name: ui-development
description: Use this skill whenever the user asks to create, modify, or debug UI, HUD, or Screens in the Minecraft mod. It contains rules and best practices for scaling, rendering, and architecture of UI elements. Trigger automatically when the user asks to: show a menu, display player stats, add a button, draw text on screen, create an inventory-style window, add a progress bar, show health or mana, render a tooltip, add a keybind to open something, display any in-game information visually, or implement any interaction that involves rendering — even if they don't say "UI", "Screen", or "HUD" explicitly.
---

# UI Development — Minecraft Mod (Fabric 1.20.1)

This skill defines the rules and patterns for building UI, HUD, and Screens in Minecraft 1.20.1 mods. The goal: interfaces that feel polished and work correctly at every window size and GUI Scale.

---

## Rule 1: Never Hardcode Absolute Coordinates

Always derive positions from `this.width` and `this.height`.

**Why:** Players change window size and GUI Scale (1–4, Auto) constantly. Hardcoded positions break the layout and push elements off-screen.

```java
// Centering a 176×166 panel
int x = (this.width  - PANEL_WIDTH)  / 2;
int y = (this.height - PANEL_HEIGHT) / 2;
```

For edge-anchored elements, add a fixed margin from the corner:
```java
int marginX = 4;
int cornerX = marginX; // top-left anchor
```

---

## Rule 2: Use DrawContext for All Rendering

In Minecraft 1.20+, `DrawContext` replaced the old `MatrixStack` pattern. Use it for every visual operation.

**Why:** Calling legacy matrix methods causes render state corruption and visual artifacts.

| Operation | Method |
|---|---|
| Draw texture | `context.drawTexture(texture, x, y, u, v, w, h)` |
| Draw text | `context.drawText(textRenderer, text, x, y, color, shadow)` |
| Filled rectangle | `context.fill(x1, y1, x2, y2, color)` |
| Gradient rectangle | `context.fillGradient(x1, y1, x2, y2, colorTop, colorBot)` |
| Tooltip | `context.drawTooltip(textRenderer, lines, x, y)` |

Text padding: always add at least 4px from any panel edge so text never sticks to borders.

---

## Rule 3: Render in the Correct Layer Order

What is drawn *later* appears *on top*. Follow this order inside `render()`:

1. `this.renderBackground(context)` — dim the world behind the screen
2. Panel backgrounds / textures
3. Decorative elements (icons, progress bars, dividers)
4. Interactive elements (buttons) with hover highlights
5. Text labels and values
6. **Tooltips last** — `context.drawTooltip(...)` must be the final call

**Why:** Tooltips drawn in the middle get hidden under panels. Backgrounds drawn after buttons erase them.

---

## Rule 4: Make Interactive Elements Feel Alive

Buttons and panels must respond visually to hover.

**Why:** Static UIs feel unfinished. Hover feedback is expected in modern games and guides the player's eye.

```java
boolean hovered = mouseX >= x && mouseX <= x + w
               && mouseY >= y && mouseY <= y + h;

int color = hovered ? 0xFF_FFFFFF : 0xFF_AAAAAA;
context.fill(x, y, x + w, y + h, hovered ? 0x80_FFFFFF : 0x60_000000);
context.drawText(textRenderer, label, x + 4, y + 4, color, true);
```

---

## Rule 5: UI is Client-Only — Use Packets for State Changes

The UI must **never** mutate game state directly on the client.

**Why:** The client is not authoritative. Accepting client-side changes without server validation opens the mod to exploitation and desync bugs.

```
[Button clicked] → send C2S packet (e.g., LevelUpStatC2SPacket)
    → Server validates (enough points? legal action?)
    → Server mutates state
    → Server sends S2C sync packet back
[Client receives sync] → screen refreshes from server data
```

---

## Rule 6: Aesthetics — No Default Gray Squares

Use custom textures or styled fills for all panels.

**Why:** Default Minecraft UI looks generic. A mod earns its identity through a distinct visual style.

- Use semi-transparent black backgrounds: `0xA0_000000`
- Use `fillGradient` for bars (mana, XP, health) instead of flat fills
- Use your mod's custom texture atlas for panels and icons when available
- Titles and headers: centered, with shadow (`drawTextWithShadow`)

---

## Pre-Commit Checklist

Before finalising any UI work, verify:

- [ ] **No magic numbers** — every x/y is derived from `this.width`/`this.height` or a named constant
- [ ] **GUI Scale safe** — mentally test at Scale 1 (large elements) and Scale 4 (tiny elements)
- [ ] **Tooltip is the last draw call** in `render()`
- [ ] **Hover state** is calculated and applied to all clickable elements
- [ ] **No direct state mutations** in button handlers — only packet sends
- [ ] **Text has padding** from panel edges (minimum 4px)

---

## Common Mistakes

| Mistake | Fix |
|---|---|
| `drawText` with hardcoded `x=100` | Use `(this.width / 2) - textWidth / 2` |
| Tooltip drawn before buttons | Move `drawTooltip` to the very end of `render()` |
| Changing player stats inside `mouseClicked` | Send a C2S packet, let the server handle it |
| Using `MatrixStack` directly | Use `DrawContext` (wraps the stack internally) |
| Button works at Scale 2, breaks at Scale 4 | Derive all sizes from screen dimensions, not constants |
