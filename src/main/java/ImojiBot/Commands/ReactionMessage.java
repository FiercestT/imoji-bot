package ImojiBot.Commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//An object that stores a message for the cache in the ReactionHandler
public class ReactionMessage 
{
	public Message message;
	public Command continuation;
	public MessageReceivedEvent event;
	public ReactTypes type;
	
	public ReactionMessage(Message message, Command continuation, MessageReceivedEvent event, ReactTypes type)
	{
		this.message = message;
		this.continuation = continuation;
		this.event = event;
		this.type = type;
	}
}
