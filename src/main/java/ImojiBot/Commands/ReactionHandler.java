package ImojiBot.Commands;

import java.util.ArrayList;
import java.util.List;

//The class that handles reactions on messages for commands
public class ReactionHandler 
{
	//A list (or cache) of the messages that are awaiting reaction.
	public static List<ReactionMessage> messages = new ArrayList<ReactionMessage>();
	
	/**
	 * Addss a ReactionMessage to the cache.
	 * @param m The Message to be added.
	 */
	public static void AddMessage(ReactionMessage m)
	{
		messages.add(m);
	}
	
	/**
	 * Matches the id of a message to a message in the cache.
	 * @param id The messageId as a long.
	 * @return The index of the message in the 'messages' list. -1 if it doesn't exist.
	 */
	public static int matchId(long id)
	{
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).message.getIdLong() == id)
				return i;
		return -1;
	}
	
	/**
	 * Removes a message from the cache based on its id.
	 * @param id The messageId as a long.
	 */
	public static void removeMessageById(long id)
	{
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).message.getIdLong() == id)
				messages.remove(i);
	}
}
