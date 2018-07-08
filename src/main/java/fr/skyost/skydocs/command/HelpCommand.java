package fr.skyost.skydocs.command;

import com.beust.jcommander.Parameter;
import fr.skyost.skydocs.Constants;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * "help" command.
 */

public class HelpCommand extends Command<HelpCommand.Arguments> {

	/**
	 * Creates a new Command instance.
	 *
	 * @param args User arguments.
	 */

	public HelpCommand(final String... args) {
		this(System.out, System.in, args);
	}

	/**
	 * Creates a new Command instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 * @param args User arguments.
	 */

	public HelpCommand(final PrintStream out, final InputStream in, final String... args) {
		super(out, in, args, new Arguments());
	}

	@Override
	protected Boolean execute() {
		switch(this.getArguments().command) {
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
		case Constants.COMMAND_GUI:
			outputLine(Constants.COMMAND_GUI_SYNTAX);
			break;
		default:
			outputLine(Constants.APP_NAME + " " + Constants.APP_VERSION + " by " + Constants.APP_AUTHORS);
			outputLine("Commands :");
			for(final String command : new String[]{Constants.COMMAND_NEW_SYNTAX, Constants.COMMAND_BUILD_SYNTAX, Constants.COMMAND_SERVE_SYNTAX, Constants.COMMAND_UPDATE_SYNTAX, Constants.COMMAND_HELP_SYNTAX, Constants.COMMAND_GUI_SYNTAX}){
				outputLine("* " + command);
			}
			break;
		}

		return true;
	}

	/**
	 * Command arguments.
	 */

	public static class Arguments {

		@Parameter(names = {"command", "c"}, description = "Shows the help of a specific command.")
		public String command = "";

	}
	
}