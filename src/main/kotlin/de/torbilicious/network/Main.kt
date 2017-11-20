package de.torbilicious.network

import org.icmp4j.IcmpPingRequest


val allocator = PingAllocator()
val elasticAdapter = ElkAdapter()

fun main(args: Array<String>) {
    val requests = mutableListOf<IcmpPingRequest>()
    requests.addAll(allocator.createRequests("www.google.com", 10))
    requests.addAll(allocator.createRequests("www.facebook.com", 10))
    requests.addAll(allocator.createRequests("104.160.141.3", 10))

    val report = allocator.execute(requests)
    elasticAdapter.upload(report)
}