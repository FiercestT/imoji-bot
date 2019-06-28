# Imoji Bot! Discord Hack Week 2019

## What is It?
The Imoji Bot is a discord bot developed in java through the JDA library by one person for Discord Hack Week 2019. Imoji bot allows server admins to give specific guild members and members of a certain role, emoji icons before, after, or before and after their name. 

## How To Use
**Note: You must have administrator privileges to use this bot!**
Upon inviting the bot, you can type the following to show a list of commands.: 

> ~help

 You can add icons by using

> member add Name
> role add Name

Where Name is the name of role or user without an @ symbol. Follow the provided instructions from there.
If the bot has not updated roles properly, please type:

> ~update

## Installing
Simply clone this repo through git or download it as a zip via github. Exract the project and import it to your Eclipse Workspace (or whatever other IDE you use). 
*Apache Maven must be installed and the project compliance is set to JRE 1.8.*
From there, you can modify the bot and build it with maven. Please note that the bot is command line based and should be accompanied by a .bat file to run the jar with a console.

## Commands
Rather than using the regular command structure of the JDA library, I opted to use my own, it was a little more fun. How does it work?

Commands are Registered via the `ImojiBot.Commands.CommandList.java` class in the RegisterCommands method. Here is an example of registering a command.

    RegisterCommand("name", functional_interface_implementation, "description");	
    RegisterCommand("name", functional_interface_implementation, "description", new String[] {"argument1"});

 - The name is what is detected with the prefix. E.g. if a user types:
   > ~help
   
   This will be recognized as the command with the name argument simply
   set to "help".
 - The description is used by the help command to describe what a
   command does, and the String[] of arguments is used by the help
   command to provide arguments.

The functional_interface_implementation is the body of a command. This allows you to code the command whichever way you want. For instance: 

    //Register Command in the RegisterCommands method
    RegisterCommand("SayHello", say_hello, "The bot says hi!");
    
    //Define say_hello
    public static Command say_hello = (MessageReceivedEvent e) -> {
	   //Says "Hello!" in the channel that the command was received!
	   e.getChannel().sendMessage("Hello!").queue();
    };
    
This works similar to the built in system. However, arguments must be parsed manually within the implementation. Commands are caught in the onMessageReceived method in the Core class.

## Bot Permissions
Here is the following permissions string. Use administrator at your own discretion. The bot will notify the guild  if it lacks privileges.

    &permissions=1275591744

**Additionally, give the bot a role that is highest on the hierarchy so that it can edit nicknames of users!**
## Todo List

 1. Make bot add emoji and change name based on member nickname rather than global user name.
 2. Make icon location base off of specific role rather than a server-wide setting.

## Team
??Fiercest??#2681 | 399017078616621065

## Restrictions
The Imoji Bot does not work with custom emojis as placing these in the nickname of the user will exceed 32 characters, and will not work.

## Other
**If you choose to modify this bot and post it, please credit the originial work as a link to this repo. Thank you!**