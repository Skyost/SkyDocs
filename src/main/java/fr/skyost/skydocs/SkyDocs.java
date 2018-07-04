package fr.skyost.skydocs;

import fr.skyost.skydocs.command.CommandManager;
import fr.skyost.skydocs.command.HelpCommand;

import java.util.Arrays;

/**
 * Executable class of SkyDocs.
 */

public class SkyDocs {
	
	/**
	 * Main method of SkyDocs.
	 * 
	 * @param args Arguments to pass.
	 */
	
	public static void main(String[] args) {
		new CommandManager(arguments -> new HelpCommand(Arrays.copyOfRange(arguments, 1, arguments.length)).run()).execute(args);
	}
	
}