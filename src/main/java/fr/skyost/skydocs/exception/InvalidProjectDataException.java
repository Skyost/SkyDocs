package fr.skyost.skydocs.exception;

/**
 * Thrown when an invalid project data is submitted.
 */

public class InvalidProjectDataException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidProjectDataException(final String message) {
		super(message);
	}
	
	public InvalidProjectDataException(final Exception ex) {
		super(ex);
	}
	
}
