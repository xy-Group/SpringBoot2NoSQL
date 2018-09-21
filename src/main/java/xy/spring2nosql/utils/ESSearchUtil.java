package xy.spring2nosql.utils;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import xy.spring2nosql.exception.CreateIndexFailedException;
import xy.spring2nosql.exception.IndicesExistsFailedException;
import xy.spring2nosql.exception.PutMappingFailedException;

public class ESSearchUtil {

	/**
	 * 索引是否存在
	 * @param client
	 * @param indexName
	 * @return
	 */
	public static IndicesExistsResponse indexExist(Client client, String indexName) {
		try {
			return client.admin().indices().prepareExists(indexName).execute().actionGet();
		} catch (Exception e) {

			throw new IndicesExistsFailedException(indexName, e);
		}
	}

	/**
	 * 创建索引
	 * @param client
	 * @param indexName
	 * @return
	 */
	public static CreateIndexResponse createIndex(Client client, String indexName) {
		try {
			return internalCreateIndex(client, indexName);
		} catch (Exception e) {
			throw new CreateIndexFailedException(indexName, e);
		}
	}

	public static PutMappingResponse putMapping(Client client, String indexName, IElasticSearchMapping mapping) {
		try {
			return internalPutMapping(client, indexName, mapping);
		} catch (Exception e) {

			throw new PutMappingFailedException(indexName, e);
		}
	}

	private static CreateIndexResponse internalCreateIndex(Client client, String indexName) throws IOException {
		final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);

		final CreateIndexResponse indexResponse = createIndexRequestBuilder.execute().actionGet();

		return indexResponse;
	}

	private static PutMappingResponse internalPutMapping(Client client, String indexName, IElasticSearchMapping mapping)
			throws IOException {

		final PutMappingRequest putMappingRequest = new PutMappingRequest(indexName).type(mapping.getIndexType())
				.source(mapping.getMapping().string());

		final PutMappingResponse putMappingResponse = client.admin().indices().putMapping(putMappingRequest)
				.actionGet();

		return putMappingResponse;
	}

	/**
	 * 部分匹配查询，包括模糊匹配和短语或邻近查询。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static QueryBuilder matchQuery(String key, String value) {
		return QueryBuilders.matchQuery(key, value);
	}

	/**
	 * 完全匹配。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static QueryBuilder matchPhraseQuery(String key, String value) {
		return QueryBuilders.matchPhraseQuery(key, value);
	}

	/**
	 * 多字段匹配
	 * 
	 * @param text
	 * @param fieldNames
	 * @return
	 */
	public static QueryBuilder multiMatchQuery(Object text, String... fieldNames) {
		return QueryBuilders.multiMatchQuery(text, fieldNames);

	}

	/**
	 * 模糊查询
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static QueryBuilder fuzzyQuery(String key, String value) {
		return QueryBuilders.fuzzyQuery(key, value);
	}

	/**
	 * 全部查询
	 * 
	 * @return
	 */
	public static QueryBuilder matchAllQuery() {
		return QueryBuilders.matchAllQuery();
	}

	/**
	 * 范围查询
	 * 
	 * @param key
	 * @param from
	 * @param to
	 * @return
	 */
	public static QueryBuilder rangeQuery(String key, Object from, Object to) {
		return QueryBuilders.rangeQuery(key).from(from).to(to).includeLower(true) // 包括下界
				.includeUpper(false); // 包括上界
	}

	/**
	 * 完全匹配查询
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static QueryBuilder termsQuery(String key, String... values) {
		return QueryBuilders.termsQuery(key, values);
	}

	public static QueryBuilder termsQuery(String key, Object values) {
		return QueryBuilders.termsQuery(key, values);
	}

}
