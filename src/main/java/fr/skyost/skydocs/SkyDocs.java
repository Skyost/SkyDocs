package fr.skyost.skydocs;

import java.util.Arrays;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.skyost.skydocs.commands.BuildCommand;
import fr.skyost.skydocs.commands.HelpCommand;
import fr.skyost.skydocs.commands.NewCommand;
import fr.skyost.skydocs.commands.ServeCommand;
import fr.skyost.skydocs.commands.UpdateCommand;
import fr.skyost.skydocs.commands.frames.ProjectsFrame;
import fr.skyost.skydocs.exceptions.LoadException;

public class SkyDocs {
	
	public static final void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		if(args.length == 0) {
			args = new String[]{Constants.COMMAND_GUI};
		}
		if(args[0].startsWith("-")) {
			args[0] = args[0].substring(1);
		}
		switch(args[0].toLowerCase()) {
		case Constants.COMMAND_NEW:
			new NewCommand(Arrays.copyOfRange(args, 1, args.length)).run();
			break;
		case Constants.COMMAND_BUILD:
			try {
				final BuildCommand buildCommand = new BuildCommand(true, Arrays.copyOfRange(args, 1, args.length));
				buildCommand.setExitOnFinish(true);
				buildCommand.run();
			}
			catch(final LoadException ex) {
				System.out.println();
				System.out.println("Cannot load the project from the specified directory !");
				ex.printStackTrace();
			}
			break;
		case Constants.COMMAND_SERVE:
			final ServeCommand serveCommand = new ServeCommand(Arrays.copyOfRange(args, 1, args.length));
			serveCommand.setExitOnFinish(true);
			serveCommand.run();
			break;
		case Constants.COMMAND_UPDATE:
			new UpdateCommand().run();
			break;
		case Constants.COMMAND_GUI:
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new ProjectsFrame().setVisible(true);
			break;
		default:
			new HelpCommand(Arrays.copyOfRange(args, 1, args.length)).run();
			break;
		}
	}
	
}