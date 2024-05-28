package com.github.fabricservertools.htm.command.arguments;

import com.github.fabricservertools.htm.api.Group;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class GroupArgument implements ArgumentType<Group> {
    @Override
    public Group parse(StringReader reader) throws CommandSyntaxException {
        return null;
    }
}
