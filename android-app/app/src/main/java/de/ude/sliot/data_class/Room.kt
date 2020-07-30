package de.ude.sliot.data_class

import de.ude.sliot.RecommendationAlgorithm

class Room(newTitle : String, newTemperatureHistory : DataHistory, newNoiseHistory : DataHistory, newWifiHistory : DataHistory, newHumidityHistory : DataHistory, filterWeightConfiguration: FilterWeightConfiguration) {
    val title = newTitle
    val temperatureHistory = newTemperatureHistory
    val noiseHistory = newNoiseHistory
    val wifiHistory = newWifiHistory
    val humidityHistory = newHumidityHistory

    var recommendationLevel = RecommendationAlgorithm.getRecommendationValue(this, filterWeightConfiguration)

    fun isDataMissing(): Boolean {
        return temperatureHistory.isMissingData() || noiseHistory.isMissingData() || wifiHistory.isMissingData() || humidityHistory.isMissingData()
    }
}