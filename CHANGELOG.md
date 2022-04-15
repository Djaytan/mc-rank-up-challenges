# Changelog

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

### Refactoring/Design:
* Restructuration of the whole code in order to be in accord with MVC principles
* Abstraction of LuckPerms, JobsReborn and Vault libraries to permit a switch of them easily

### Other:
* Repair CI
