package fr.skyost.skydocs.exceptions;

/**
 * Thrown when an invalid menu entry is submitted.
 */

public class InvalidMenuEntryException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidMenuEntryException(final String message) {
		super(message);
	}
	
	public InvalidMenuEntryException(final Exception ex) {
		super(ex);
	}
	
}
