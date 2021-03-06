package de.torbilicious.network

import com.google.gson.Gson
import org.apache.http.HttpHost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.ResponseException
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType


class ElkAdapter {
    private val gson = Gson()
    private val index = "internet"

    fun upload(report: Report) {
        val client = createClient()

        if (!indexExists(client.lowLevelClient, index)) {
            createIndex(client.lowLevelClient)
        }

        val request = IndexRequest(index, "data")

        val json = gson.toJson(report)
        request.source(json, XContentType.JSON)

        client.index(request)
        client.close()
    }

    private fun indexExists(client: RestClient, index: String): Boolean {
        return try {
            client.performRequest("GET", index, mapOf(), StringEntity("")).statusLine.statusCode == 200
        } catch (e: ResponseException) {
            false
        }
    }

    private fun createClient(): RestHighLevelClient {
        println("Creating index")

        return RestHighLevelClient(
                RestClient.builder(
                        HttpHost("localhost", 9200, "http")))
    }

    private fun createIndex(client: RestClient) {
        val indexSettings = "{\n" +
                "  \"mappings\": {\n" +
                "    \"data\": {\n" +
                "      \"properties\": {\n" +
                "        \"timestamp\": {\n" +
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

        client.performRequest("PUT", index, mapOf(), entity)

    }
}
