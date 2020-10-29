# Cashbot  

## The chatbot  

### Works with slack (and possibly other clients in the future)  

### Capabilities  

* Matching incoming messages against set up triggers  
* Running corresponding commands witch can execute functions  
* Running periodic tasks  

### Description  

Bot connects to your slack team, where you can add it to any channel.
It listens to messages and when the message matches any trigger of any command
it will execute this command and return the result to the chat.
You can manage commands using system commands that are already registered.  

Commands are loaded from (and saved to) `commands.yaml` file (can be changed in the config).
You can copy sample commands from `commands_sample.yaml` file.
You can add your commands directly to the file or by using system commands.  


### Set up instructions  

1. Copy the config sample file and name it `config.yaml`.  
2. Set client credentials in the config file.  
3. Build with `gradle clean build`.  
4. Launch with `java -jar build/libs/cashbot.jar `.  

### System commands  

* `list_functions` - Returns registered functions with its names, arguments and descriptions  
* `list_commands` - Returns registered commands with its ids, triggers and commands to execute  
* `find_command`- Searches a command that has its id or triggers matching the search term  
* `remove_command` - Removes command by id  
* `set_command` - Updates/Adds a command  

### Triggers  

* Triggers can consist of words and sentences  
* Triggers for one command are separated by `|`  
* Triggers can have parts that use regex expressions to pass matched result to a function  

Format examples:  
`how are you?`  
`hi|hello`  
`repeat (?<sentence>.+)|say (?<sentence>.+)`  

### Commands  

* Commands can be simple strings of text  
* Commands can call registered functions  
* Commands can pass matched parts of message to function or return them as a response  

Format Examples:  
`'Great!'`  
`choose('hello', 'hi', concat('hey you', repeat('!', random(4)))`  
`req('sentence')`  

### Tasks  

Tasks are loaded from `tasks.yaml` file (can be changed in the config).

* Tasks are executed periodically, the period is set in the file  
* Tasks run commands and return results to the main chat  


### Functions  

To add a new function instantiate you class from `Function` class,
and add it to `Wiring.functions` list before the initialization.  

------  

###### You may wonder why it's called cashbot? Because it definitely attracts money to each member of your chat, just try it.