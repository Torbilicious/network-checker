package de.torbilicious.network

import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import java.math.BigDecimal

class SpeedAllocator {
    fun calcSpeed(): Speed {
        val speedTestSocket = SpeedTestSocket()

        var result: BigDecimal = BigDecimal.ZERO

        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                result = report.transferRateBit
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                println(errorMessage)
                result = BigDecimal.ONE
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
            }
        })

        speedTestSocket.startFixedDownload("http://2.testdebit.info/fichiers/500Mo.dat", 10000)

        while (result == BigDecimal.ZERO) {
            Thread.sleep(200)
        }

        return Speed(result)
    }
}

data class Speed(private val speed: BigDecimal) {
    private val million = BigDecimal(1000000)

    val downloadSpeed = "${speed / million}MB/s"

    override fun toString() = downloadSpeed
}
