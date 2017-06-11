package fr.skyost.skydocs.exceptions;

/**
 * Thrown when an invalid menu data is submitted.
 */

public class InvalidMenuDataException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidMenuDataException(final String message) {
		super(message);
	}
	
	public InvalidMenuDataException(final Exception ex) {
		super(ex);
	}
	
}
