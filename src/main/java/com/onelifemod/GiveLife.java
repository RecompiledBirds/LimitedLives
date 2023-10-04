package com.onelifemod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GiveLife {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(literal("life")
                .then(literal("give")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("amount", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                                    int amount = IntegerArgumentType.getInteger(source, "amount");
                                                    ServerPlayer sourcePlayer = source.getSource().getPlayer();
                                                    ServerPlayer target = EntityArgument.getPlayer(source, "player");

                                                    LifeUtility.ModifyPlayerLives(sourcePlayer, -amount);
                                            LifeUtility.ModifyPlayerLives(target, amount);
                                                    source.getSource().sendSuccess(Component.literal("Gave " + target.getName().getString() + " " + amount + (amount > 1 ? " lives" : " life") + "."), true);
                                                    return 0;
                                                }
                                        )
                                )
                        )
                ).then(literal("set").requires((p) -> p.hasPermission(2))
                        .then(argument("player", EntityArgument.player())
                                .then(argument("amount", IntegerArgumentType.integer(0))
                                        .executes((source) -> {
                                            int amount = IntegerArgumentType.getInteger(source, "amount");
                                            ServerPlayer target = EntityArgument.getPlayer(source, "player");
                                            LifeUtility.SetLives(target, amount);
                                            source.getSource().sendSuccess(Component.literal("Set " + target.getName().getString() + " lives to " + amount), true);
                                            return 0;
                                        })
                                )
                        )
                ).then(literal("add").requires((p) -> p.hasPermission(2))
                        .then(argument("player", EntityArgument.player())
                                .then(argument("amount", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                                    int amount = IntegerArgumentType.getInteger(source, "amount");
                                                    ServerPlayer target = EntityArgument.getPlayer(source, "player");
                                                    LifeUtility.ModifyPlayerLives(target, amount);
                                                    source.getSource().sendSuccess(Component.literal("Gave " + target.getName().getString() + " " + amount + (amount > 1 ? " lives" : " life") + "."), true);
                                                    return 0;
                                                }
                                        )
                                )
                        )
                )
        );
    }
}
