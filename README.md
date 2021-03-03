# HeyThatsMine

A fabric mod for a container claiming and trusting system.

## Getting started

HTM requires no additional setup apart from placing it in your mods folder. All new containers will automatically be set to private.

## Using HTM

The mod has multiple commands which you can use on your containers

### Flag

`/htm flag`: Checks the flags of a specific container. Right click on the container after running this command to check

`/htm flag <type> <value>`: Right click a container to set the flag

### Set

`/htm set PUBLIC`: Allows everyone to access the container

`/htm set PRIVATE`: Allows only the owner and those with permissions to access the container

`/htm set KEY`: Allows only those with a key to access the container

### Trust

`/htm trust <player>`: Allows a player to access that container if private

### Untrust

`/htm untrust <player>`: Revokes a player's access to the container

### Remove

`/htm remove`: Removes all protections from a container

### Transfer

`/htm transfer <player>`: Transfers ownership to another player
