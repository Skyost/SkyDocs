package fr.skyost.skydocs.command;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.exception.LoadException;
import fr.skyost.skydocs.frame.ProjectsFrame;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;

public class CommandManager {

	private final HashMap<String, CommandExecutor> commands = new HashMap<>();
	private CommandExecutor defaultExecutor;

	public CommandManager(final CommandExecutor defaultExecutor) {
		register(args -> new NewCommand(Arrays.copyOfRange(args, 1, args.length)).run(), Constants.COMMAND_NEW);
		register(args -> {
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
		}, Constants.COMMAND_BUILD);
		register(args -> {
			final ServeCommand serveCommand = new ServeCommand(Arrays.copyOfRange(args, 1, args.length));
			serveCommand.setExitOnFinish(true);
			serveCommand.run();
		}, Constants.COMMAND_SERVE);
		register(args -> new UpdateCommand().run(), Constants.COMMAND_UPDATE);
		register(args -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				final ProjectsFrame projectsFrame = new ProjectsFrame();
				projectsFrame.setVisible(true);
				projectsFrame.checkForUpdates();
			}
			catch(final Exception ex) {
				JOptionPane.showMessageDialog(null, "Unable to start the GUI : " + ex.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}, Constants.COMMAND_GUI);

		this.defaultExecutor = defaultExecutor;
	}

	public void execute(String... args) {
		if(args.length == 0) {
			args = new String[]{Constants.COMMAND_GUI};
		}
		if(args[0].startsWith("-")) {
			args[0] = args[0].substring(1);
		}

		execute(args[0], Arrays.copyOfRange(args, 1, args.length));
	}

	public void execute(final String command, final String... commandArgs) {
		getExecutor(command).execute(commandArgs);
	}

	public CommandExecutor getExecutor(final String command) {
		final CommandExecutor executor = commands.get(command.toLowerCase());
		return executor == null ? defaultExecutor : executor;
	}

	private void register(final CommandExecutor executor, final String... commands) {
		for(final String command : commands) {
			this.commands.put(command.toLowerCase(), executor);
		}
	}

	public CommandExecutor getDefaultExecutor() {
		return defaultExecutor;
	}

	public void setDefaultExecutor(final CommandExecutor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}

	public interface CommandExecutor {

		void execute(final String... args);

	}

}