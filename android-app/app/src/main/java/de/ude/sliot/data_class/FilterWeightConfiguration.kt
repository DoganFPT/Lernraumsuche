package de.ude.sliot.data_class

class FilterWeightConfiguration(newNoiseThreshold: Int, newNoiseWeight: Int, newTempRange: Pair<Int,Int>, newTempWeight: Int,
                                newWifiThreshold: Int, newWifiWeight: Int, newHumidityRange: Pair<Int,Int>, newHumidityWeight: Int) {
    var noiseThreshold = newNoiseThreshold
    var noiseWeight = newNoiseWeight

    var tempRange = newTempRange
    var tempWeight = newTempWeight

    var wifiThreshold = newWifiThreshold
    var wifiWeight = newWifiWeight

    var humidityRange = newHumidityRange
    var humidityWeight = newHumidityWeight
}