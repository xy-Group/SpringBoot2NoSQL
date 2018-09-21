package xy.spring2nosql.utils;

import org.elasticsearch.Version;
import org.elasticsearch.common.xcontent.XContentBuilder;

public interface IElasticSearchMapping {

    XContentBuilder getMapping();

    String getIndexType();

    Version getVersion();

}