package ImojiBot;

import ImojiBot.Commands.CommandList;
import ImojiBot.Commands.CommandUtil;
import ImojiBot.Commands.ReactTypes;
import ImojiBot.Commands.ReactionHandler;
import ImojiBot.Commands.ReactionMessage;
import ImojiBot.Roles.RoleObject;
import ImojiBot.Roles.ServerInstance;
import ImojiBot.Roles.ServerInstances;
import ImojiBot.Util.ConsoleLog;
import ImojiBot.Util.Responses;
import ImojiBot.Util.Util;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * 
 * @author Fiercest
 *
 */
public class Core extends ListenerAdapter
{
	//The ShardManager for sharding the bot to servers.
	public static ShardManager shardManager;
	public static JDA jda;

	//Initialize the Bot
	public static void main( String[] args)
	{
		try
		{
			//Instantiate the bot's core component
			Core core = new Core();

			//Instanitate Sharding Manager
			DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
			builder.setToken(Config.TOKEN);
			builder.addEventListeners(core);
			shardManager = builder.build();

			jda = new JDABuilder(Config.TOKEN).build();

			//Register the commands
			CommandList.RegisterCommands();

			//Load Server Instances
			ServerInstances.LoadAllServerInstances();

			//Update the members of all servers that this bot is connected to.
			shardManager.getShards().forEach(s -> {
				s.getGuilds().forEach(g -> {
					ServerInstance instance = ServerInstances.FindServerInstanceById(g.getIdLong());
					if(instance != null)
					{
						try
						{
							Util.UpdateRoles(g);
						}
						catch(HierarchyException he)
						{
							Responses.HierarchyError(g);
							he.printStackTrace();
							return;
						}
						catch(InsufficientPermissionException ip)
						{
							Responses.InsufficientPermissionError(g);
							ip.printStackTrace();
							return;
						}
					}
				});
			});
			ConsoleLog.Log("Updated Roles for All Servers");

			//Set status
			shardManager.setGame(Game.listening("~help"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	//Handles when a message is sent.
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		//If the message was sent by a bot, simply don't do anything.
		if(event.getMember().getUser().isBot())
			return;
		//Log the message to console and call the command which will handle if it is a command or not.
		CommandUtil.CallCommand(event, event.getMessage().getContentDisplay());
	}

	//Handles when a reaction is added.
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		//If the user is a bot that reacted, don't worry about it.
		if(event.getUser().isBot())
			return;
		//If the user is 'fake' or not an admin, remove the reaction and return.
		if(event.getUser().isFake())
		{event.getReaction().removeReaction(event.getUser()).queue(); return;}
		if(!Util.isAdmin(event.getMember()))
		{event.getReaction().removeReaction(event.getUser()).queue(); return;}

		//Get the message id and find the message from the cached messages in the reaction handler
		long id = event.getReaction().getMessageIdLong();
		event.getChannel().getMessageById(id).queue(t -> {
			int result = ReactionHandler.matchId(t.getIdLong());
			//If the message is not -1 (Which means there is a match).
			if(result != -1)
			{
				//Get the custom message object stored in the cache.
				ReactionMessage rm = ReactionHandler.messages.get(result);
				//If the type is a react message. E.g. the first message after role add, execute its continuation, then remove the cached message.
				if(rm.type == ReactTypes.React)
				{
					ReactionHandler.messages.get(result).continuation.execute(ReactionHandler.messages.get(result).event); //Execute the continuation
					ReactionHandler.removeMessageById(id);
				}
				//If the type is a confirmation. E.g. A check mark, check if it is the check mark or not, and if it is, continue with its execution.
				else if(rm.type == ReactTypes.Confirmation)
				{

					if(!event.getReactionEmote().getName().equals("✅"))
						event.getReaction().removeReaction().queue();
					else
					{
						ReactionHandler.messages.get(result).continuation.execute(ReactionHandler.messages.get(result).event); //Execute the continuation
						ReactionHandler.removeMessageById(id);
					}
				}
				//Placeholder that was not fully implemented as it wasn't needed
				/*else if(rm.type == ReactTypes.Specific)
				{
					List<MessageReaction> reactions = t.getReactions();
					if(!reactions.contains(event.getReaction()))
						event.getReaction().removeReaction().queue();
				}*/
			}
		});
	}

	//Handle Roles added or Removed 

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event)
	{
		//Store the member and their highest role.
		Member member = event.getMember();
		Role highestRole = Util.getHighestRole(member);

		//Get the stored server instance by the guild id.
		ServerInstance instance = ServerInstances.FindServerInstanceById(event.getGuild().getIdLong());
		if(instance == null) //If it doesnt exist, just return.
			return;

		if(instance.findMemberById(member.getUser().getIdLong()) != null)
			return;

		//For the roles on the guild.
		for(Role r : event.getGuild().getRoles())
		{			
			//If the current role is the highest role.
			if(highestRole.getName().equalsIgnoreCase(r.getName()))
			{
				try
				{
					RoleObject roleObject = instance.findRoleByName(highestRole.getName()); //Find the RoleObject that stores the emoji for the role.
					if(roleObject == null) //If it doesnt exist, set the user to the default name as the role they have been assigned does not have an emote.
					{
						event.getGuild().getController().setNickname(member, member.getUser().getName()).queue();
						return;
					}
					String emote = roleObject.emoteMention; //Get the emote from the RoleObject.		
					//Set the user's name
					event.getGuild().getController().setNickname(member, Util.SetNameWithEmote(member.getUser().getName(), emote, instance.location)).queue();
				}
				catch(HierarchyException he)
				{
					Responses.HierarchyError(event.getGuild());
					he.printStackTrace();
					return;
				}
				catch(InsufficientPermissionException ip)
				{
					Responses.InsufficientPermissionError(event.getGuild());
					ip.printStackTrace();
					return;
				}
			}
		}
	}

	//Same thing as above but instead of adding a role it determines the new icon-based nickname based on the new highest role.

	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event)
	{
		Member member = event.getMember();
		Role highestRole = Util.getHighestRole(member);

		ServerInstance instance = ServerInstances.FindServerInstanceById(event.getGuild().getIdLong());
		if(instance == null)
			return;

		if(instance.findMemberById(member.getUser().getIdLong()) != null)
			return;

		for(Role r : event.getGuild().getRoles())
		{
			if(highestRole == null)
			{
				event.getGuild().getController().setNickname(member, member.getUser().getName()).queue();
				return;
			}

			if(highestRole.getName().equalsIgnoreCase(r.getName()))
			{
				try
				{
					RoleObject roleObject = instance.findRoleByName(highestRole.getName());
					if(roleObject == null)
					{
						event.getGuild().getController().setNickname(member, member.getUser().getName()).queue();
						return;
					}
					String emote = roleObject.emoteMention;

					event.getGuild().getController().setNickname(member, Util.SetNameWithEmote(member.getUser().getName(), emote, instance.location)).queue();
				}
				catch(HierarchyException he)
				{
					Responses.HierarchyError(event.getGuild());
					he.printStackTrace();
				}
				catch(InsufficientPermissionException ip)
				{
					Responses.InsufficientPermissionError(event.getGuild());
					ip.printStackTrace();
				}
			}
		}
	}

	//When the bot joins a guild create a ServerInstance for it.
	@Override 
	public void onGuildJoin(GuildJoinEvent event)
	{
		ServerInstance instance = new ServerInstance(event.getGuild().getIdLong());
		ServerInstances.addServerInstance(instance);
		instance.SaveServer();
	}
}
