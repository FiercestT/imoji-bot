package ImojiBot;

public class Config 
{
	//Invite: https://discordapp.com/api/oauth2/authorize?client_id=592774233453494272&permissions=8&scope=bot
	
	//The Bot Token.
	public static final String TOKEN = "bot token here"; 
	
	//The Command Prefix.
	public static final String PREFIX = "~"; 
	
	//The time to delete messages using this field. Mainly bot settings messages.
	public static final int SETTINGS_DELETE_TIME = 60*3; 
	
	//The strings to notify users that the bot has insufficient permissions.
	public static final String INSUFFICIENT_PERMISSION_ERROR_MESSAGE = "Imoji Bot has insufficient permissions to set nicknames or delete messages, please add that now. Thank you!";
	public static final String HIERARCHY_EXCEPTION = "Imoji Bot is not at the top of your role hierarchy. In order to work, please move it to the top. Thank you!";
}

