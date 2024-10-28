package ee.ut.math.tvt.salessystem;

public class InvalidPriceException extends SalesSystemException {
    public InvalidPriceException(String message) {
        super(message);
    }

    public InvalidPriceException(String message, Throwable cause) {
        super(message, cause);
    }
}
