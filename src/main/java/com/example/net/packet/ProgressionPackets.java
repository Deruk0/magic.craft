package com.example.net.packet;

import com.example.core.progression.ProgressionData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ProgressionPackets {
    public static final Identifier LEVEL_UP_STAT_C2S = new Identifier("template-mod", "level_up_stat");
    public static final Identifier SYNC_PROGRESSION_DATA_S2C = new Identifier("template-mod", "sync_progression_data");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(LEVEL_UP_STAT_C2S,
                (server, player, handler, buf, responseSender) -> {
                    String statName = buf.readString();

                    server.execute(() -> {
                        if (player instanceof ProgressionData data) {
                            if (data.getStatPoints() > 0) {
                                boolean leveledUp = false;
                                switch (statName) {
                                    case "strength" -> {
                                        if (data.getStrengthLevel() < 20) {
                                            data.setStrengthLevel(data.getStrengthLevel() + 1);
                                            leveledUp = true;
                                        }
                                    }
                                    case "speed" -> {
                                        if (data.getSpeedLevel() < 20) {
                                            data.setSpeedLevel(data.getSpeedLevel() + 1);
                                            leveledUp = true;
                                        }
                                    }
                                    case "health" -> {
                                        if (data.getHealthLevel() < 20) {
                                            data.setHealthLevel(data.getHealthLevel() + 1);
                                            leveledUp = true;
                                        }
                                    }
                                    case "luck" -> {
                                        if (data.getLuckLevel() < 20) {
                                            data.setLuckLevel(data.getLuckLevel() + 1);
                                            leveledUp = true;
                                        }
                                    }
                                    case "mining" -> {
                                        if (data.getMiningSpeedLevel() < 20) {
                                            data.setMiningSpeedLevel(data.getMiningSpeedLevel() + 1);
                                            leveledUp = true;
                                        }
                                    }
                                }
                                if (leveledUp) {
                                    data.setStatPoints(data.getStatPoints() - 1);
                                }
                            }
                        }
                    });
                });
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_PROGRESSION_DATA_S2C,
                (client, handler, buf, responseSender) -> {
                    int customExp = buf.readInt();
                    int customLevel = buf.readInt();
                    int statPoints = buf.readInt();
                    int strength = buf.readInt();
                    int speed = buf.readInt();
                    int health = buf.readInt();
                    int luck = buf.readInt();
                    int mining = buf.readInt();

                    client.execute(() -> {
                        if (client.player instanceof ProgressionData data) {
                            data.setCustomExp(customExp);
                            data.setCustomLevel(customLevel);
                            data.setStatPoints(statPoints);
                            data.setStrengthLevel(strength);
                            data.setSpeedLevel(speed);
                            data.setHealthLevel(health);
                            data.setLuckLevel(luck);
                            data.setMiningSpeedLevel(mining);
                        }
                    });
                });
    }

    public static void sendSyncPacket(ServerPlayerEntity player) {
        if (player instanceof ProgressionData data) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(data.getCustomExp());
            buf.writeInt(data.getCustomLevel());
            buf.writeInt(data.getStatPoints());
            buf.writeInt(data.getStrengthLevel());
            buf.writeInt(data.getSpeedLevel());
            buf.writeInt(data.getHealthLevel());
            buf.writeInt(data.getLuckLevel());
            buf.writeInt(data.getMiningSpeedLevel());
            ServerPlayNetworking.send(player, SYNC_PROGRESSION_DATA_S2C, buf);
        }
    }
}
