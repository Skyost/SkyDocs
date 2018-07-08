package fr.skyost.skydocs.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.skyost.skydocs.DocsRunnable;

import java.io.InputStream;
import java.io.PrintStream;

/**
 *
 * Represents a command.
 *
 * @param <T> JCommander class arguments.
 */

public abstract class Command<T> extends DocsRunnable<Boolean> {

	/**
	 * JCommander class instance.
	 */

	private T arguments;

	/**
	 * Creates a new Command instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 * @param userArgs The user arguments.
	 * @param objectArgs The JCommander class instance.
	 * @param subTasks Command's sub tasks.
	 */

	Command(final PrintStream out, final InputStream in, final String[] userArgs, final T objectArgs, final DocsRunnable... subTasks) {
		super(out, in, subTasks);

		this.arguments = objectArgs;
		if(userArgs != null && userArgs.length > 0) {
			try {
				JCommander.newBuilder().addObject(objectArgs).build().parse(userArgs);
			}
			catch(final ParameterException ex) {
				ex.usage();
			}
			catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Gets the JCommander class instance.
	 *
	 * @return The JCommander class instance.
	 */

	public T getArguments() {
		return arguments;
	}

	/**
	 * Sets the JCommander class instance.
	 *
	 * @param arguments The JCommander class instance.
	 */

	public void setArguments(final T arguments) {
		this.arguments = arguments;
	}

}