package fr.skyost.skydocs.commands;

import java.io.PrintStream;
import java.util.HashSet;

/**
 * Represents a command.
 */

public abstract class Command implements Runnable {
	
	/**
	 * Arguments sent to this command.
	 */
	
	private String[] args;
	
	/**
	 * If the JVM should exit when the command has been ran.
	 */
	
	private boolean exitOnFinish = false;
	
	/**
	 * If this command should output informations.
	 */

	private boolean output = true;
	
	/**
	 * First time point.
	 */
	
	private long firstTime = 0L;
	
	/**
	 * Second time point.
	 */
	
	private long secondTime = 0L;
	
	/**
	 * Whether this command has been interrupted.
	 */
	
	boolean isInterrupted = false;
	
	/**
	 * The command listeners.
	 */
	
	private final HashSet<CommandListener> listeners = new HashSet<CommandListener>();
	
	/**
	 * The current PrintStream.
	 */
	
	private PrintStream out = System.out;
	
	/**
	 * Creates a new Command instance.
	 * 
	 * @param args The arguments.
	 */
	
	public Command(final String... args) {
		this.args = args;
	}
	
	/**
	 * Gets the arguments of this command.
	 * 
	 * @return The arguments.
	 */
	
	public final String[] getArguments() {
		return args;
	}
	
	/**
	 * Sets the arguments of this command.
	 * 
	 * @param args The arguments.
	 */
	
	public final void setArguments(final String... args) {
		this.args = args;
	}
	
	/**
	 * Checks if this command is interruptible.
	 * 
	 * @return Whether this command is interruptible.
	 */
	
	public abstract boolean isInterruptible();
	
	/**
	 * Checks if the JVM should exit on finish.
	 * 
	 * @return Whether the JVM should exit or not.
	 */
	
	public final boolean getExitOnFinish() {
		return exitOnFinish;
	}
	
	/**
	 * Sets whether the JVM should exit or not.
	 * 
	 * @param exitOnFinish If the JVM should exit on finish.
	 */
	
	public final void setExitOnFinish(final boolean exitOnFinish) {
		this.exitOnFinish = exitOnFinish;
	}
	
	/**
	 * Gets if this command should use System.out.print(...).
	 * 
	 * @return Whether this command should output informations.
	 */
	
	public final boolean isOutputing() {
		return output;
	}
	
	/**
	 * Sets whether this command should use System.out.print(...).
	 * 
	 * @param output Whether this command should output informations.
	 */
	
	public final void setOutputing(final boolean output) {
		this.output  = output;
	}
	
	/**
	 * Registers the first time point.
	 */
	
	public final void firstTime() {
		firstTime = System.currentTimeMillis();
	}
	
	/**
	 * Registers the second time point.
	 */
	
	public final void secondTime() {
		secondTime = System.currentTimeMillis();
	}
	
	/**
	 * Prints "Done in x seconds !" with x being the time between the first and the second point.
	 */
	
	public final void printTimeElapsed() {
		outputLine("Done in " + ((float)((secondTime - firstTime) / 1000f)) + " seconds !");
	}
	
	/**
	 * Run the command.
	 */
	
	@Override
	public void run() {
		isInterrupted = false;
		broadcastCommandStarted();
	}
	
	/**
	 * Exits if interrupted.
	 * 
	 * @throws InterruptionException The interruption signal.
	 */
	
	void exitIfInterrupted() throws InterruptionException {
		if(!isInterrupted()) {
			return;
		}
		exitIfNeeded();
		throw new InterruptionException();
	}
	
	/**
	 * Exits if needed.
	 */
	
	final void exitIfNeeded() {
		interrupt();
		if(exitOnFinish) {
			System.exit(0);
		}
	}
	
	/**
	 * Interrupts the execution of the current command. Please note that the implementation of this method may change depending on the command.
	 */
	
	public void interrupt() {
		isInterrupted = true;
		broadcastCommandFinished();
	}
	
	/**
	 * Checks if this command is interrupted (or is currently not running).
	 * 
	 * @return Whether this command is interrupted (or is currently not running).
	 */
	
	public final boolean isInterrupted() {
		return isInterrupted;
	}
	
	/**
	 * Gets the current PrintStream.
	 * 
	 * @return The current PrintStream.
	 */
	
	public final PrintStream getOut() {
		return out;
	}
	
	/**
	 * Sets the current PrintStream.
	 * 
	 * @param out The new PrintStream.
	 */
	
	public final void setOut(final PrintStream out) {
		this.out = out;
	}
	
	/**
	 * Adds a listener.
	 * 
	 * @param listener The listener.
	 */
	
	public final void addListener(final CommandListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener.
	 * 
	 * @param listener The listener.
	 */
	
	public final void removeListener(final CommandListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Clears all listeners.
	 */
	
	public final void clearListeners() {
		listeners.clear();
	}
	
	/**
	 * Outputs a message without line break.
	 * 
	 * @param message The message.
	 */
	
	void output(final String message) {
		output(message, output);
	}
	
	/**
	 * Outputs a message without line break.
	 * 
	 * @param message The message.
	 * @param output Whether the message should be printed.
	 */
	
	void output(final String message, final boolean output) {
		if(!output) {
			return;
		}
		out.print(message + " ");
		if(out != System.out) {
			System.out.print(message + " ");
		}
	}
	
	/**
	 * Outputs a message with a line break.
	 * 
	 * @param message The message.
	 */
	
	final void outputLine(final String message) {
		outputLine(message, output);
	}
	
	/**
	 * Outputs a message with a line break.
	 * 
	 * @param message The message.
	 * @param output Whether the message should be printed.
	 */
	
	final void outputLine(final String message, final boolean output) {
		if(!output) {
			return;
		}
		out.println(message);
		if(out != System.out) {
			System.out.println(message);
		}
	}
	
	/**
	 * Outputs a line only if the current PrintStream is System.out.
	 * 
	 * @param message The line.
	 */
	
	final void standardOutputLine(final String message) {
		if(!output || out != System.out) {
			return;
		}
		out.println(message);
	}
	
	/**
	 * Prints a blank line.
	 */
	
	final void blankLine() {
		if(!output) {
			return;
		}
		out.println();
		if(out != System.out) {
			System.out.println();
		}
	}
	
	/**
	 * Prints the StackTrace of a specified throwable.
	 * 
	 * @param throwable The throwable.
	 */
	
	final void printStackTrace(final Throwable throwable) {
		out.println();
		if(throwable instanceof InterruptionException) {
			outputLine(throwable.getMessage());
			return;
		}
		throwable.printStackTrace(out);
		if(out != System.out) {
			throwable.printStackTrace();
		}
	}
	
	/**
	 * Broadcasts a command started event.
	 */
	
	final void broadcastCommandStarted() {
		for(final CommandListener listener : listeners) {
			listener.onCommandStarted(this);
		}
	}
	
	/**
	 * Broadcasts a command finished event.
	 */
	
	final void broadcastCommandFinished() {
		for(final CommandListener listener : listeners) {
			listener.onCommandFinished(this);
		}
	}
	
	/**
	 * Broadcasts a command error event.
	 */
	
	final void broadcastCommandError(final Throwable throwable) {
		for(final CommandListener listener : listeners) {
			listener.onCommandError(this, throwable);
		}
	}
	
	/**
	 * The command listener interface.
	 */
	
	public interface CommandListener {
		
		public void onCommandStarted(final Command command);
		public void onCommandFinished(final Command command);
		public void onCommandError(final Command command, final Throwable error);
		
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