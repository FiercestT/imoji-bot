package ImojiBot.Roles;

import net.dv8tion.jda.core.entities.Member;

//An object that stores a member and their emoji to be saved and used within a ServerInstance.
//Same as RoleObject just for a user
public class MemberObject
{
	public String emoteMention;
	public long userId;
	
	public MemberObject() {}
	
	public MemberObject(String emote, long userId)
	{
		this.emoteMention = emote;
		this.userId = userId;
	}
	
	public MemberObject(String emote, Member member)
	{
		this.emoteMention = emote;
		this.userId = member.getUser().getIdLong();
	}
	
	public void SetEmote(String emote)
	{
		this.emoteMention = emote;
	}
	
	public void setUserId(long id)
	{
		this.userId = id;
	}
}
