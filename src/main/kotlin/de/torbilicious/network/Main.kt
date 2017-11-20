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

    val deferred = requests.map {
        async {
            IcmpPingUtil.executePingRequest(it)
        }
    }


    val responses = runBlocking {
        deferred.map { it.await() }
    }

    responses.forEach {
        val formattedResponse = "Host '${it.host}': ${it.duration}ms"
        println(formattedResponse)
    }

    println("Average time: ${responses.map { it.duration }.average()}ms")
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
