package ImojiBot.Commands.Modules;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ImojiBot.Config;
import ImojiBot.Commands.Command;
import ImojiBot.Commands.ReactTypes;
import ImojiBot.Commands.ReactionHandler;
import ImojiBot.Commands.ReactionMessage;
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
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class RoleModule 
{
	//The command for adding role icons.
	public static Command role_add = (MessageReceivedEvent e) -> {
		e.getMessage().delete().queue();

		if(!Util.isAdmin(e.getMessage().getMember())) //Only the owner can use this command
		{Responses.SendError(e, "You cannot use this command!"); return;}

		String message = e.getMessage().getContentDisplay(); //The Message.
		String args = message.replace("~role add", "").trim(); //The arguments of the message (Role).
		if(!Util.IsNotNullOrWhitespace(args)) //If the arguments are blank, give an error and return.
		{Responses.SendError(e, "Invalid arguments!"); return;}

		if(args.contains("@everyone") || args.contains("@here")) //If the role isn't @everyone or @here.
		{Responses.SendError(e, "This is not a valid role!"); return;}

		List<Role> rolesByName = e.getGuild().getRolesByName(args, true); //Get the roles by their name.
		if(rolesByName.size() > 1) //If there are more than 1 role by that name, return an error because we don't want that.
		{Responses.SendError(e, "There are multiple roles with this name!"); return;}
		else if (rolesByName.size() == 0) //If there are no roles by that name, return an error.
		{Responses.SendError(e, "This role doesn't exist!"); return;}

		MessageBuilder mb = new MessageBuilder(); //Create a message saying to react with an emoji to assign to the role.
		mb.setContent(e.getMember().getAsMention() + ", React with an Emoji on this message to set the Icon of the role **" + rolesByName.get(0).getName() + "**");
		Message m = mb.build();

		e.getChannel().sendMessage(m).queue(t ->
		{
			//Once the reaction has been added
			Command role_add_continue = (MessageReceivedEvent event) -> {
				//Fetch the message id where the reaction was added.
				long messageId = t.getIdLong();
				event.getChannel().getMessageById(messageId).queue(reactMessage -> {
					//Get the emote that was added
					ReactionEmote emote = reactMessage.getReactions().get(0).getReactionEmote();

					t.delete().queue();

					//If the emote is a custom emote, it cannot be added to nicknames as the implementation of an emote <name:id> is too long for the max 32 of a nickname.
					if(emote.isEmote())
					{
						Responses.SendError(event, "Custom Emojis are unfortunately not supported by the Imoji bot due to api restrictions."); 
						return;
					}

					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Add Role Icon?");
					eb.setDescription("Are you sure you would like to add the icon" + emote.getName() +" to the role **" + rolesByName.get(0).getName() + "**?");
					eb.setColor(Color.orange);
					MessageEmbed em = eb.build();

					event.getChannel().sendMessage(em).queue(t1 -> {
						t1.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);

						t1.addReaction("✅").queue();

						//Once the user has confirmed that they want to add the role
						Command role_add_finalize = (MessageReceivedEvent event1) -> {
							t1.delete().queue();

							//Save the role to the instance/create it and add it if it didnt exist.
							long guildId = event1.getGuild().getIdLong();
							ServerInstance instance;
							if(ServerInstances.doesServerInstanceExist(guildId))
							{
								instance = ServerInstances.FindServerInstanceById(guildId);
								instance.AddRole(new RoleObject(emote.getName(), args));
								instance.SaveServer();
							}
							else
							{
								instance = new ServerInstance(guildId);
								instance.AddRole(new RoleObject(emote.getName(), args));
								instance.SaveServer();
								ServerInstances.addServerInstance(instance);
							}

							//For every member with the role that was applied
							for(Member member : event1.getGuild().getMembersWithRoles(event1.getGuild().getRolesByName(args, true)))
							{	
								if(instance.findMemberById(member.getUser().getIdLong()) != null) 
									continue;

								//Get their highest role.
								if(Util.getHighestRole(member).getName().equalsIgnoreCase(args) && !member.isOwner())
								{
									try
									{
										//Apply their new icons
										event1.getGuild().getController().setNickname(member, Util.SetNameWithEmote(member.getUser().getName(), emote.getName(), instance.location)).queue();;
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
								}
							}
							//Then send a success report
							Responses.SendSuccess(event1, "Successfully added icons to the role **" + args + "**!");
						};
						//The second reaction-based message to confirm adding the icon to the role.
						ReactionMessage rm = new ReactionMessage(t1, role_add_finalize, event, ReactTypes.Confirmation);
						ReactionHandler.AddMessage(rm);
					});});
			}; 
			//The first reaction-based message to get the icon the user wanted to add to the role.
			ReactionMessage rm = new ReactionMessage(t, role_add_continue, e, ReactTypes.React);
			ReactionHandler.AddMessage(rm);
		});};

		//The command to remove an icon from a role.
		public static Command role_remove = (MessageReceivedEvent e) -> {
			e.getMessage().delete().queue();

			if(!Util.isAdmin(e.getMessage().getMember())) //Only the owner can use this command
			{Responses.SendError(e, "You cannot use this command!"); return;}

			String message = e.getMessage().getContentDisplay();
			String arg = message.substring(1).replace("role remove", "").trim();

			if(!Util.IsNotNullOrWhitespace(arg)) //If the arguments are blank
			{Responses.SendError(e, "Invalid arguments!"); return;}

			if(arg.contains("@everyone") || arg.contains("@here")) //If the role isn't @everyone or @here
			{Responses.SendError(e, "This is not a valid role!"); return;}

			List<Role> rolesByName = e.getGuild().getRolesByName(arg, true); //Get the roles by their name
			if(rolesByName.size() > 1) //If there are more than 1 role by that name, return an error because we don't want that.
			{Responses.SendError(e, "There are multiple roles with this name!"); return;}
			else if (rolesByName.size() == 0) //If there are no roles by that name, return an error
			{Responses.SendError(e, "This role doesn't exist!"); return;}

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Remove Role Icon?");
			eb.setDescription("Are you sure you would like to remove the icon from the role **" + rolesByName.get(0) + "**?");
			eb.setColor(Color.red);
			MessageEmbed em = eb.build();
			e.getChannel().sendMessage(em).queue(m -> {
				m.delete().queueAfter(Config.SETTINGS_DELETE_TIME, TimeUnit.SECONDS);

				m.addReaction("✅").queue();

				//When the user confirms to remove the role.
				Command remove_finalize = (MessageReceivedEvent event) -> {
					m.delete().queue();

					//Get the server instance based on guild id.
					long guildId = event.getGuild().getIdLong();
					ServerInstance instance = ServerInstances.FindServerInstanceById(guildId);
					if(instance == null) //If there is no server instance, no roles exist for the guild.
						Responses.SendError(event, "No icon exists for this role yet!");
					else
					{
						//If the role cannot be found in the server instance, it does not exist
						if(ServerInstances.FindServerInstanceById(guildId).findRoleByName(arg) == null)
							Responses.SendError(event, "No icon exists for this role yet!");
						else
						{
							//If it exists, remove it from the instance and save.
							instance.RemoveRole(arg);
							instance.SaveServer();
						}
					}

					//Update all users where the roles were removed to their base name.
					for(Member member : event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName(arg, true)))
					{
						if(instance.findMemberById(member.getUser().getIdLong()) != null)
							continue;

						if(Util.getHighestRole(member).getName().equalsIgnoreCase(arg) && !member.isOwner())
						{
							try
							{
								event.getGuild().getController().setNickname(member, member.getUser().getName()).queue();;
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
						}

					}
					Responses.SendSuccess(event, "Successfully removed icons from the role **" + arg + "**");
				};
				//The confirmation of the message that asks if the user is sure they want to remove the icon.
				ReactionMessage rm = new ReactionMessage(m, remove_finalize, e, ReactTypes.Confirmation);
				ReactionHandler.AddMessage(rm);
			});
		};
}
