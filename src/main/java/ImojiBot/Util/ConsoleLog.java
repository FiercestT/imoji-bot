package ImojiBot.Util;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//A utility to log messages
public class ConsoleLog 
{
	/**
	 * Logs a message that was received based on it's ChannelType
	 * @param event The MessageReceivedEvent of the message.
	 */
	public static void LogMessageEvent(MessageReceivedEvent event)
	{
		if(event.isFromType(ChannelType.PRIVATE))
			System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(), event.getMessage().getContentDisplay());
		else
			if(Util.IsNotNullOrWhitespace(event.getGuild().getName()))
				System.out.printf("[%s] %s: %s\n", event.getGuild().getName(), event.getAuthor().getName(), event.getMessage().getContentDisplay());
			else
				System.out.printf("[Unknown] %s: %s\n", event.getAuthor().getName(), event.getMessage().getContentDisplay());
	}
	
	/**
	 * Logs a message to console.
	 * @param message The content of the message.
	 */
	public static void Log(Object message)
	{
		System.out.println("[LOG] " + message);
	}
}
