package de.torbilicious.network

import com.google.gson.Gson
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType


class ElkAdapter {
    private val gson = Gson()
    private val index = "ping"

    fun upload(report: PingReport) {
        val client = createClient()

        val request = IndexRequest(index, "doc")

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
}
