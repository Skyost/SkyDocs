package fr.skyost.skydocs.exception;

/**
 * Thrown when an error occurs while loading a project.
 */

public class LoadException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public LoadException(final String message) {
		super(message);
	}
	
	public LoadException(final Exception ex) {
		super(ex);
	}
	
}
