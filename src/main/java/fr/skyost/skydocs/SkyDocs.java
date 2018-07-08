package fr.skyost.skydocs;

import fr.skyost.skydocs.command.CommandManager;

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
		new CommandManager().execute(args);
	}
	
}