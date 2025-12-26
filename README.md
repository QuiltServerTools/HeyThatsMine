# HeyThatsMine

[![build]( https://img.shields.io/github/actions/workflow/status/QuiltServerTools/HeyThatsMine/main.yml?branch=master)](https://github.com/fabricservertools/HeyThatsMine/actions)
[![discord](https://img.shields.io/discord/764543203772334100?label=Fabric%20Server%20Tools%20Discord)](https://discord.gg/jydqZzkyEa)
[![discord](https://img.shields.io/discord/776126068024410135?label=Potatos%20Place)](https://discord.gg/ByaVuebAPb)

HTM is a fabric mod for protecting your containers and trusting people with access to them

[Requires Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

## Getting started

HTM requires no additional setup apart from placing it in your mods folder. All new containers will automatically be set to private.

HTM also supports the LuckPerms API, which allows you to manage permissions. Permission nodes are listed in the relevant section. All nodes except admin are enabled for all users by default

## Using HTM

The mod has multiple commands which you can use on your containers

### Flag

`/htm flag`: Checks the flags of a specific container. Left-click on the container after running this command to check

`/htm flag <type> <value>`: Left-click a container to set the flag

`/htm flag <type>`: Left-click a container to reset the flag to the default value defined in the config file

Permission node: `htm.command.flag`

### Set

`/htm set PUBLIC`: Allows everyone to access the container

`/htm set PRIVATE`: Allows only the owner and those with permissions to access the container

`/htm set KEY`: Allows only those with a key to access the container

Permission node: `htm.command.set`

### Trust

`/htm trust <player> [global]`: Allows a player to access that container if private

Permission node: `htm.command.trust`

### Untrust

`/htm untrust <player> [global]`: Revokes a player's access to the container

Permission node: `htm.command.trust`

### Remove

`/htm remove`: Removes all protections from a container

Permission node: `htm.command.remove`

### Transfer

`/htm transfer <player>`: Transfers ownership to another player

Permission node: `htm.command.transfer`

### Persist

Toggles persist mode, which allows you to continue executing the same action without typing the command again

`/htm persist`

Permission node: `htm.command.persist`

### Quiet

Toggles no message mode, which hides non-command messages like automatic protection creation and override.

`/htm quiet`

Permission node: `htm.command.quiet`

### Config

The config file can be found in `<server>/config/htm_config.json`.

`can_trusted_players_break_chests`: Toggles whether players trusted to a locked container can break the container

    (is set to false by default, meaning only the owner can break a locked container).

`default_flags`:
   
   - `overrides`: Overrides for default flags for containers.

         (a map of blocks or block tags to a map of flag overrides, overrides do not need to contain all flags. Empty by default)

   - `defaults`: The default flags, applicable to all blocks when no overrides are present. 

       - `hoppers`: Toggles whether hoppers can pull from locked containers by default 

             (is set to true by default, meaning hoppers can pull from locked containers).
       - `copper_golems`: Toggles whether copper golems can take items from or put items into locked containers by default

             (is set to true by default, meaning copper golems can take items from or put items into locked containers)

`auto_locking_containers`: Map of containers which will be automatically locked

    (add or remove blocks to the map to lock them by default, or not, block tags can also be used).

An example config file, with custom flag overrides for copper golems:

```json
{
  "can_trusted_players_break_chests": false,
  "default_flags": {
    "overrides": {
      "chest": {
        "copper_golems": true
      },
      "#copper_chests": {
        "copper_golems": true
      }
    },
    "default": {
      "hoppers": true,
      "copper_golems": false
    }
  },
  "auto_locking_containers": {
    "minecraft:chest": "private",
    "minecraft:trapped_chest": "private",
    "minecraft:blast_furnace": "private",
    "minecraft:barrel": "private",
    "minecraft:smoker": "private",
    "minecraft:furnace": "private",
    "#minecraft:copper_chests": "public",
    "#minecraft:shulker_boxes": "public"
  }
}
```

The file above disables copper golem access to all locked containers except copper chests and normal chests. Flag overrides
can also be used to only have a specific set of locked containers allow hoppers by default. Flags can always be overridden
by players on a per-locked container instance basis.

### Additional permissions

`htm.admin`: Allows unrestricted access to containers and other managerial permissions
