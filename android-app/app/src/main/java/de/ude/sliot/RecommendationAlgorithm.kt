package de.ude.sliot

import de.ude.sliot.data_class.*
import de.ude.sliot.filter_enum.NoiseEnum
import de.ude.sliot.filter_enum.WiFiStrengthEnum
import kotlin.math.pow
import kotlin.math.roundToInt

class RecommendationAlgorithm {
    companion object {
        fun getRecommendationValue(room: Room, filterWeightConfiguration: FilterWeightConfiguration) : Int {
            //Get recommendation for every filter
            val noiseRecommendation = getNoiseRecommendation(room.noiseHistory, NoiseEnum.enumValueToValue(filterWeightConfiguration.noiseThreshold), filterWeightConfiguration.noiseWeight)
            val temperatureRecommendation = getTemperatureRecommendation(room.temperatureHistory, filterWeightConfiguration.tempRange, filterWeightConfiguration.tempWeight)
            val wifiRecommendation = getWifiRecommendation(room.wifiHistory, WiFiStrengthEnum.enumValueToValue(filterWeightConfiguration.wifiThreshold), filterWeightConfiguration.wifiWeight)
            val humidityRecommendation = getHumidityRecommendation(room.humidityHistory, filterWeightConfiguration.humidityRange, filterWeightConfiguration.humidityWeight)

            //Add recommendations and keep track of how much "usable" information we had
            var recommendationScore = 0.0
            var usedDataCounter = 0

            if (!noiseRecommendation.isNaN()) {
                recommendationScore += noiseRecommendation
                usedDataCounter++
            }
            if (!temperatureRecommendation.isNaN()) {
                recommendationScore += temperatureRecommendation
                usedDataCounter++
            }
            if (!wifiRecommendation.isNaN()) {
                recommendationScore += wifiRecommendation
                usedDataCounter++
            }
            if (!humidityRecommendation.isNaN()) {
                recommendationScore += humidityRecommendation
                usedDataCounter++
            }

            //Calc avg recommendation for all used information and set it
            recommendationScore /= if (usedDataCounter > 0) usedDataCounter else 1//safe division

            //NOTE: Enable this for debugging purposes related to the recommendation calculation!
            //println("Room: " + room.title + " - Total: " + (100 * recommendationScore).roundToInt() + ", Noise: " + room.noiseHistory?.avg + "/" + noiseRecommendation + ", Temp: " + room.temperatureHistory?.avg + "/" + temperatureRecommendation + ", WiFi: " + room.wifiHistory?.avg + "/" + wifiRecommendation + ", Humidity: " + room.humidityHistory?.avg + "/" + humidityRecommendation)

            return (100 * recommendationScore).roundToInt()
        }

        private fun getNoiseRecommendation(noiseHistory: DataHistory, noiseThreshold: Int, noiseWeight: Int) : Double {
            return when {
                    noiseHistory.avg.isNaN() -> Double.NaN
                    noiseHistory.avg <= noiseThreshold -> 1.0
                    else -> getValueGEQZero(1 - (0.005 * noiseWeight * (noiseHistory.avg - noiseThreshold).pow(2)))
            }
        }

        private fun getTemperatureRecommendation(temperatureHistory: DataHistory, tempRange: Pair<Int, Int>, tempWeight: Int) : Double {
            return when {
                temperatureHistory.avg.isNaN() -> Double.NaN
                temperatureHistory.avg < tempRange.first -> getValueGEQZero(1 - (0.005 * tempWeight * (-temperatureHistory.avg + tempRange.first).pow(2)))
                temperatureHistory.avg > tempRange.second -> getValueGEQZero(1 - (0.005 * tempWeight * (temperatureHistory.avg - tempRange.second).pow(2)))
                else -> 1.0
            }
        }

        private fun getWifiRecommendation(wifiHistory: DataHistory, wifiThreshold: Int, wifiWeight: Int) : Double {
            return when {
                wifiHistory.avg.isNaN() -> Double.NaN
                wifiHistory.avg >= wifiThreshold -> 1.0
                else -> getValueGEQZero(1 - (0.005 * wifiWeight * (-wifiHistory.avg + wifiThreshold).pow(2)))
            }
        }

        private fun getHumidityRecommendation(humidityHistory: DataHistory, humidityRange: Pair<Int, Int>, humidityWeight: Int) : Double {
            return when {
                humidityHistory.avg.isNaN() -> Double.NaN
                humidityHistory.avg < humidityRange.first -> getValueGEQZero(1 - (0.005 * humidityWeight * (-humidityHistory.avg + humidityRange.first).pow(2)))
                humidityHistory.avg > humidityRange.second -> getValueGEQZero(1 - (0.005 * humidityWeight * (humidityHistory.avg - humidityRange.second).pow(2)))
                else -> 1.0
            }
        }

        private fun getValueGEQZero(x: Double) : Double {
            return if (x < 0.0) 0.0 else x
        }
    }
}