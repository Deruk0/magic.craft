package com.example.core.event;

import com.example.core.progression.ProgressionData;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.mob.MobEntity;

public class ServerEventsRegistration {
    public static void registerEvents() {
        // Sync progression when joining a server
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ((ProgressionData) handler.player).syncProgressionData();
        });

        // Sync when coming back from End/Nether
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((ProgressionData) player).syncProgressionData();
        });

        // Example: killing a mob gives custom XP
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ProgressionData data && killedEntity instanceof MobEntity) {
                data.addCustomExp(3);
            }
        });
    }
}
