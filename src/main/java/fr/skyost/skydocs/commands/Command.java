package fr.skyost.skydocs.commands;

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
	 * Run the command.
	 */
	
	@Override
	public void run() {
		if(exitOnFinish) {
			System.exit(0);
		}
	}
	
	public final void output(final String message) {
		output(message, output);
	}
	
	public final void output(final String message, final boolean output) {
		if(output) {
			System.out.print(message + " ");
		}
	}
	
	public final void outputLine(final String message) {
		outputLine(message, output);
	}
	
	public final void outputLine(final String message, final boolean output) {
		if(output) {
			System.out.println(message);
		}
	}
	
	public final void blankLine() {
		if(output) {
			System.out.println();
		}
	}
	
	public final void printStackTrace(final Throwable throwable) {
		System.out.println();
		throwable.printStackTrace();
	}
	
}