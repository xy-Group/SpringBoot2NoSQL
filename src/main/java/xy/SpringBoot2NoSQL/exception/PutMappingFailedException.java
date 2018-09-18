package xy.SpringBoot2NoSQL.exception;

public class PutMappingFailedException extends RuntimeException {
    public PutMappingFailedException(String indexName, Throwable cause) {
        super(String.format("Put Mapping failed for Index '%s'", indexName), cause);
    }
}
