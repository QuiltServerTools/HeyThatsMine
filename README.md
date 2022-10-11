# HeyThatsMine

[![build](https://img.shields.io/github/workflow/status/fabricservertools/HeyThatsMine/build)](https://github.com/fabricservertools/HeyThatsMine/actions)

[![discord](https://img.shields.io/discord/764543203772334100?label=Fabric%20Server%20Tools%20Discord)](https://discord.gg/jydqZzkyEa)

[![discord](https://img.shields.io/discord/776126068024410135?label=Potatos%20Place)](https://discord.gg/ByaVuebAPb)

HTM is a fabric mod for protecting your containers and trusting people with access to them

[Requires Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

## Getting started

HTM requires no additional setup apart from placing it in your mods folder. All new containers will automatically be set to private.

HTM also supports the luckperms API, which allows you to manage permissions. Permission nodes are listed in the relevant section. All nodes except admin are enabled for all users by default

## Using HTM

The mod has multiple commands which you can use on your containers

### Flag

`/htm flag`: Checks the flags of a specific container. Left click on the container after running this command to check

`/htm flag <type> <value>`: Left click a container to set the flag

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
`canTrustedPlayersBreakChests`: Toggles whether players trusted to a locked container can break the container
(set to false by default meaning only the owner can break a locked container).

`defaultFlags`:
    - `hoppers`: Toggles whether hoppers can pull from locked containers by default, true by default meaning hoppers can pull from locked containers.
`autolockingContainers`: List of containers which will be set to PRIVATE by default
(remove or comment out items in the list to make them set to public by default).

### Additional permissions

`htm.admin`: Allows unrestricted access to containers and other managerial permissions