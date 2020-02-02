package uk.offtopica.moneropool.api.exception;

public class MinerNotFoundException extends Exception {
    public MinerNotFoundException() {
    }

    public MinerNotFoundException(String message) {
        super(message);
    }

    public MinerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinerNotFoundException(Throwable cause) {
        super(cause);
    }
}
