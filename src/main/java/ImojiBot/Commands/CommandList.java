package ImojiBot.Commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ImojiBot.Config;
import ImojiBot.Commands.Modules.MemberModule;
import ImojiBot.Commands.Modules.RoleModule;
import ImojiBot.Roles.Locations;
import ImojiBot.Roles.MemberObject;
import ImojiBot.Roles.RoleObject;
import ImojiBot.Roles.ServerInstance;
import ImojiBot.Roles.ServerInstances;
import ImojiBot.Util.Responses;
import ImojiBot.Util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

/*
 * The big boy class of this bot.
 * The CommandList handles implementation of commands
 */

public class CommandList 
{
	//A list of the commands
	public static List<CommandObject> commands = new ArrayList<CommandObject>();

	//Initialize Commands to Tags. See the far bottom for a javadoc explanation of these methods. This was called in the main method.
	public static void RegisterCommands()
	{
		RegisterCommand("help", help, "Shows this message that you are reading!");
		RegisterCommand("settings", settings, "Configurate your server!");
		RegisterCommand("role add", RoleModule.role_add, "Add or replace an emoji to a role!", new String[] {"Role"});
		RegisterCommand("role remove", RoleModule.role_remove, "Remove an emoji from a role.", new String[] {"Role"});
		RegisterCommand("view", view, "View the icons associated with roles and members of your server.");
		RegisterCommand("member add", MemberModule.member_add, "Add or replace an emoji to a member. Member icons show instead of role icons. Note: simply type the user's name rather than an @.", new String[] {"MemberName"});
		RegisterCommand("member remove", MemberModule.member_remove, "Remove an emoji from a member. Note: simply type the user's name rather than an @.", new String[] {"MemberName"});
		RegisterCommand("update", update, "Updates roles for all members of your discord server! Use this if the bot was offline or if an icon didnt apply.");
	}

	//Define Commands

	//The update command. Updates names of all guild members.
	public static Command update = (MessageReceivedEvent e) -> {
		e.getMessage().delete().queue();

		if(!Util.isAdmin(e.getMessage().getMember())) //Only the owner can use this command
		{Responses.SendError(e, "You cannot use this command!"); return;}

		try
		{
			Util.UpdateRoles(e);
			Responses.SendSuccess(e, "Successfully updated all members!");
		}
		catch(HierarchyException he)
		{
			Responses.HierarchyError(e);
			he.printStackTrace();
		}
		catch(InsufficientPermissionException ip)
		{
			Responses.InsufficientPermissionError(e);
			ip.printStackTrace();
		}

	};

