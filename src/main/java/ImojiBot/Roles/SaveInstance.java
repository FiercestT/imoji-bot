package ImojiBot.Roles;

import java.util.List;

//An object that is used to store the roles and settings of a ServerInstance when it is serialized and saved.
public class SaveInstance
{
	public SaveInstance() {}
	
	public List<RoleObject> roles;
	public List<MemberObject> members;
	public Locations location;
	
	public SaveInstance(List<RoleObject> roles, List<MemberObject> members, Locations location)
	{
		this.roles = roles;
		this.location = location;
		this.members = members;
	}
}
