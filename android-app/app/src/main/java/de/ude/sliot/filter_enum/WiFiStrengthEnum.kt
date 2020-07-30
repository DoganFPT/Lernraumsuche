package de.ude.sliot.filter_enum

enum class WiFiStrengthEnum(val value: Int) {
    NONE(7),
    VERY_BAD(6),
    BAD(5),
    AVERAGE(4),
    GOOD(3),
    VERY_GOOD(2),
    EXCELLENT(1);

    companion object {
        fun enumToValue(x: WiFiStrengthEnum) : Int {
            //Starting from this value (dB), it is ...
            return when(x) {
                NONE -> Int.MIN_VALUE
                VERY_BAD -> -90
                BAD -> -80
                AVERAGE -> -70
                GOOD -> -67
                VERY_GOOD -> -60
                EXCELLENT -> -50
            }
        }

        fun valueToEnum(x: Double?) : WiFiStrengthEnum {
            x?.let {
                return when {
                    it >= -50.0 -> EXCELLENT
                    it >= -60.0 -> VERY_GOOD
                    it >= -67.0 -> GOOD
                    it >= -70.0 -> AVERAGE
                    it >= -80.0 -> BAD
                    it >= -90.0 -> VERY_BAD
                    else -> NONE
                }
            }

            return NONE
        }

        fun enumValueToValue(i: Int) : Int {
            return when(i) {
                NONE.value -> Int.MIN_VALUE
                VERY_BAD.value -> -90
                BAD.value -> -80
                AVERAGE.value -> 70
                GOOD.value -> -67
                VERY_GOOD.value -> -60
                EXCELLENT.value -> -50
                else -> Int.MIN_VALUE
            }
        }
    }
}