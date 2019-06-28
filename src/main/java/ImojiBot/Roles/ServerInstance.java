package ImojiBot.Roles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

//An instance of a server containing its settings, and a list of RoleObjects for roles and their respective emotes, as well as the guildId and path to where these are serialized and saved.
public class ServerInstance 
{
	//The id of the guild this instance handles.
	public long guildId;
	//The list of RoleObjects of the guild.
	public List<RoleObject> roles = new ArrayList<RoleObject>();
	//The list of MemberObjects of the guild.
	public List<MemberObject> members = new ArrayList<MemberObject>();
	//The path to the file where this guild's data is stored
	public File path;
	//The location setting
	public Locations location = Locations.Before;
	
	public ServerInstance(long guildId)
	{
		this.guildId = guildId;
		this.path = new File("./Data/" + guildId);
	}
	
	/**
	 * Adds a MemberObject to the guild's ServerInstance. If the server has a that member with a MemberObject, it will be replaced.
	 * @param r The MemberObject that is added.
	 */
	public void AddMember(MemberObject m)
	{
		if(members == null)
		{
			members.add(m);
			return;
		}
		
		for(MemberObject member : members)
		{
			if(member.userId == m.userId)
			{
				members.remove(member);
				break;
			}
		}
		members.add(m);
	}
	
	/**
	 * Removes a MemberObject from the guild's ServerInstance based on its name.
	 * @param name The String name of the member that is being removed.
	 */
	public void RemoveMember(long id)
	{
		if(members == null)
			return;
		
		for(MemberObject member : members)
		{
			if(member.userId == id)
			{
				members.remove(member);
				break;
			}
		}
	}
	
	/**
	 * Adds a RoleObject to the guild's ServerInstance. If the server's roles already contains the role being added, it will be replaced.
	 * @param r The RoleObject that is added.
	 */
	public void AddRole(RoleObject r)
	{
		if(roles == null)
		{
			roles.add(r);
			return;
		}
		
		for(RoleObject role : roles)
		{
			if(role.roleString.equals(r.roleString))
			{
				roles.remove(role);
				break;
			}
		}
		roles.add(r);
	}
	
	/**
	 * Removes a RoleObject from the guild's ServerInstance based on its name.
	 * @param name The String name of the role that is being removed.
	 */
	public void RemoveRole(String name)
	{
		if(roles == null)
			return;
		
		for(RoleObject r : roles)
		{
			if(r.roleString.equals(name))
			{
				roles.remove(r);
				break;
			}
		}		
	}
	
	/**
	 * Finds a RoleObject based on the name of the role.
	 * @param name The role that is being searched for.
	 * @return A RoleObject of the role that was searched for. Null if it is not found.
	 */
	public RoleObject findRoleByName(String name)
	{
		if(roles == null)
			return null;
		
		for(RoleObject r : roles)
			if(r.roleString.equals(name))
				return r;
		return null;
	}
	
	/**
	 * Finds a MemberObject based on the name of the role.
	 * @param name The member that is being searched for.
	 * @return A MemberObject of the role that was searched for. Null if it is not found.
	 */
	public MemberObject findMemberById(long id)
	{
		if(members == null)
			return null;
		for(MemberObject m : members)
			if(m.userId == id)
				return m;
		return null;
	}
	
	/**
	 * Saves the ServerInstance in a serialized JSON file named after the GuildId.
	 */
	public void SaveServer()
	{
		if(!new File("./Data/").exists())
			new File("./Data/").mkdir();
		
		//Save to file
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(path, new SaveInstance(roles, members, location));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Loads the saved JSON file for this Guild based on its Id and stores the saved values into this object.
	 */
	public void LoadServer()
	{
		if(!new File("./Data/").exists())
			new File("./Data/").mkdir();
		
		//Load from file
		try
		{
			if(!path.exists())
			{
				SaveServer();
				return;
			}
			ObjectMapper mapper = new ObjectMapper();
			SaveInstance save = mapper.readValue(path, new TypeReference<SaveInstance>() {});
			this.roles = save.roles;
			this.location = save.location;
			this.members = save.members;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
