package coapproxy.payload.transformer;

public class InvalidFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidFormatException(String message) {
		super(message);
	}

	public InvalidFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
