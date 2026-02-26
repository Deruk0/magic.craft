---
name: ui-development
description: Use this skill whenever the user asks to create, modify, or debug UI, HUD, or Screens in the Minecraft mod. It contains rules and best practices for scaling, rendering, and architecture of UI elements. Make sure to use this skill whenever the user mentions GUI Scale issues, screen rendering bugs, HUD overlays, tooltip rendering, button hover effects, DrawContext usage, or any visual layout problem in Minecraft Fabric 1.20.1 modding — even if they don't explicitly say "UI".
---

# UI Development Skill for Minecraft Mod (Fabric 1.20.1)

This skill contains strict rules and best practices for developing user interfaces (UI), HUDs, and Screens in Minecraft 1.20.1 mods.

The main goal is to create beautiful, adaptive, and performant interfaces that don't break when the window is resized or when "GUI Scale" changes.

## Core Rules (Must always be followed!)

### 1. Adaptivity and Scaling
* **No hardcoded absolute coordinates.** Players can change window size or GUI Scale (from 1 to 4 or Auto). Hardcoded coordinates will inevitably cause overlapping elements or elements going off-screen.
* Use `this.width` and `this.height` variables as starting points for screen rendering.
* To center an element horizontally: `(this.width - elementWidth) / 2`.
* To center vertically: `(this.height - elementHeight) / 2`.
* Anchor elements to screen edges or center, adding relative margins/padding.

### 2. Using DrawContext
In Minecraft 1.20+, all rendering is done through `DrawContext context` (replaced the old `MatrixStack`).
* Texture rendering: `context.drawTexture(...)`.
* Text rendering: `context.drawText(...)` or `context.drawTextWithShadow(...)`. Add padding for text so it doesn't stick to panel edges.
* Rectangle/background rendering: `context.fill(...)` or `drawGradient(...)` method.
* Tooltips: `context.drawTooltip(...)`. These are drawn last (on top of everything else) so they aren't overlapped by other windows.

### 3. Layering (Z-Index)
The order of render method calls is very important. What is called later is drawn *on top* of previous elements.
* **Ideal render order (bottom to top):**
  1. Main background dimming (if it's a `Screen`): `this.renderBackground(context)`.
  2. Interface backgrounds (main panel textures).
  3. Decorative elements (icons, progress bars, divider lines).
  4. Interactive elements (buttons, their hover effects).
  5. Text (titles, stat values).
  6. At the very end of the `render()` method — Tooltips, so they are above EVERYTHING.

### 4. Interactive Elements and Hover Effects
* The interface should feel "alive".
* Implement darkening or highlighting of buttons and panels on mouse hover (isHovered).
* Calculate hover logic: `mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height`.

### 5. Networking and Logic Separation (Client vs Server)
* **Never trust the client!** UI is a client-side entity. If the UI has a "Level Up Skill" button, it must not change stats directly on the client.
* The button should send a **C2S (Client-to-Server) packet** with a level-up request.
* The server validates the logic (enough points/resources), levels up the skill, then sends an **S2C packet** back to the client or uses a sync system to update the visuals.

### 6. Aesthetics and Design
* Use non-standard designs (custom GUI textures instead of default gray Minecraft squares, unless specified otherwise).
* Use beautiful semi-transparent backgrounds (black with alpha channel) for popup elements.
* Draw beautiful indicators/progress bars (e.g., mana bar with gradient or different textures).

### Application Checklist
When creating or updating UI, always think:
- *How will this element look at GUI Scale 3? Will the button go off-screen?*
- *Am I centering the background correctly?*
- *Where is the tooltip being drawn (at the end or not)?*
