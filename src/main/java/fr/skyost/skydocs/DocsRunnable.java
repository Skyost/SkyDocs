package fr.skyost.skydocs;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Represents a runnable.
 *
 * @param <T> The return type. Null is treated as an error.
 */

public abstract class DocsRunnable<T> {

	/**
	 * Contains all runnable listeners.
	 */

	private final HashSet<RunnableListener> listeners = new HashSet<>();

	/**
	 * The output stream.
	 */

	private PrintStream out;

	/**
	 * The input scanner.
	 */

	private Scanner in;

	/**
	 * Whether this command is interrupted.
	 */

	private boolean isInterrupted = true;

	/**
	 * All sub tasks (they need to be interrupted when this runnable is interrupted).
	 */

	private DocsRunnable[] subTasks;

	/**
	 * Creates a new runnable instance.
	 */

	public DocsRunnable() {
		this(System.out);
	}

	/**
	 * Creates a new runnable instance.
	 *
	 * @param out The output stream.
	 */

	public DocsRunnable(final PrintStream out) {
		this(out, System.in);
	}

	/**
	 * Creates a new runnable instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 */

	public DocsRunnable(final PrintStream out, final InputStream in) {
		this(out, in, new DocsRunnable[0]);
	}

	/**
	 * Creates a new runnable instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 * @param subTasks The sub tasks.
	 */

	public DocsRunnable(final PrintStream out, final InputStream in, final DocsRunnable... subTasks) {
		this.out = out;
		setInputStream(in);

		this.subTasks = subTasks;
	}

	/**
	 * Adds some listeners.
	 *
	 * @param listeners The listeners.
	 */

	public final void addListeners(final RunnableListener... listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}

	/**
	 * Removes a listener.
	 *
	 * @param listener The listener.
	 */

	public final void removeListener(final RunnableListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Clears all listeners.
	 */

	public final void clearListeners() {
		listeners.clear();
	}

	/**
	 * Returns whether the runnable can output some message.
	 *
	 * @return Whether the runnable can output some message.
	 */

	public boolean canOutput() {
		return out != null;
	}

	/**
	 * Returns the output stream.
	 *
	 * @return The output stream.
	 */

	public PrintStream getOutputStream() {
		return out;
	}

	/**
	 * Sets the output stream.
	 *
	 * @param out The output stream.
	 */

	public void setOutputStream(final PrintStream out) {
		this.out = out;
	}

	/**
	 * Outputs a message.
	 *
	 * @param message The message.
	 */

	public void output(final String message) {
		if(out == null) {
			return;
		}

		out.print(message);
	}

	/**
	 * Outputs a line.
	 *
	 * @param message The line.
	 */

	public void outputLine(final String message) {
		if(out == null) {
			return;
		}

		out.println(message);
	}

	/**
	 * Outputs a blank line.
	 */

	public void blankLine() {
		if(out == null) {
			return;
		}

		out.println();
	}

	/**
	 * Returns whether the command can input a message.
	 *
	 * @return Whether the command can input a message.
	 */

	public boolean canInput() {
		return in != null;
	}

	/**
	 * Returns the input scanner.
	 *
	 * @return The input scanner.
	 */

	public Scanner getScanner() {
		return in;
	}

	/**
	 * Sets the input stream.
	 *
	 * @param in The input stream.
	 */

	public void setInputStream(final InputStream in) {
		this.in = in == null ? null : new Scanner(in, StandardCharsets.UTF_8.name());
	}

	/**
	 * Inputs a line.
	 *
	 * @return The line.
	 */

	public String inputLine() {
		if(in == null) {
			return null;
		}

		if(in.hasNextLine() && !isInterrupted) {
			return in.nextLine();
		}

		return null;
	}

	/**
	 * Returns all sub tasks.
	 *
	 * @return All sub tasks.
	 */

	public DocsRunnable[] getSubTasks() {
		return subTasks;
	}

	/**
	 * Sets all sub tasks.
	 *
	 * @param subTasks All sub tasks.
	 */

	public void setSubTasks(final DocsRunnable... subTasks) {
		this.subTasks = subTasks;
	}

	/**
	 * Interrupts this runnable and all its sub tasks.
	 */

	public void interrupt() {
		isInterrupted = true;

		for(final DocsRunnable subTask : subTasks) {
			subTask.interrupt();
		}

		if(in != null) {
			in.close();
		}

		for(final RunnableListener listener : listeners) {
			listener.onRunnableFinished(this);
		}
	}

	/**
	 * Returns whether this runnable is interrupted.
	 *
	 * @return Whether this runnable is interrupted.
	 */

	public boolean isInterrupted() {
		return isInterrupted;
	}

	/**
	 * Exits if interrupted.
	 *
	 * @throws InterruptionException The interruption signal.
	 */

	protected void exitIfInterrupted() throws InterruptionException {
		if(!isInterrupted) {
			return;
		}
		throw new InterruptionException();
	}

	/**
	 * Runs this runnable.
	 *
	 * @return The result.
	 */

	public T run() {
		return run(true);
	}

	/**
	 * Runs this runnable.
	 *
	 * @param showTime Whether the execution time should be shown.
	 *
	 * @return The result.
	 */

	public T run(final boolean showTime) {
		try {
			for(final RunnableListener listener : listeners) {
				listener.onRunnableStarted(this);
			}

			isInterrupted = false;
			final long first = System.currentTimeMillis();

			final T result = execute();
			if(result == null) {
				return null;
			}

			final long second = System.currentTimeMillis();
			if(showTime) {
				outputLine("Done in " + (second - first) / 1000f + " seconds !");
			}
			interrupt();

			return result;
		}
		catch(final Exception ex) {
			for(final RunnableListener listener : listeners) {
				listener.onRunnableError(this, ex);
			}
			blankLine();
			outputLine("An exception occurred while executing the command :");
			if(out != null) {
				ex.printStackTrace(out);
			}

			if(out != System.out) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Executes this runnable.
	 *
	 * @return The execution result.
	 *
	 * @throws Exception Whether any exception occurs.
	 */

	protected abstract T execute() throws Exception;

	/**
	 * Represents a runnable listener.
	 */

	public interface RunnableListener {

		/**
		 * Triggered when the runnable execution started.
		 *
		 * @param runnable The runnable.
		 */

		void onRunnableStarted(final DocsRunnable runnable);

		/**
		 * Triggered when the runnable execution finished.
		 *
		 * @param runnable The runnable.
		 */

		void onRunnableFinished(final DocsRunnable runnable);

		/**
		 * Triggered when the runnable encounters an error.
		 *
		 * @param runnable The runnable.
		 */

		void onRunnableError(final DocsRunnable runnable, final Throwable error);

	}

	/**
	 * The interruption signal.
	 */

	public static class InterruptionException extends Exception {

		private static final long serialVersionUID = 1L;

		public InterruptionException() {
			super("Interrupted !");
		}

	}

}