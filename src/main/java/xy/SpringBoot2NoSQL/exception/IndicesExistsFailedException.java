package xy.SpringBoot2NoSQL.exception;

public class IndicesExistsFailedException extends RuntimeException {
    public IndicesExistsFailedException(String indexName, Throwable cause) {
        super(String.format("Indices '%s' failed", indexName), cause);
    }
}