package com.example.core.event;

import com.example.core.progression.ProgressionData;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.mob.MobEntity;

public class ServerEventsRegistration {
    public static void registerEvents() {
        // Luck stat → Fortune enchantment bonus on ore drops, via
        // EnchantmentHelperMixin.
        LuckFortuneHandler.register();
        // Sync progression when joining a server and re-apply all attributes.
        // The server.execute delay lets the player entity fully load before touching
        // attributes.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.execute(() -> {
                if (handler.player instanceof ProgressionData data) {
                    data.syncProgressionData();
                    // Force attribute re-application server-side (luck, speed, health, etc.)
                    data.refreshAttributes();
                }
            });
        });

        // Sync when coming back from End/Nether
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((ProgressionData) player).syncProgressionData();
        });

        // Preserve progression data across death / respawn.
        // "alive" is true when keepInventory gamerule is on; we copy regardless.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (oldPlayer instanceof ProgressionData oldData && newPlayer instanceof ProgressionData newData) {
                newData.copyProgressionFrom(oldData);
            }
        });

        // Sync progression immediately after the player finishes respawning and loading
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer instanceof ProgressionData data) {
                data.syncProgressionData();
                data.refreshAttributes();
            }
        });

        // Example: killing a mob gives custom XP
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ProgressionData data && killedEntity instanceof MobEntity) {
                data.addCustomExp(3);
            }
        });
    }
}
