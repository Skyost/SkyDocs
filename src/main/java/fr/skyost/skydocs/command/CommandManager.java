package fr.skyost.skydocs.command;

import fr.skyost.skydocs.Constants;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This class allows to manage all app's commands : names, executor, ...
 */

public class CommandManager {

	/**
	 * Contains all app's commands.
	 */

	private final HashMap<String, CommandExecutor> commands = new HashMap<>();

	/**
	 * The default command executor (if no command is associated with the provided arguments, this will be executed instead).
	 */

	private CommandExecutor defaultExecutor;

	/**
	 * Creates a new command manager instance.
	 */

	public CommandManager() {
		this(args -> new HelpCommand(copyOfRangeIfPossible(args)).run(false));
	}

	/**
	 * Creates a new command manager instance.
	 *
	 * @param defaultExecutor The default command executor.
	 */

	public CommandManager(final CommandExecutor defaultExecutor) {
		register(args -> new NewCommand(copyOfRangeIfPossible(args)).run(), Constants.COMMAND_NEW);
		register(args -> new BuildCommand(true, copyOfRangeIfPossible(args)).run(), Constants.COMMAND_BUILD);
		register(args -> new ServeCommand(copyOfRangeIfPossible(args)).run(false), Constants.COMMAND_SERVE);
		register(args -> new UpdateCommand().run(false), Constants.COMMAND_UPDATE);
		register(args -> new GUICommand().run(false), Constants.COMMAND_GUI);

		this.defaultExecutor = defaultExecutor;
	}

	/**
	 * Executes the command that matches the given arguments.
	 *
	 * @param args The arguments.
	 */

	public final void execute(String... args) {
		if(args.length == 0) {
			args = new String[]{Constants.COMMAND_GUI};
		}
		if(args[0].startsWith("-")) {
			args[0] = args[0].substring(1);
		}

		execute(args[0], Arrays.copyOfRange(args, 1, args.length));
	}

	/**
	 * Finds a command by its name and executes it with the given arguments.
	 *
	 * @param command The name.
	 * @param args The arguments.
	 */

	public final void execute(final String command, final String... args) {
		getExecutor(command).execute(args);
	}

	/**
	 * Returns the executor that matches the specified command name.
	 *
	 * @param command The command name.
	 *
	 * @return The executor that matches the specified command name.
	 */

	public final CommandExecutor getExecutor(final String command) {
		final CommandExecutor executor = commands.get(command.toLowerCase());
		return executor == null ? defaultExecutor : executor;
	}

	/**
	 * Registers a command executor.
	 *
	 * @param executor The command executor.
	 * @param commands The associated commands.
	 */

	private void register(final CommandExecutor executor, final String... commands) {
		for(final String command : commands) {
			this.commands.put(command.toLowerCase(), executor);
		}
	}

	/**
	 * Returns the default command executor.
	 *
	 * @return The default command executor.
	 */

	public CommandExecutor getDefaultExecutor() {
		return defaultExecutor;
	}

	/**
	 * Sets the default command executor.
	 *
	 * @param defaultExecutor The default command executor.
	 */

	public void setDefaultExecutor(final CommandExecutor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}

	/**
	 * Copies all arguments from index 1 if possible.
	 *
	 * @param args The arguments.
	 *
	 * @return The copy.
	 */

	private static String[] copyOfRangeIfPossible(final String... args) {
		if(args == null || args.length == 0) {
			return args;
		}

		return Arrays.copyOfRange(args, 1, args.length);
	}

	/**
	 * Represents a command executor.
	 */

	@FunctionalInterface
	public interface CommandExecutor {

		/**
		 * Executes the given command with the specified arguments.
		 *
		 * @param args The arguments.
		 */

		void execute(final String... args);

	}

}