package uk.offtopica.moneropool.api.exception;

public class BlockNotFoundException extends Exception {
    public BlockNotFoundException() {
    }

    public BlockNotFoundException(String message) {
        super(message);
    }

    public BlockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockNotFoundException(Throwable cause) {
        super(cause);
    }
}
