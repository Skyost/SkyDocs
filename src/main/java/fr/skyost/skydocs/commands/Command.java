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
	 * Run the command.
	 */
	
	@Override
	public void run() {
		if(exitOnFinish) {
			System.exit(0);
		}
	}
	
}