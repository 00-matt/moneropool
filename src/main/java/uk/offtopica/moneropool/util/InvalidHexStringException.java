package uk.offtopica.moneropool.util;

public class InvalidHexStringException extends Exception {
    InvalidHexStringException() {
    }

    InvalidHexStringException(String message) {
        super(message);
    }

    InvalidHexStringException(String message, Throwable cause) {
        super(message, cause);
    }

    InvalidHexStringException(Throwable cause) {
        super(cause);
    }
}
