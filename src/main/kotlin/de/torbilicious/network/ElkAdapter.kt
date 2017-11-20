package de.torbilicious.network

import com.google.gson.Gson
import org.apache.http.HttpHost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType


class ElkAdapter {
    private val gson = Gson()
    private val index = "ping"

    fun upload(report: PingReport) {
        val client = createClient()

        createIndex(client)

        val request = IndexRequest(index, "data")

        val json = gson.toJson(report)
        request.source(json, XContentType.JSON)

        client.index(request)
        client.close()
    }

    private fun createClient(): RestHighLevelClient {
        return RestHighLevelClient(
                RestClient.builder(
                        HttpHost("localhost", 9200, "http")))
    }

    private fun createIndex(client: RestHighLevelClient) {
        val indexSettings = "{\n" +
                "  \"mappings\": {\n" +
                "    \"data\": {\n" +
                "      \"properties\": {\n" +
                "        \"results.timestamp\": {\n" +
                "          \"type\": \"date\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        var entity: StringEntity? = null
        if (!indexSettings.isNullOrEmpty()) {
            entity = StringEntity(indexSettings, ContentType.APPLICATION_JSON)
        }

        client.lowLevelClient.performRequest("PUT", index, mapOf(), entity)

    }
}
