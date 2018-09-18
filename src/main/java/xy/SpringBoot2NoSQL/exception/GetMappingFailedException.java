package xy.SpringBoot2NoSQL.exception;

public class GetMappingFailedException extends RuntimeException {
    public GetMappingFailedException(String indexName, Throwable cause) {
        super(String.format("Create Mapping failed for Index '%s'", indexName), cause);
    }
}
