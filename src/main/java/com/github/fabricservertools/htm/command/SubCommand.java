package com.github.fabricservertools.htm.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;

public interface SubCommand {
	LiteralCommandNode<CommandSourceStack> build();
}
