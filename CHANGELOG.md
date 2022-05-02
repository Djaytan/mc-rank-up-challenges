# Changelog

## Version 1.1.2 (05/02/2022)

### Features
* MessageController now expose a sendRawMessage method
* MessageController methods now accept Audience objects
* Startup banner display during plugin enabling
* Block blacklisted enchantments (by default Infinity and Mending) for usage and drops
  * Usage in enchantment table
  * Usage in anvil
  * Drop from piglin barters, loot chests, entity drops or in fishing treasures
  * Trade with villagers
* Remove blacklisted enchantments at usage (at bow shoot or at mend item)
* Fallback enchantment added when there isn't enchantments anymore is Durability 3
* Remove descriptions in player shops

### Fixes
* Patch break-and-place actions with Jobs Reborn plugin by adding a tag in placed blocks by player
  and cancelling events when tag is detected

### Refactoring/Design
* Homogenize config files with HOCON format from Configurate lib
* Split interfaces and implementations and reorganize the whole project by defining APIs at service
  and controller levels

## Version 1.1.1 (04/15/2022)

### Fixes
* Make plugin compatible with MiniMessage v4.10.0 and higher (required when using PaperMC 1.18.2 or higher)
* Only shown message about missing item for rank challenges when it's not completed yet.

## Version 1.1.0 (04/15/2022)

### Features
* Performances optimization
* Dispatch of a broadcast at rankup
* Possibility to go back to the main menu (`/menu`, command implemented by a third-party program)
* Add of messages depending on context (e.g. teleportation to a playershop)
* Add prefixes to messages depending on the type of message (info, success, failure, warning, error, ...)
* Add the command `/rankup`
* Add the command `/playershop tp <playershop_name>` where the playershop's name correspond to the playername of the owner
* Translation of item names based on the client language of the player

### Fixes
* Critical fix about the possibility unlimited amount of a given item (renamed item was counted but not removed from inventory)

### Refactoring/Design
* Restructuration of the whole code in order to be in accord with MVC principles
* Abstraction of LuckPerms, JobsReborn and Vault libraries to permit a switch of them easily

### Other
* Repair CI
