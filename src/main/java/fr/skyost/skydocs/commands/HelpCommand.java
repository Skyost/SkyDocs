package fr.skyost.skydocs.commands;

import fr.skyost.skydocs.Constants;

/**
 * "help" command.
 */

public class HelpCommand extends Command {
	
	public HelpCommand(final String... args) {
		super(args);
	}
	
	@Override
	public final void run() {
		String[] args = this.getArguments();
		if(args.length == 0) {
			args = new String[]{""};
		}
		switch(args[0].toLowerCase()) {
		case Constants.COMMAND_NEW:
			System.out.println(Constants.COMMAND_NEW_SYNTAX);
			break;
		case Constants.COMMAND_BUILD:
			System.out.println(Constants.COMMAND_BUILD_SYNTAX);
			break;
		case Constants.COMMAND_SERVE:
			System.out.println(Constants.COMMAND_SERVE_SYNTAX);
			break;
		case Constants.COMMAND_UPDATE:
			System.out.println(Constants.COMMAND_UPDATE_SYNTAX);
			break;
		case Constants.COMMAND_HELP:
			System.out.println(Constants.COMMAND_HELP_SYNTAX);
			break;
		default:
			System.out.println(Constants.APP_NAME + " " + Constants.APP_VERSION + " by " + Constants.APP_AUTHORS);
			System.out.println("Commands :");
			for(final String command : new String[]{Constants.COMMAND_NEW_SYNTAX, Constants.COMMAND_BUILD_SYNTAX, Constants.COMMAND_SERVE_SYNTAX, Constants.COMMAND_UPDATE_SYNTAX, Constants.COMMAND_HELP_SYNTAX}){
				System.out.println("* " + command);
			}
			break;
		}
		super.run();
	}
	
}