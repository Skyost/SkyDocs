package fr.skyost.skydocs;

import java.util.Arrays;

import fr.skyost.skydocs.commands.BuildCommand;
import fr.skyost.skydocs.commands.HelpCommand;
import fr.skyost.skydocs.commands.NewCommand;
import fr.skyost.skydocs.commands.ServeCommand;
import fr.skyost.skydocs.commands.UpdateCommand;

public class SkyDocs {
	
	public static final void main(String[] args) {
		if(args.length == 0) {
			args = new String[]{Constants.COMMAND_HELP};
		}
		if(args[0].startsWith("-")) {
			args[0] = args[0].substring(1);
		}
		switch(args[0].toLowerCase()) {
		case Constants.COMMAND_NEW:
			new NewCommand(Arrays.copyOfRange(args, 1, args.length)).run();
			break;
		case Constants.COMMAND_BUILD:
			final BuildCommand buildCommand = new BuildCommand(Arrays.copyOfRange(args, 1, args.length));
			buildCommand.setExitOnFinish(true);
			buildCommand.run();
			break;
		case Constants.COMMAND_SERVE:
			final ServeCommand serveCommand = new ServeCommand(Arrays.copyOfRange(args, 1, args.length));
			serveCommand.setExitOnFinish(true);
			serveCommand.run();
			break;
		case Constants.COMMAND_UPDATE:
			new UpdateCommand().run();
			break;
		default:
			new HelpCommand(Arrays.copyOfRange(args, 1, args.length)).run();
			break;
		}
	}
	
}