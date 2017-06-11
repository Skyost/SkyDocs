package fr.skyost.skydocs.exceptions;

/**
 * Thrown when an error occurs while loading a template.
 */

public class InvalidTemplateException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidTemplateException(final String message) {
		super(message);
	}
	
	public InvalidTemplateException(final Exception ex) {
		super(ex);
	}
	
}
