# CharacterDialogue [![Codacy Badge](https://app.codacy.com/project/badge/Grade/488a2448b0b64650adaf6af431c696ee)](https://www.codacy.com/gh/iAtog/CharacterDialogue/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=iAtog/CharacterDialogue&amp;utm_campaign=Badge_Grade)
It is a plugin for SpigotMC/PaperMC that specializes in creating dynamic conversations with npcs, giving a dynamic experience to players.

Wiki: https://absolute-algorithmic.gitbook.io/character-dialogue-documentation/

## Changelog v0.9
### Dialogues Rework
Now you can configure the methods according to your taste, unlike before you can know what you are defining, there is no need to get confused with separators like `|` or others, here is an example with conditional:

#### Before
```
conditional: %player_name% == aatog | RUN_DIALOGUE: dialogue | STOP_SEND_MSG: &c[NPC] &b%npc_name%&f: Who are you?
```

#### After
```
conditional{condition="%player_name% == aatog", ifTrue="RUN_DIALOGUE: dialogue", ifFalse="STOP_SEND_MSG: &c[NPC] &b%npc_name%&f: Who are you?"}
```
We know in an orderly way what each thing is, thus improving your view for those who are creating the dialogs manually.

**Important fact**: If it is a text with spaces that is defined, you must use ' or “ to be detected, but for numbers or booleans they are not necessary.

Thanks to the configuration, internally the dialogs can handle default data, so for example if you put `talk: Hello!`, it will handle a default configuration without so much mess.
### New Methods
Some new methods have been added to increase the possibilities of potential dialogues.
#### Talk Method
Messages with writing animation, as if the npc is talking to you, supports hex colors.
Types:
* **action_bar**: Messages are sent to action bar.
* **message**: Messages are sent by chat.
* **full_chat**: The message is formed in the whole chat allowing to put long texts centered in the chat.

I recommend using only action_bar, since the feature that saves the chat has not been implemented.

Format:
```
talk{[sound], [volume], [pitch], [tickSpeed], [skippable]}: <message>
```
Example:
````
talk{sound=BLOCK_COMPOSTER_FILL}: Hello!
````
#### NPC Control Method
This method creates a new npc, a clone of the npc you were interacting with (you can choose another npc), this npc can 
be controlled, an important feature is that the npc that is created can only be seen by the player that is watching the 
dialog, the original npc is hidden only for the player.

For more information [read here](https://absolute-algorithmic.gitbook.io/character-dialogue-documentation/methods/npc-control-method).
## Method Adjustments
### Command & DispatchCommand
The ``command`` and ``dispatchcommand`` methods have been merged, they are now 1, they look like this:
```
command{sender=console}: gamemode creative %player_name%.
command{sender=player}: kit newbie
```
The sender is the one who will execute the command.
> As a result, **dispatchcommand** has been removed.

### Choices
The choices now have 3 types that can be shown 1 to the player.
* **CHAT**: It will show the options in the chat, allowing to choose them by clicking on them, or selecting them with the hotbar (pressing the numbers).
* **GUI**: Opens a menu that displays the options as items (heads), detailing the information provided.
* **BEDROCK_GUI**: It does the same as GUI, but, it only works if geyser and floodgate are on the server, with this, the bedrock players that are going to select, it will show them a form with the questions on the screen, allowing them to choose with the mouse or with the screen, if the player is not from bedrock, it will simply open the minecraft GUI.

Players have a time limit of 30 seconds to respond, if they do not respond, the dialog is canceled and tells them that they were late. You can set the length of the wait by adding `timeout=<seconds>` to the configuration.
Ex: `choice{timeout=120}: choice_sample`

### More plugins support
* [zNPCS Plus](https://www.spigotmc.org/resources/znpcsplus.109380/) support has been added.
* Support with [FancyNpcs](https://modrinth.com/plugin/fancynpcs) is being added.
* [Floodgate](https://geysermc.org/wiki/floodgate/) support for displaying forms to bedrock players