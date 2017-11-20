package de.torbilicious.network

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.icmp4j.IcmpPingRequest
import org.icmp4j.IcmpPingUtil

class PingAllocator {
    fun execute(requests: List<IcmpPingRequest>): PingReport {
        val deferred = requests.map {
            async {
                IcmpPingUtil.executePingRequest(it)
            }
        }

        val responses = runBlocking {
            deferred.map { it.await() }
        }

        return PingReport(responses.map { PingResult(it.host, it.duration) })
    }

    fun createRequests(host: String, amount: Int): Iterable<IcmpPingRequest> {
        val requests = mutableListOf<IcmpPingRequest>()

        for (count in 1..amount) {
            val request = IcmpPingUtil.createIcmpPingRequest()
            request.host = host

            requests.add(request)
        }

        return requests
    }
}

data class PingReport(private val results: List<PingResult>) {
    val average: Double = results.map { it.ping }.average()

    override fun toString() = results.joinToString("\n")
}

data class PingResult(val host: Host, val ping: Ping)

typealias Host = String
typealias Ping = Long
