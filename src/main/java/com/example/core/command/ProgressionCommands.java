package com.example.core.command;

import com.example.core.progression.ProgressionData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ProgressionCommands {
        public static void register() {
                CommandRegistrationCallback.EVENT
                                .register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
        }

        private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
                dispatcher.register(CommandManager.literal("magiccraft")
                                .then(CommandManager.literal("addpoints")
                                                .requires(source -> source.hasPermissionLevel(2)) // OP only
                                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                                                .executes(context -> {
                                                                        ServerPlayerEntity player = context.getSource()
                                                                                        .getPlayerOrThrow();
                                                                        int amount = IntegerArgumentType
                                                                                        .getInteger(context, "amount");

                                                                        if (player instanceof ProgressionData data) {
                                                                                data.setStatPoints(data.getStatPoints()
                                                                                                + amount);
                                                                                player.sendMessage(Text.literal("Added "
                                                                                                + amount
                                                                                                + " stat points!"),
                                                                                                false);
                                                                                return 1;
                                                                        }
                                                                        return 0;
                                                                })))
                                .then(CommandManager.literal("addexp")
                                                .requires(source -> source.hasPermissionLevel(2)) // OP only
                                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                                                .executes(context -> {
                                                                        ServerPlayerEntity player = context.getSource()
                                                                                        .getPlayerOrThrow();
                                                                        int amount = IntegerArgumentType
                                                                                        .getInteger(context, "amount");

                                                                        if (player instanceof ProgressionData data) {
                                                                                data.addCustomExp(amount);
                                                                                player.sendMessage(Text.literal("Added "
                                                                                                + amount
                                                                                                + " custom experience!"),
                                                                                                false);
                                                                                return 1;
                                                                        }
                                                                        return 0;
                                                                })))
                                .then(CommandManager.literal("resetstats")
                                                .requires(source -> source.hasPermissionLevel(2)) // OP only
                                                .executes(context -> {
                                                        ServerPlayerEntity player = context.getSource()
                                                                        .getPlayerOrThrow();
                                                        if (player instanceof ProgressionData data) {
                                                                data.resetProgression();
                                                                player.sendMessage(Text.literal(
                                                                                "Your progression has been reset!"),
                                                                                false);
                                                                return 1;
                                                        }
                                                        return 0;
                                                }))
                                .then(CommandManager.literal("help")
                                                .executes(context -> {
                                                        ServerPlayerEntity player = context.getSource()
                                                                        .getPlayerOrThrow();
                                                        player.sendMessage(Text.literal("§6--- Magic Mod Commands ---"),
                                                                        false);
                                                        player.sendMessage(Text.literal(
                                                                        "§e/magiccraft help §7- Show this help message"),
                                                                        false);
                                                        if (context.getSource().hasPermissionLevel(2)) {
                                                                player.sendMessage(
                                                                                Text.literal("§c[Admin] §e/magiccraft addpoints <amount> §7- Add stat points"),
                                                                                false);
                                                                player.sendMessage(
                                                                                Text.literal(
                                                                                                "§c[Admin] §e/magiccraft addexp <amount> §7- Add magic experience"),
                                                                                false);
                                                                player.sendMessage(
                                                                                Text.literal("§c[Admin] §e/magiccraft resetstats §7- Reset your progression"),
                                                                                false);
                                                        }
                                                        player.sendMessage(Text.literal(
                                                                        "§bHint: §7Press 'P' to open the Magic Progression menu!"),
                                                                        false);
                                                        return 1;
                                                })));
        }
}
