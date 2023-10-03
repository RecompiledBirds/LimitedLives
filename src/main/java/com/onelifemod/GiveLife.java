package com.onelifemod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sun.jdi.connect.Connector;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;

public class GiveLife {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("life")
                .then(Commands.literal("give")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                                    int amount = IntegerArgumentType.getInteger(source, "amount");
                                                    ServerPlayer sourcePlayer = source.getSource().getPlayer();
                                                    ServerPlayer target = EntityArgument.getPlayer(source, "player");

                                                    EventHandler.ModifyPlayerLives(sourcePlayer, -amount);
                                                    EventHandler.ModifyPlayerLives(target, amount);
                                                    source.getSource().sendSuccess(Component.literal("Gave " + target.getName().getString() + " " + amount + (amount > 1 ? " lives" : " life") + "."), true);
                                                    return 0;
                                                }
                                        )
                                )
                        )
                ).then(Commands.literal("set").requires((p) -> p.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes((source) -> {
                                            int amount = IntegerArgumentType.getInteger(source, "amount");
                                            ServerPlayer target = EntityArgument.getPlayer(source, "player");
                                            EventHandler.SetLives(target, amount);
                                            source.getSource().sendSuccess(Component.literal("Set " + target.getName().getString() + " lives to " + amount), true);
                                            return 0;
                                        })
                                )
                        )
                ).then(Commands.literal("add").requires((p) -> p.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                                    int amount = IntegerArgumentType.getInteger(source, "amount");
                                                    ServerPlayer target = EntityArgument.getPlayer(source, "player");
                                                    EventHandler.ModifyPlayerLives(target, amount);
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
