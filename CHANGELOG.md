## Changelog v0.9
### Dialogues Rework
Now you can configure the methods according to your taste, unlike before you can know what you are defining, there is no need to get confused with separators like `|` or others, here is an example with conditional:

#### Before
```yaml
conditional: %player_name% == aatog | RUN_DIALOGUE: dialogue | STOP_SEND_MSG: &c[NPC] &b%npc_name%&f: Who are you?
```

#### After
```yaml
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
```yaml
talk{[sound], [volume], [pitch], [tickSpeed], [skippable]}: <message>
```
Example:
````yaml
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
````yaml
command{sender=console}: gamemode creative %player_name%.
command{sender=player}: kit newbie
````
The sender is the one who will execute the command.
> As a result, **dispatchcommand** has been removed.

### Choices
The choices now have 3 types that can be shown 1 to the player.
* **CHAT**: It will show the options in the chat, allowing to choose them by clicking on them, or selecting them with the hotbar (pressing the numbers).
* **GUI**: Opens a menu that displays the options as items (heads), detailing the information provided.
* **BEDROCK_GUI**: It does the same as GUI, but, it only works if geyser and floodgate are on the server, with this, the bedrock players that are going to select, it will show them a form with the questions on the screen, allowing them to choose with the mouse or with the screen, if the player is not from bedrock, it will simply open the minecraft GUI.

Players have a time limit of 30 seconds to respond, if they do not respond, the dialog is canceled and tells them that they were late. You can set the length of the wait by adding `timeout=<seconds>` to the configuration.
Ex: `choice{timeout=120}: choice_sample`

### Visibility of NPCs with conditions
Conditions can be added to ensure that an npc is shown to a player.
* Only available with packet-based npcs plugins.

in config.yml:
````yaml
hidden-npcs:
  '92':
    # Only needs one condition with 'true' to hide the npc
    type: "one"
    conditions:
    - '%characterdialogue_readedFirstInteraction:alexherman% == no'
    - '%characterdialogue_readed:alex2% == yes'
  # Type default is: all
  # If type is "all", all conditions must be true to hide the npc
  alexbrother:
    conditions:
    - '%characterdialogue_readedFirstInteraction:alexherman% == yes'
````
### Placeholders
Added 3 placeholders:
* `%characterdialogue_readed:<dialogue>%` - returns `yes` or `no` if the player has read the specified dialogue.
* `%characterdialogue_readedFirstInteraction:<dialogue>%` - returns `yes` or `no` if the player has already read the first interaction of the dialogue.
* `%characterdialogue_readed_size:<type>` - <type> has 2 options: `finished` and `firstInteractions`.

Example:
* ``%characterdialogue_readed_size:finished%`` returns `6`
* ``%characterdialogue_readed:myDialogue%`` returns `no`/`yes`

### Persistent dialogues
It will save the following: dialogueName and currentIndex

When the player joins the server, the dialog will be executed where it was, if it is in a method like “conditional_events” it will work perfectly.
Persistence will have:
* New section that executes method lines when the player enters the server.
* A `persistent` value to the dialog to define if it will be persistent.
* Persistent dialogs will remove the information related to the player when the player enters the server, when the player exits the session will be stored.
* The `on-join` value will be executed when you enter, allowing you to reset things like `npc_control` or give you a reminder that you are in a dialog or rather `mission`.

### Recordings
Now you can make recordings, these recordings are used to make an npc move to accuracy as you recorded them.
Using `/characterd record start <name>` will start a 5 seconds countdown and start recording, in the action bar it will show you a text saying that you can stop the recording by pressing “f” (or whatever button you use to change the offhand) and the elapsed time.
#### Operation
When starting to record, the movements are saved precisely (each tick), this could generate lag at the time of recording.

#### Commands
`/characterd record start <name>` - Starts a new recording. <br>
`/characterd record stop` - Stops the current recording (works the same as pressing **F**). <br>
`/characterd record cancel` - Stops the current recording, and does not save it.
`/characterd record view <name>` - Displays some data of the recording and the time it lasts <br>
`/characterd record replay <name> <npcId>` - Plays a recording to an npc, it is for testing purposes, it does not create an npc from 0, so it will not teleport the npc to its original location. <br>
`/characterd record delete <name>` - Deletes a record permanently.

These recordings can be used in the `npc_control` method, bringing npcs to life.
### More plugins support
* [zNPCS Plus](https://www.spigotmc.org/resources/znpcsplus.109380/) support has been added.
* [FancyNpcs](https://modrinth.com/plugin/fancynpcs) support has been added.
* [Floodgate](https://geysermc.org/wiki/floodgate/) support for displaying forms to bedrock players