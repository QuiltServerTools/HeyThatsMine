package com.github.fabricservertools.htm.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;

public interface SubCommand {
	LiteralCommandNode<ServerCommandSource> build();
}
