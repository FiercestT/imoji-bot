package ImojiBot.Commands;

import ImojiBot.Config;
import ImojiBot.Util.ConsoleLog;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//Utilities and methods to call and work with commands
public class CommandUtil 
{
	/**
	 * Determines if a command from the CommandList exists or not, and returns its location.
	 * @note This method only works for commands with a max of one space.
	 * @param commandName The input that was entered to determine if it is a command or not.
	 * @return The index of the command. -1 if it doesn't exist.
	 */
	public static int CommandExists(String commandName)
	{
		if(!IsCommand(commandName))
			return -1;
	
		if(commandName.trim().contains(" "))
			commandName = commandName.split(" ")[0] + " " + commandName.split(" ")[1];
		
		for(int i = 0; i < CommandList.commands.size(); i++)
			if(CommandList.commands.get(i).name.equalsIgnoreCase(CleanCommandInput(commandName)))
				return i;
		
		return -1;
	}

	/**
	 * Executes a Command implementation if the command exists.
	 * @param event The MessageReceivedEvent where the command was submitted.
	 * @param commandName The name of the command submitted.
	 */
	public static void CallCommand(MessageReceivedEvent event, String commandName)
	{
		//If the command actually exists Get the index based on the name.
		int index = CommandExists(commandName);
		if(index != -1)
		{
			ConsoleLog.LogMessageEvent(event);
			//Then execute the command
			CommandList.commands.get(index).implementation.execute(event);
		}
	}

	/**
	 * Is the string that was submitted a command or not.
	 * @param command The string that is being checked whether it is a command or not.
	 * @return Whether the string is in fact a command or not.
	 */
	public static boolean IsCommand(String command)
	{
		return command.startsWith(Config.PREFIX);
	}

	/**
	 * Cleans the String input of a command.
	 * @param input The command's input
	 * @return A cleaned string that is trimmed, and does not have the command prefix.
	 */
	public static String CleanCommandInput(String input)
	{
		if(input.startsWith(Config.PREFIX))
			return input.substring(1).trim();
		return input.trim();
	}
}
