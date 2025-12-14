package com.github.fabricservertools.htm.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

@FunctionalInterface
public interface SubCommand {

    void register(LiteralArgumentBuilder<CommandSourceStack> root);
}
