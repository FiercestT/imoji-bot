package ImojiBot.Roles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ImojiBot.Util.ConsoleLog;

//The manager that stores all active ServerInstances
public class ServerInstances 
{
	//A list of all the ServerInstances
	public static List<ServerInstance> serverInstances = new ArrayList<ServerInstance>();
	
	/**
	 * Adds a ServerInstance to the cache.
	 * @param si The ServerInstance to be added.
	 */
	public static void addServerInstance(ServerInstance si)
	{
		serverInstances.add(si);
	}
	
	/**
	 * Gets the List of ServerInstances.
	 * @return The List of all ServerInstances
	 */
	public static List<ServerInstance> getAllServerInstances()
	{
		return serverInstances;
	}
	
	/**
	 * Finds a ServerInstance by its GuildId
	 * @param id The Guild's Id that the ServerInstance belongs to.
	 * @return The ServerInstance belonging to the Guild. Null if none are found.
	 */
	public static ServerInstance FindServerInstanceById(long id)
	{
		for(ServerInstance si : serverInstances)
			if(si.guildId == id)
				return si;
		return null;
	}
	
	/**
	 * Determines if a ServerInstance exists for the Guild Id
	 * @param id The Id of the guild.
	 * @return Whether the ServerInstance is in the cache or not.
	 */
	public static boolean doesServerInstanceExist(long id)
	{
		for(ServerInstance si : serverInstances)
			if(si.guildId == id)
				return true;
		return false;
	}
	
	/**
	 * Loads all server instances from data into the list and initializes their respective objects.
	 */
	public static void LoadAllServerInstances()
	{
		for(File server : Arrays.asList(new File("./Data/").listFiles()))
		{			
			ServerInstance instance = new ServerInstance(Long.parseLong(server.getName()));
			instance.LoadServer();
			addServerInstance(instance);
		}
		ConsoleLog.Log("Loaded All Server Instances");
	}
}