	//The help command. Posts a message with all the commands initialized above.
	public static Command help = (MessageReceivedEvent e) -> {
		e.getMessage().delete().queue();	
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Imoji Icon Handler");
		eb.setDescription("Here is a list of commands.");
		eb.setColor(Color.green);

		commands.forEach((i) -> {
			String arguments; 
			if(i.arguments != null)
				arguments = " " + Util.UnpackArray(i.arguments);
			else
				arguments = "";

			eb.addField(Config.PREFIX + i.name + arguments, i.description, true);
		});

		e.getChannel().sendMessage(eb.build()).queue(h -> {
			h.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);
		});
	};

	//A command to view all the roles and members that are stored in a ServerInstance for a guild.
	public static Command view = (MessageReceivedEvent e) ->
	{
		e.getMessage().delete().queue();
		//Get the server instance based on guild id.
		ServerInstance instance;
		long guildId = e.getGuild().getIdLong();

		if(ServerInstances.doesServerInstanceExist(guildId))
		{
			instance = ServerInstances.FindServerInstanceById(guildId);
		}
		else
		{
			instance = new ServerInstance(guildId);
			ServerInstances.addServerInstance(instance);
		}

		//Post all the roles and their emojis.
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Imoji Roles/Members");
		eb.setDescription("Here is a list of roles and members with their emojis for your server.");
		eb.setColor(Color.green);

		instance.roles.forEach((r) -> {
			eb.addField(r.roleString + ": " + r.emoteMention, "", true);
		});

		instance.members.forEach((m) -> {
			if(e.getGuild().isMember(e.getJDA().getUserById(m.userId)))
				eb.addField(e.getGuild().getMemberById(m.userId).getEffectiveName() + ": " + m.emoteMention, "", true);
			else
				instance.RemoveMember(m.userId);
		});

		if(instance.roles.size() == 0 && instance.members.size() == 0)
			eb.addField("🙄", "It's a little lonely down here...", true);

		e.getChannel().sendMessage(eb.build()).queue(r -> {
			r.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);
		});
	};

	//The settings command.
	public static Command settings = (MessageReceivedEvent e) -> 
	{
		e.getMessage().delete().queue();

		if(!Util.isAdmin(e.getMessage().getMember())) //Only the owner can use this command
		{Responses.SendError(e, "You cannot use this command!"); return;}

		//First get the server instance.
		ServerInstance instance;
		long guildId = e.getGuild().getIdLong();

		if(ServerInstances.doesServerInstanceExist(guildId))
		{
			instance = ServerInstances.FindServerInstanceById(guildId);
		}
		else
		{
			instance = new ServerInstance(guildId);
			ServerInstances.addServerInstance(instance);
		}

		//Intro
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Settings");
		eb.setDescription("Below you will find a bunch of settings. These will automatically remove after " + Config.SETTINGS_DELETE_TIME/60 + " minutes or when you press the checkmark all the way down there!");
		eb.setColor(Color.green);
		MessageEmbed em = eb.build();
		e.getChannel().sendMessage(em).queue(a -> {
			a.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);

			//Location
			EmbedBuilder eb1 = new EmbedBuilder();
			eb1.setTitle("Icon Location");
			eb1.setDescription("Where would you like the icon to be placed? (Current: " + instance.location + ").");
			eb1.addField("⬅", "Before Names", false);
			eb1.addField("➡", "After Names", false);
			eb1.addField("↔", "Before and After", false);
			eb1.setColor(Color.orange);
			MessageEmbed em1 = eb1.build();
			e.getChannel().sendMessage(em1).queue(b -> {
				b.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);

				b.addReaction("⬅").queue();
				b.addReaction("➡").queue();
				b.addReaction("↔").queue();

				//Finished
				EmbedBuilder eb3 = new EmbedBuilder();
				eb3.setTitle("Finished");
				eb3.setDescription("All done? Click the check mark below to close the settings menu! Or you can wait 5 minutes that works too!");
				eb3.setColor(Color.green);
				MessageEmbed em3 = eb3.build();
				e.getChannel().sendMessage(em3).queue(c -> {

					c.addReaction("✅").queue();
					e.getMessage().delete().queue();

					//Apply new settings.
					Command finished = (MessageReceivedEvent event) ->
					{								
						//Re-fetch the message as it's reactions have changed.
						b.getChannel().getMessageById(b.getIdLong()).queue(b1 -> {
							for(MessageReaction r : b1.getReactions())
								if(r.getCount() > 1)
									if(r.getReactionEmote().getName().equals("⬅"))
										instance.location = Locations.Before;
									else if(r.getReactionEmote().getName().equals("➡"))
										instance.location = Locations.After;
									else if(r.getReactionEmote().getName().equals("↔"))
										instance.location = Locations.Both;

							//Apply the settings.
							instance.SaveServer();

							//Delete all these when done.
							a.delete().queue();
							b.delete().queue();
							c.delete().queue();

							//Update all members based on new settings.
							try 
							{
								Util.UpdateRoles(e);
								Responses.SendSuccess(event, "Settings changed successfully!");
							}
							catch(HierarchyException he)
							{
								Responses.HierarchyError(event);
								he.printStackTrace();
								return;
							}
							catch(InsufficientPermissionException ip)
							{
								Responses.InsufficientPermissionError(event);
								ip.printStackTrace();
								return;
							}
						});
					};

					//Wait for a confirmation reaction on the message c before executing the finished implementation.
					ReactionMessage rm = new ReactionMessage(c, finished, e, ReactTypes.Confirmation);
					ReactionHandler.AddMessage(rm); //Add the message to the ReactionHandler's cache.

					c.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);
				});});});
	};

	private static int index = 0; //The index of commands
	/**
	 * Register a command to the commandlist so that they can be called by users.
	 * @param name The name of the command that will be recognized with the prefix. E.g. name = help. ~help will be recognized.
	 * @param implementation The implementation of the Command functional interface associated with this command.
	 * @param description A description of the command. Currently, the help command uses this description.
	 * @param arguments A String[] of the arguments that are part of the command. This just has to be the name that will appear as an argument after the command, usage of the argument is done within the Command implementation.
	 */
	private static void RegisterCommand(String name, Command implementation, String description, String[] arguments)
	{
		CommandObject obj = new CommandObject(name, implementation, description, arguments);
		commands.add(index, obj);
		index++;
	}
	/**
	 * Register a command to the commandlist so that they can be called by users. This overload has no arguments.
	 * @param name The name of the command that will be recognized with the prefix. E.g. name = help. ~help will be recognized.
	 * @param implementation The implementation of the Command functional interface associated with this command.
	 * @param description A description of the command. Currently, the help command uses this description.
	 */
	private static void RegisterCommand(String name, Command implementation, String description)
	{
		RegisterCommand(name, implementation, description, null);
	}
}