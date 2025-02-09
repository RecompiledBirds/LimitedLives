package com.onelifemod.lives;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.onelifemod.lives.LifeSimpleChannel;
import com.onelifemod.lives.LifeUtility;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class LifeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(literal("life")
                .then(literal("give")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("amount", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                                    int amount = IntegerArgumentType.getInteger(source, "amount");
                                                    ServerPlayer sourcePlayer = source.getSource().getPlayer();
                                                    ServerPlayer target = EntityArgument.getPlayer(source, "player");
                                                    if(amount-1> LifeUtility.GetLives(sourcePlayer)){
                                                        source.getSource().sendFailure(Component.literal("You don't have enough extra lives."));
                                                        return 0;
                                                    }
                                                    LifeUtility.ModifyPlayerLives(sourcePlayer, -amount);
                                                    LifeUtility.ModifyPlayerLives(target, amount);
                                                    Supplier<Component> s = () -> Component.literal("Gave " + target.getName().getString() + " " + amount + (amount !=1 ? " lives" : " life") + ".");
                                                    source.getSource().sendSuccess( s, true);
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
                                            Supplier<Component> s = () -> Component.literal("Set " + target.getName().getString() + " lives to " + amount);
                                            source.getSource().sendSuccess(s, true);
                                            return 0;
                                        })
                                )
                        )
                ).then(literal("add").requires((p) -> p.hasPermission(2))
                        .then(argument("player", EntityArgument.player())
                                .then(argument("amount", IntegerArgumentType.integer())
                                        .executes((source) -> {
                                                    int amount = IntegerArgumentType.getInteger(source, "amount");
                                                    ServerPlayer target = EntityArgument.getPlayer(source, "player");
                                                    LifeUtility.ModifyPlayerLives(target, amount);
                                                    String amountStr=String.valueOf(amount);
                                                    Supplier<Component> s = () -> Component.literal("Gave " + target.getName().getString() + " " + amountStr + (amount > 1 ? " lives" : " life") + ".");
                                                    source.getSource().sendSuccess(s, true);
                                                    return 0;
                                                }
                                        )
                                )
                        )
                ).then(literal("test").requires((p)->p.hasPermission(2))
                .then(literal("giveLifePacket")
                .executes((source)->{
                    ServerPlayer target = source.getSource().getPlayer();
                    LifeSimpleChannel.SendGiveLifePacketToPlayer(target);
                            Supplier<Component> s = () -> Component.literal("Sent you a test packet!");
                    source.getSource().sendSuccess(s, true);
                    return 0;
                }
                )
                )
                )
        );

    }
}
