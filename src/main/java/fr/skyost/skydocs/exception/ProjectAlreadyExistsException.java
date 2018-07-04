package fr.skyost.skydocs.exception;

/**
 * Thrown when an invalid menu data is submitted.
 */

public class ProjectAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ProjectAlreadyExistsException(final String message) {
		super(message);
	}
	
	public ProjectAlreadyExistsException(final Exception ex) {
		super(ex);
	}
	
}
