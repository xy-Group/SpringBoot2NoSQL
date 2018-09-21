package xy.spring2nosql.exception;

public class GetMappingFailedException extends RuntimeException {
    public GetMappingFailedException(String indexName, Throwable cause) {
        super(String.format("Create Mapping failed for Index '%s'", indexName), cause);
    }
}
