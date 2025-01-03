# Character Dialogue [![Codacy Badge](https://app.codacy.com/project/badge/Grade/488a2448b0b64650adaf6af431c696ee)](https://www.codacy.com/gh/iAtog/CharacterDialogue/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=iAtog/CharacterDialogue&amp;utm_campaign=Badge_Grade)
## Introduction
It is a plugin for SpigotMC/PaperMC that specializes in creating dynamic conversations with npcs, giving a dynamic experience to players.

Wiki: https://absolute-algorithmic.gitbook.io/character-dialogue-documentation/

## How to install
* Download a release [here](https://github.com/iAtog/character-dialogue/releases)
* Put the .jar file in your **/plugins** folder
* Add a NPC plugin to your **/plugins** folder
## Features
* Conversations with NPCs
  * Persistent conversations
  * Custom dialogue on first interaction with npc
  * NPC controlling
  * Multiple methods to build conversations
  * Choices
* Custom items to give/remove
* Hidden NPCs with conditions
* MiniMessage support
* PlaceholderAPI support
* Conditions inside dialogues
* Holograms (only HolographicDisplays and DecentHolograms)
* NPCs plugins support:
  * Citizens
  * ZNPCs Plus
  * FancyNPCs
## Compiling the project
Compiling the plugin will be troublesome, since you need the `ZNPCS Plus` plugin in the `libs` folder inside the project.
````shell
$ git clone https://github.com/iAtog/character-dialogue
$ cd ./character-dialogue
$ gradlew shadowJar
````
Download https://github.com/Pyrbu/ZNPCsPlus/releases/tag/2.0.0 and put it in ``/libs`` folder.

Then run ``gradlew shadowJar`` and the plugin will be in `/build`