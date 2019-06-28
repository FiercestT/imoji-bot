package ImojiBot.Util;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import ImojiBot.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//A quick method of sending messages for certain events.
public class Responses 
{
	//The time before the message is removed
	private static int DeleteSeconds = 30;
	
	/**
	 * Sends a basic message.
	 * @param event The MessageReceivedEvent containing the channel to respond to.
	 * @param content The content of the message.
	 */
	public static void SendMessage(MessageReceivedEvent event, Object content)
	{
		event.getChannel().sendMessage(content.toString()).queue(e->{
			e.delete().queueAfter(DeleteSeconds, TimeUnit.SECONDS);
		});
	}
	
	/**
	 * Sends an embed message to a channel.
	 * @param event The MessageReceivedEvent containing the channel to respond to.
	 * @param title The title of the message.
	 * @param content The content of the message.
	 * @param color The color of the message.
	 */
	public static void SendQuickEmbed(MessageReceivedEvent event, Object title, Object content, Color color)
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(title.toString());
		eb.setDescription(content.toString());
		eb.setColor(color);
		event.getChannel().sendMessage(eb.build()).queue(e->{
			e.delete().queueAfter(DeleteSeconds, TimeUnit.SECONDS);
		});
	}
	
	/**
	 * Sends an embed message to a channel.
	 * @param channel The channel to send the message in.
	 * @param title The title of the message.
	 * @param content The content of the message.
	 * @param color The color of the message.
	 */
	public static void SendQuickEmbed(TextChannel channel, Object title, Object content, Color color)
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(title.toString());
		eb.setDescription(content.toString());
		eb.setColor(color);
		channel.sendMessage(eb.build()).queue(e->{
			e.delete().queueAfter(DeleteSeconds, TimeUnit.SECONDS);
		});
	}
	
	/**
	 * Sends a success message.
	 * @param event The MessageReceivedEvent containing the channel to respond to.
	 * @param content The content of the message.
	 */
	public static void SendSuccess(MessageReceivedEvent event, Object content)
	{
		SendQuickEmbed(event, "Success!", content, Color.green);
	}
	
	/**
	 * Sends an error
	 * @param event The MessageReceivedEvent containing the channel to respond to.
	 * @param content The content of the messaage.
	 */
	public static void SendError(MessageReceivedEvent event, Object content)
	{
		SendQuickEmbed(event, event.getMessage().getAuthor().getName() + ", You have an error!", content, Color.red);
	}
	
	/**
	 * Sends an error to the default channel of a guild.
	 * @param guild The guild to send the message to. Sends to the guild's default channel.
	 * @param content The content of the message.
	 */
	public static void SendErrorToDefaultChannel(Guild guild, Object content)
	{
		SendQuickEmbed(guild.getDefaultChannel(), "There was an error!", content.toString(), Color.red);
	}
	
	/**
	 * Sends an error due to InsufficientPermissions of the bot.
	 * @param event The MessageReceivedEvent containing the channel to respond to.
	 */
	public static void InsufficientPermissionError(MessageReceivedEvent event)
	{
		SendError(event, Config.INSUFFICIENT_PERMISSION_ERROR_MESSAGE);
		ConsoleLog.Log("Error Reported");
	}
	
	/**
	 * Sends an error due to InsufficientPermissions of the bot.
	 * @param guild The guild to send the message to. Sends to the guild's default channel.
	 */
	public static void InsufficientPermissionError(Guild guild)
	{
		SendErrorToDefaultChannel(guild, Config.INSUFFICIENT_PERMISSION_ERROR_MESSAGE);
		ConsoleLog.Log("Error Reported");
	}
	
	/**
	 * Sends an error due to Hierarchy Error of the bot.
	 * @param event The MessageReceivedEvent containing the channel to respond to.
	 */
	public static void HierarchyError(MessageReceivedEvent event)
	{
		SendError(event, Config.HIERARCHY_EXCEPTION);
		ConsoleLog.Log("Error Reported");
	}
	
	/**
	 * Sends an error due to Hierarchy Error of the bot.
	 * @param guild The guild to send the message to. Sends to the guild's default channel.
	 */
	public static void HierarchyError(Guild guild)
	{
		SendErrorToDefaultChannel(guild, Config.HIERARCHY_EXCEPTION);
		ConsoleLog.Log("Error Reported");
	}
}

