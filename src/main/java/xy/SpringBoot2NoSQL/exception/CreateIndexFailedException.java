package xy.SpringBoot2NoSQL.exception;

public class CreateIndexFailedException extends RuntimeException {
    public CreateIndexFailedException(String indexName, Throwable cause) {
        super(String.format("Creating Index '%s' failed", indexName), cause);
    }
}
