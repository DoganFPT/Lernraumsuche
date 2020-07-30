package de.ude.sliot.data_class

import java.time.LocalDateTime
import java.time.ZoneOffset

class DataHistory(historyEntries : Array<HistoryEntry>) {
    private val data = historyEntries.filter { historyEntry -> !historyEntry.value.isNaN() }
    private val consideredData = data.filter { entry -> entry.timestamp.isAfter(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(30)) }

    val avg = if (consideredData.isNotEmpty()) consideredData.sumByDouble { entry -> entry.value } / consideredData.size else Double.NaN

    fun isMissingData() : Boolean {
        return consideredData.isEmpty()
    }
}