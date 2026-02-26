package com.example.client.ui;

import com.example.core.progression.ProgressionData;
import com.example.net.packet.ProgressionPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class ProgressionScreen extends Screen {
        private static final int BG_WIDTH = 260;
        private static final int BG_HEIGHT = 250;
        private int x, y;
        private boolean isMagicTab = false; // toggle between tabs

        private ButtonWidget strengthBtn, speedBtn, healthBtn, luckBtn, miningBtn;
        private ButtonWidget maxManaBtn, manaRegenBtn;

        public ProgressionScreen() {
                super(Text.translatable("screen.magiccraft.progression"));
        }

        @Override
        protected void init() {
                super.init();
                this.x = (this.width - BG_WIDTH) / 2;
                this.y = (this.height - BG_HEIGHT) / 2;

                int btnW = 16;
                int btnH = 16;
                int btnX = x + BG_WIDTH - btnW - 14;

                int startY = y + 66;
                int spacing = 36;

                this.strengthBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("strength"))
                                                .dimensions(btnX, startY, btnW, btnH).build());
                this.speedBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("speed"))
                                                .dimensions(btnX, startY + spacing, btnW, btnH).build());
                this.healthBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("health"))
                                                .dimensions(btnX, startY + spacing * 2, btnW, btnH).build());
                this.luckBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("luck"))
                                                .dimensions(btnX, startY + spacing * 3, btnW, btnH).build());
                this.miningBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("mining"))
                                                .dimensions(btnX, startY + spacing * 4, btnW, btnH).build());

                // Magic Stats Buttons
                this.maxManaBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("max_mana"))
                                                .dimensions(btnX, startY, btnW, btnH).build());
                this.manaRegenBtn = this.addDrawableChild(
                                ButtonWidget.builder(Text.literal("+"), button -> sendLevelUpPacket("mana_regen"))
                                                .dimensions(btnX, startY + spacing, btnW, btnH).build());

                updateVisibility();
        }

        private void updateVisibility() {
                strengthBtn.visible = !isMagicTab;
                speedBtn.visible = !isMagicTab;
                healthBtn.visible = !isMagicTab;
                luckBtn.visible = !isMagicTab;
                miningBtn.visible = !isMagicTab;

                maxManaBtn.visible = isMagicTab;
                manaRegenBtn.visible = isMagicTab;
        }

        private void sendLevelUpPacket(String statName) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(statName);
                ClientPlayNetworking.send(ProgressionPackets.LEVEL_UP_STAT_C2S, buf);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                // Dim the world slightly
                context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0000000);

                // Elegant RPG Panel Background
                // Outer glow/shadow
                context.fill(x - 2, y - 2, x + BG_WIDTH + 2, y + BG_HEIGHT + 2, 0x44000000);
                // Main Background (Dark Magical Indigo to Deep Blue)
                context.fillGradient(x, y, x + BG_WIDTH, y + BG_HEIGHT, 0xF01A1A2E, 0xF016213E);
                // Golden Border
                context.drawBorder(x, y, BG_WIDTH, BG_HEIGHT, 0xFFD4AF37);
                // Inner thin border for depth
                context.drawBorder(x + 2, y + 2, BG_WIDTH - 4, BG_HEIGHT - 4, 0x55FFFFFF);

                ProgressionData data = this.client.player != null ? (ProgressionData) this.client.player : null;
                if (data != null) {
                        // Title Header
                        context.fillGradient(x + 2, y + 2, x + BG_WIDTH - 2, y + 20, 0xAA0F3460, 0x000F3460);
                        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("✦ Elemental Attunement ✦"),
                                        this.width / 2, y + 6, 0xFFE9A8);

                        boolean hasPoints = data.getStatPoints() > 0;
                        int requiredXp = data.getCustomLevel() * 10;
                        float xpProgress = Math.min(1.0f, (float) data.getCustomExp() / requiredXp);

                        // Custom RPG Tabs Rendering
                        int tabW = 100;
                        int tabH = 16;
                        int tabY = y + 24;
                        int leftTabX = x + 15;
                        int rightTabX = x + BG_WIDTH - tabW - 15;

                        // Physical Tab
                        boolean hoverPhysical = mouseX >= leftTabX && mouseX <= leftTabX + tabW && mouseY >= tabY
                                        && mouseY <= tabY + tabH;
                        int physColor = !isMagicTab ? 0xFFD4AF37 : (hoverPhysical ? 0xFFAAAAAA : 0xFF555555);
                        int physBgColor = !isMagicTab ? 0xAA222233 : 0x66111111;
                        context.fill(leftTabX, tabY, leftTabX + tabW, tabY + tabH, physBgColor);
                        context.drawBorder(leftTabX, tabY, tabW, tabH, physColor);
                        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Physical"),
                                        leftTabX + tabW / 2, tabY + 4, physColor);

                        // Magic Tab
                        boolean hoverMagic = mouseX >= rightTabX && mouseX <= rightTabX + tabW && mouseY >= tabY
                                        && mouseY <= tabY + tabH;
                        int magicColor = isMagicTab ? 0xFF00FFFF : (hoverMagic ? 0xFFAAAAAA : 0xFF555555);
                        int magicBgColor = isMagicTab ? 0xAA112244 : 0x66111111;
                        context.fill(rightTabX, tabY, rightTabX + tabW, tabY + tabH, magicBgColor);
                        context.drawBorder(rightTabX, tabY, tabW, tabH, magicColor);
                        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Magic"),
                                        rightTabX + tabW / 2, tabY + 4, magicColor);

                        // Level & Points (Beautiful Header)
                        int barX = x + 15;
                        int barY = y + 50;

                        context.drawTextWithShadow(this.textRenderer, "Lvl: " + data.getCustomLevel(), barX, barY - 4,
                                        0xFFD700);

                        String xpText = data.getCustomExp() + " / " + requiredXp + " XP";
                        int xpTextX = x + (BG_WIDTH / 2) - (this.textRenderer.getWidth(xpText) / 2);
                        context.drawTextWithShadow(this.textRenderer, xpText, xpTextX, barY - 4, 0x00FFFF);

                        String ptsText = "AP: " + data.getStatPoints();
                        context.drawTextWithShadow(this.textRenderer, ptsText,
                                        x + BG_WIDTH - 15 - this.textRenderer.getWidth(ptsText), barY - 4, 0xFFB6C1);

                        // Glowing Magic XP Bar
                        int barWidth = BG_WIDTH - 30;
                        int barHeight = 6;
                        context.fill(barX, barY + 6, barX + barWidth, barY + 6 + barHeight, 0xFF111111);
                        context.fillGradient(barX, barY + 6, barX + (int) (barWidth * xpProgress), barY + 6 + barHeight,
                                        0xFF00FFCC, 0xFF0066FF);

                        // Divider line
                        context.fill(x + 10, y + 64, x + BG_WIDTH - 10, y + 65, 0x55FFFFFF);

                        // Button Activity
                        strengthBtn.active = hasPoints && data.getStrengthLevel() < 20;
                        speedBtn.active = hasPoints && data.getSpeedLevel() < 20;
                        healthBtn.active = hasPoints && data.getHealthLevel() < 20;
                        luckBtn.active = hasPoints && data.getLuckLevel() < 20;
                        miningBtn.active = hasPoints && data.getMiningSpeedLevel() < 20;

                        maxManaBtn.active = hasPoints && data.getMaxManaLevel() < 20;
                        manaRegenBtn.active = hasPoints && data.getManaRegenLevel() < 20;

                        // Draw stats based on selected tab
                        int startY = y + 66;
                        int spacing = 36;

                        if (!isMagicTab) {
                                drawStatEx(context, "⚔ Strength", data.getStrengthLevel(), startY, 0xFFFF8844,
                                                "Melee Damage: +0.25");
                                drawStatEx(context, "⚡ Agility", data.getSpeedLevel(), startY + spacing, 0xFF44AAFF,
                                                "Move Speed: +2%");
                                drawStatEx(context, "❤ Vitality", data.getHealthLevel(), startY + spacing * 2,
                                                0xFFFF69B4,
                                                "Max Health: +0.5♥");
                                drawStatEx(context, "☘ Luck", data.getLuckLevel(), startY + spacing * 3, 0xFF55FF55,
                                                "Chance to double ore drops");
                                drawStatEx(context, "⛏ Efficiency", data.getMiningSpeedLevel(), startY + spacing * 4,
                                                0xFFFFCC00, "Mining Speed: +7.5%");
                        } else {
                                drawStatEx(context, "❈ Max Mana", data.getMaxManaLevel(), startY, 0xFF44BBFF,
                                                "Increases maximum mana pool by 10");
                                drawStatEx(context, "❂ Mana Regen", data.getManaRegenLevel(), startY + spacing,
                                                0xFF77DDFF,
                                                "Speeds up mana recovery (+0.5/sec)");
                        }

                }

                super.render(context, mouseX, mouseY, delta);

                if (data != null) {
                        drawTooltips(context, mouseX, mouseY);
                }
        }

        private void drawStatEx(DrawContext context, String name, int level, int rowY, int color, String bonusText) {
                int maxLvl = 20;
                float progress = Math.min(1.0f, (float) level / maxLvl);

                // Characteristic Name (Left aligned)
                context.drawTextWithShadow(this.textRenderer, name, x + 14, rowY + 4, color);

                // Level text indicator (Right aligned before the button)
                String lvlText = "Lvl " + level + " / " + maxLvl;
                int lvlColor = level >= maxLvl ? 0xFFFFD700 : 0xFFDDDDDD;
                int lvlX = x + BG_WIDTH - 38 - this.textRenderer.getWidth(lvlText);
                context.drawTextWithShadow(this.textRenderer, lvlText, lvlX, rowY + 4, lvlColor);

                // Progress Bar (Below title)
                int barWidth = BG_WIDTH - 28;
                int barHeight = 4;
                int barX = x + 14;
                int barY = rowY + 16;

                context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF222222);
                context.fillGradient(barX, barY, barX + (int) (barWidth * progress), barY + barHeight, color,
                                darkenColor(color));

                // Bonus text (Below progress bar)
                context.drawTextWithShadow(this.textRenderer, bonusText, x + 14, barY + 8, 0xFFDDDDDD);

                // Divider
                context.fill(x + 10, rowY + 34, x + BG_WIDTH - 10, rowY + 35, 0x33FFFFFF);
        }

        private int darkenColor(int hexColor) {
                int a = (hexColor >> 24) & 0xFF;
                int r = (hexColor >> 16) & 0xFF;
                int g = (hexColor >> 8) & 0xFF;
                int b = hexColor & 0xFF;
                return (a << 24) | ((r / 2) << 16) | ((g / 2) << 8) | (b / 2);
        }

        private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
                int statX = x + 8;
                int statWidth = BG_WIDTH - 16;
                int startY = y + 66;
                int spacing = 36;

                if (!isMagicTab) {
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY, statWidth, 30,
                                        "Increases melee damage (Fire Base).");
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing, statWidth, 30,
                                        "Move faster on ground (Water Base).");
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing * 2, statWidth, 30,
                                        "Adds 0.5♥ max health per level.");
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing * 3, statWidth, 30,
                                        "Increases chance to double ore drops (+2.5% chance per level).");
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing * 4, statWidth, 30,
                                        "Break blocks faster (+7.5% speed per level).");
                } else {
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY, statWidth, 30,
                                        "Increases the maximum limit of mana available.");
                        checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing, statWidth, 30,
                                        "Increases the rate at which mana naturally regenerates.");
                }
        }

        private void checkAndDrawTooltip(DrawContext context, int mouseX, int mouseY, int hX, int hY, int hW, int hH,
                        String text) {
                if (mouseX >= hX && mouseX <= hX + hW && mouseY >= hY && mouseY <= hY + hH) {
                        List<Text> lines = new ArrayList<>();
                        lines.add(Text.literal(text));
                        context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
                }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                        int tabW = 100;
                        int tabH = 16;
                        int tabY = y + 24;
                        int leftTabX = x + 15;
                        int rightTabX = x + BG_WIDTH - tabW - 15;

                        // Click on Physical
                        if (mouseX >= leftTabX && mouseX <= leftTabX + tabW && mouseY >= tabY
                                        && mouseY <= tabY + tabH) {
                                isMagicTab = false;
                                updateVisibility();
                                return true;
                        }
                        // Click on Magic
                        if (mouseX >= rightTabX && mouseX <= rightTabX + tabW && mouseY >= tabY
                                        && mouseY <= tabY + tabH) {
                                isMagicTab = true;
                                updateVisibility();
                                return true;
                        }
                }
                return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean shouldPause() {
                return false;
        }
}
