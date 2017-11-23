package de.torbilicious.network

import org.icmp4j.IcmpPingRequest


val allocator = PingAllocator()
val elasticAdapter = ElkAdapter()

fun main(args: Array<String>) {
    val requests = mutableListOf<IcmpPingRequest>()
    requests.addAll(allocator.createRequests("www.google.com", 10))
    requests.addAll(allocator.createRequests("4.2.2.2", 10))
//    requests.addAll(allocator.createRequests("www.facebook.com", 10))
//    requests.addAll(allocator.createRequests("104.160.141.3", 10))
    val pingReport = allocator.execute(requests)

    val speed = SpeedAllocator().calcSpeed()
    println("Speed: $speed")

    val report = Report(pingReport, SpeedResult(speed))

    elasticAdapter.upload(report)
    println("Average ping: ${report.average}ms")
}
