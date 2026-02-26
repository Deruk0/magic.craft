package com.example;

import com.example.net.packet.ProgressionPackets;
import net.fabricmc.api.ClientModInitializer;

public class TemplateModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProgressionPackets.registerS2CPackets();
        com.example.client.keybind.ProgressionKeybinds.register();
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT
                .register(new com.example.client.ui.ManaHudOverlay());
    }
}
