package de.ude.sliot.data_class
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JSONEntry(val Room: String, val data: List<JSONData>) {

    fun getTemperatureHistory(): DataHistory {
        val temperatureEntries: ArrayList<HistoryEntry> = ArrayList()
        for(elem in data) {
            temperatureEntries.add(HistoryEntry(LocalDateTime.parse(elem.Time, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")), elem.Temperature))
        }
        val temperatures = arrayOfNulls<HistoryEntry>(temperatureEntries.size)
        return DataHistory(temperatureEntries.toArray(temperatures))
    }

    fun getHumidityHistory(): DataHistory {
        val temperatureEntries: ArrayList<HistoryEntry> = ArrayList()
        for(elem in data) {
            temperatureEntries.add(HistoryEntry(LocalDateTime.parse(elem.Time, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")), elem.Humidity))
        }
        val temperatures = arrayOfNulls<HistoryEntry>(temperatureEntries.size)
        return DataHistory(temperatureEntries.toArray(temperatures))
    }

    fun getNoiseHistory(): DataHistory {
        val temperatureEntries: ArrayList<HistoryEntry> = ArrayList()
        for(elem in data) {
            temperatureEntries.add(HistoryEntry(LocalDateTime.parse(elem.Time, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")), elem.Noise))
        }
        val temperatures = arrayOfNulls<HistoryEntry>(temperatureEntries.size)
        return DataHistory(temperatureEntries.toArray(temperatures))
    }

    fun getWifiHistory(): DataHistory {
        val temperatureEntries: ArrayList<HistoryEntry> = ArrayList()
        for(elem in data) {
            temperatureEntries.add(HistoryEntry(LocalDateTime.parse(elem.Time, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")), elem.Wlan))
        }
        val temperatures = arrayOfNulls<HistoryEntry>(temperatureEntries.size)
        return DataHistory(temperatureEntries.toArray(temperatures))
    }
}