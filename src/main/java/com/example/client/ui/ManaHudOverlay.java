package com.example.client.ui;

import com.example.core.progression.ProgressionData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ManaHudOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.hudHidden ||
                client.player.isCreative() || client.player.isSpectator())
            return;

        ProgressionData data = (ProgressionData) client.player;
        float currentMana = data.getCurrentMana();
        int maxManaLevel = data.getMaxManaLevel();
        float maxMana = 50.0f + (maxManaLevel * 10.0f);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Position: Right side, above the food bar (food bar is height 9, from
        // center+10 to center+91)
        // Air bubbles are at screenHeight - 49. We place Mana there.
        int barWidth = 81; // Same width as 10 hearts/food pieces
        int barHeight = 5;
        int x = screenWidth / 2 + 10;
        int y = screenHeight - 49;

        // Draw Mana text
        String manaText = String.format("%.0f / %.0f", currentMana, maxMana);
        int textW = client.textRenderer.getWidth(manaText);
        int textX = x + (barWidth - textW) / 2;
        int textY = y - 9; // Right above the bar
        drawContext.drawTextWithShadow(client.textRenderer, manaText, textX, textY, 0x00FFFF);

        float progress = Math.min(1.0f, currentMana / maxMana);

        // Background
        drawContext.fill(x, y, x + barWidth, y + barHeight, 0xAA000000);
        // Foreground (gradient from light blue to deep blue)
        drawContext.fillGradient(x, y, x + (int) (barWidth * progress), y + barHeight, 0xFF00BFFF, 0xFF0055FF);
        // Golden Border matching the Progression UI
        drawContext.drawBorder(x - 1, y - 1, barWidth + 2, barHeight + 2, 0xFFD4AF37);
    }
}
