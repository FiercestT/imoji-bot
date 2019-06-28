package ImojiBot.Commands;

//An object that stores the data of a command created through the CommandList.RegisterCommand(...) method.
public class CommandObject 
{
	String name;
	Command implementation;
	String description;
	String[] arguments;
	
	public CommandObject(String name, Command implementation, String description, String[] arguments)
	{
		this.name = name;
		this.implementation = implementation;
		this.description = description;
		this.arguments = arguments;
	}
}
