package ImojiBot.Roles;

import net.dv8tion.jda.core.entities.Role;

//An object that stores a role and its emoji to be saved and used within a ServerInstance.
public class RoleObject
{
	public String emoteMention;
	public String roleString;
	
	public RoleObject() {}
	
	public RoleObject(String emote, Role role)
	{
		this.emoteMention = emote;
		this.roleString = role.getName();
	}
	
	public RoleObject(String emote, String role)
	{
		this.emoteMention = emote;
		this.roleString = role;
	}
	
	public void SetEmote(String emote)
	{
		this.emoteMention = emote;
	}
	
	public void setRoleString(String role)
	{
		this.roleString = role;
	}
}
