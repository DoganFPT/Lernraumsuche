package de.ude.sliot.filter_enum

enum class HumidityEnum {
    VERY_DRY, DRY, AVERAGE, HUMID, VERY_HUMID;

    companion object {
        fun enumToValue(x: HumidityEnum) : Double {
            //Up until this value (percentage 0-100), it is...
            return when(x) {
                VERY_DRY -> 30.0
                DRY -> 50.00
                AVERAGE -> 60.0
                HUMID -> 70.0
                VERY_HUMID -> Double.MAX_VALUE
            }
        }

        fun valueToEnum(x: Double?) : HumidityEnum {
            x?.let {
                return when {
                    it <= 30.0 -> VERY_DRY
                    it <= 50.0 -> DRY
                    it <= 60.0 -> AVERAGE
                    it <= 70.0 -> HUMID
                    else -> VERY_HUMID
                }
            }

            return VERY_DRY
        }
    }
}