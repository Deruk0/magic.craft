package com.example.client.keybind;

import com.example.client.ui.ProgressionScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ProgressionKeybinds {
    public static KeyBinding openProgressionUiKey;

    public static void register() {
        openProgressionUiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.template-mod.open_progression", // Translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.template-mod.keys" // Translation category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openProgressionUiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ProgressionScreen());
                }
            }
        });
    }
}
