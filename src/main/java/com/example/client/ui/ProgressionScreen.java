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
        private static final int BG_WIDTH = 250;
        private static final int BG_HEIGHT = 240;
        private int x, y;

        private ButtonWidget strengthBtn, speedBtn, healthBtn, luckBtn, miningBtn;

        public ProgressionScreen() {
                super(Text.translatable("screen.template-mod.progression"));
        }

        @Override
        protected void init() {
                super.init();
                this.x = (this.width - BG_WIDTH) / 2;
                this.y = (this.height - BG_HEIGHT) / 2;

                int btnW = 16;
                int btnH = 16;
                int btnX = x + BG_WIDTH - btnW - 14;

                int startY = y + 45;
                int spacing = 38;

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
                        boolean hasPoints = data.getStatPoints() > 0;
                        strengthBtn.active = hasPoints && data.getStrengthLevel() < 20;
                        speedBtn.active = hasPoints && data.getSpeedLevel() < 20;
                        healthBtn.active = hasPoints && data.getHealthLevel() < 20;
                        luckBtn.active = hasPoints && data.getLuckLevel() < 20;
                        miningBtn.active = hasPoints && data.getMiningSpeedLevel() < 20;

                        // Title Header
                        context.fillGradient(x + 2, y + 2, x + BG_WIDTH - 2, y + 20, 0xAA0F3460, 0x000F3460);
                        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("✦ Elemental Attunement ✦"),
                                        this.width / 2, y + 6, 0xFFE9A8);

                        int requiredXp = data.getCustomLevel() * 10;
                        float xpProgress = Math.min(1.0f, (float) data.getCustomExp() / requiredXp);

                        // Level & Points (Beautiful Header)
                        int barX = x + 15;
                        int barY = y + 28;

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
                        context.fill(x + 10, y + 42, x + BG_WIDTH - 10, y + 43, 0x55FFFFFF);

                        // Stat names, values & progress bars
                        int startY = y + 46;
                        int spacing = 38;
                        drawStatEx(context, "⚔ Strength", data.getStrengthLevel(), startY, 0xFFFF8844,
                                        "Melee Damage: +1.0"); // Fiery Orange
                        drawStatEx(context, "⚡ Agility", data.getSpeedLevel(), startY + spacing, 0xFF44AAFF,
                                        "Move Speed: +1.5%"); // Lightning / Water
                        drawStatEx(context, "❤ Vitality", data.getHealthLevel(), startY + spacing * 2, 0xFFFF69B4,
                                        "Max Health: +1.0"); // Pink
                        drawStatEx(context, "☘ Fortune", data.getLuckLevel(), startY + spacing * 3, 0xFF55FF55,
                                        "Looting Bonus: +1.0"); // Earth Green
                        drawStatEx(context, "⛏ Efficiency", data.getMiningSpeedLevel(), startY + spacing * 4,
                                        0xFFFFCC00, "Mining Speed: +20%"); // Lightning Yellow
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
                int startY = y + 46;
                int spacing = 38;

                checkAndDrawTooltip(context, mouseX, mouseY, statX, startY, statWidth, 34,
                                "Increases melee damage (Fire Base).");
                checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing, statWidth, 34,
                                "Move faster on ground (Water Base).");
                checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing * 2, statWidth, 34,
                                "Increases maximum health.");
                checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing * 3, statWidth, 34,
                                "Better loot chances (Earth Base).");
                checkAndDrawTooltip(context, mouseX, mouseY, statX, startY + spacing * 4, statWidth, 34,
                                "Break blocks faster (Lightning Base).");
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
        public boolean shouldPause() {
                return false;
        }
}
