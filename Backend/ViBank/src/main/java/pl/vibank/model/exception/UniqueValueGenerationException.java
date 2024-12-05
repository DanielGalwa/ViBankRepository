package pl.vibank.model.exception;

public class UniqueValueGenerationException extends RuntimeException {
    public UniqueValueGenerationException(String message) {
        super(message);
    }
}