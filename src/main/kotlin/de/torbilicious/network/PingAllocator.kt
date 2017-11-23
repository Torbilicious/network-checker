package de.torbilicious.network

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.icmp4j.IcmpPingRequest
import org.icmp4j.IcmpPingUtil

class PingAllocator {
    fun execute(requests: List<IcmpPingRequest>): List<PingResult> {
        val deferred = requests.map {
            async {
                IcmpPingUtil.executePingRequest(it)
            }
        }

        val responses = runBlocking {
            deferred.map { it.await() }
        }

        return responses.map { PingResult(it.host, it.duration) }
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

data class Report(private val pingResults: List<PingResult>,
                  private val speedResult: SpeedResult,
                  private val timestamp: Long = System.currentTimeMillis()) {
    val average: Double = pingResults.map { it.ping }.average()

    override fun toString() = pingResults.joinToString("\n")
}

data class PingResult(val host: Host?, val ping: Ping)
data class SpeedResult(val speed: Speed)

typealias Host = String
typealias Ping = Long
