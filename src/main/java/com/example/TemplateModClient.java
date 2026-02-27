package com.example;

import com.example.net.packet.ProgressionPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import com.example.entity.ModEntityTypes;

public class TemplateModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntityTypes.FIREBALL_PROJECTILE, FlyingItemEntityRenderer::new);
        ProgressionPackets.registerS2CPackets();
        com.example.client.keybind.ProgressionKeybinds.register();
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT
                .register(new com.example.client.ui.ManaHudOverlay());
    }
}
