package ImojiBot.Commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

//Functional Interface for Creating Commands.
public interface Command
{
	//Used to execute the command or action that was implemented.
	void execute(MessageReceivedEvent event);
}
