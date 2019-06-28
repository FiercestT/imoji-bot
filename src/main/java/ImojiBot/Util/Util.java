package ImojiBot.Util;

import java.util.List;

import ImojiBot.Roles.Locations;
import ImojiBot.Roles.MemberObject;
import ImojiBot.Roles.RoleObject;
import ImojiBot.Roles.ServerInstance;
import ImojiBot.Roles.ServerInstances;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

//A set of utilities
public class Util 
{
	/**
	 * Determines if a string is null or just a whitespace.
	 * @param input The String
	 * @return Whether the String has content or not.
	 */
	public static boolean IsNotNullOrWhitespace(String input)
	{
		if(input.contentEquals(" ") || input.isEmpty())
			return false;
		return true;
	}

	/**
	 * Unpacks the contents of a String array to a single String with spaces in between.
	 * @param input The String array.
	 * @return The single line String with spaces in between input elements.
	 */
	public static String UnpackArray(String[] input)
	{
		String out = "";
		for(int i = 0; i < input.length; i ++)
			out += input[i] + " ";
		return out.trim();
	}

	/**
	 * Determines the highest role of a member.
	 * @param member The member to find the highest role of.
	 * @return The highest Role of the Member. Returns null if there isn't one.
	 */
	public static Role getHighestRole(Member member)
	{
		List<Role> roles = member.getRoles();
		List<Role> serverRoles = member.getGuild().getRoleCache().asList();

		for(int i = 0; i < serverRoles.size(); i++)
		{
			for(int k = 0; k < roles.size(); k++)
			{
				if(serverRoles.get(i) == roles.get(k))
				{
					return serverRoles.get(i);
				}
			}
		}
		return null;
	}

	/**
	 * Applies an emote to a user's name.
	 * @param name The name of the user.
	 * @param emote The emote to be applied.
	 * @param location Where is the emote meant to be placed.
	 * @return The String of the user's name with the emote in its desired position.
	 */
	public static String SetNameWithEmote(String name, String emote, Locations location)
	{
		if(location == Locations.Before)
			return emote + name;
		else if (location == Locations.After)
			return name + emote;
		else if(location == Locations.Both)
			return emote + name + emote;
		else
			return name;
	}

	/**
	 * Updates the roles and icons of all members in a guild.
	 * @param guild The guild where all the members should be updated.
	 */
	public static void UpdateRoles(Guild guild) throws InsufficientPermissionException, HierarchyException
	{
		ServerInstance instance = ServerInstances.FindServerInstanceById(guild.getIdLong());

		for(Member m : guild.getMembers())
		{
			if(instance == null)
			{guild.getController().setNickname(m, m.getUser().getName()).queue(); return;}

			if(instance.roles != null && instance.members != null)
				if(instance.roles.size() == 0 && instance.members.size() == 0)
				{guild.getController().setNickname(m, m.getUser().getName()).queue(); return;}

			MemberObject mObj = instance.findMemberById(m.getUser().getIdLong());
			if(mObj != null)
			{
				guild.getController().setNickname(m, Util.SetNameWithEmote(m.getUser().getName(), mObj.emoteMention, instance.location)).queue();
			}
			else
			{
				for(RoleObject r : instance.roles)
				{	
					Role highestRole = Util.getHighestRole(m); 
					if(highestRole != null)
						if(highestRole.getName().equals(r.roleString) && !m.isOwner())
							guild.getController().setNickname(m, Util.SetNameWithEmote(m.getUser().getName(), r.emoteMention, instance.location)).queue();
				}
			}
		}
	}

	/**
	 * Updates the roles and icons of all members in a guild.
	 * @param e The MessageReceivedEvent where the guild can be obtained.
	 */
	public static void UpdateRoles(MessageReceivedEvent e)
	{
		UpdateRoles(e.getGuild());
	}

	/**
	 * Determines if the user has administrator privileges or not.
	 * @param m The Member.
	 * @return If the user has administrator privileges or not.
	 */
	public static boolean isAdmin(Member m)
	{
		return m.hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR);

	}
}
