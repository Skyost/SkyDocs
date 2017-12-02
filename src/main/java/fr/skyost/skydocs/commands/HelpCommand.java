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
		super.run();
		String[] args = this.getArguments();
		if(args.length == 0) {
			args = new String[]{""};
		}
		switch(args[0].toLowerCase()) {
		case Constants.COMMAND_NEW:
			outputLine(Constants.COMMAND_NEW_SYNTAX);
			break;
		case Constants.COMMAND_BUILD:
			outputLine(Constants.COMMAND_BUILD_SYNTAX);
			break;
		case Constants.COMMAND_SERVE:
			outputLine(Constants.COMMAND_SERVE_SYNTAX);
			break;
		case Constants.COMMAND_UPDATE:
			outputLine(Constants.COMMAND_UPDATE_SYNTAX);
			break;
		case Constants.COMMAND_HELP:
			outputLine(Constants.COMMAND_HELP_SYNTAX);
			break;
		default:
			outputLine(Constants.APP_NAME + " " + Constants.APP_VERSION + " by " + Constants.APP_AUTHORS);
			outputLine("Commands :");
			for(final String command : new String[]{Constants.COMMAND_NEW_SYNTAX, Constants.COMMAND_BUILD_SYNTAX, Constants.COMMAND_SERVE_SYNTAX, Constants.COMMAND_UPDATE_SYNTAX, Constants.COMMAND_HELP_SYNTAX, Constants.COMMAND_GUI_SYNTAX}){
				outputLine("* " + command);
			}
			break;
		}
		exitIfNeeded();
	}

	@Override
	public final boolean isInterruptible() {
		return false;
	}
	
}