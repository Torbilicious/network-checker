package de.torbilicious.network

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.icmp4j.IcmpPingRequest
import org.icmp4j.IcmpPingUtil


fun main(args: Array<String>) {
    request()
}

fun request() {

    val requests = mutableListOf<IcmpPingRequest>()
    requests.addAll(createRequests("www.google.com", 10))
    requests.addAll(createRequests("www.facebook.com", 10))
    requests.addAll(createRequests("104.160.141.3", 10))

    val report = execute(requests)

    println()
    println("Report: $report")

    println("Average time: ${report.average()}ms")
}

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

data class PingReport(private val results: List<PingResult>) {
    fun average(): Double = results.map { it.ping }.average()

    override fun toString() = results.joinToString("\n")
}

data class PingResult(val host: Host, val ping: Ping)

typealias Host = String
typealias Ping = Long
