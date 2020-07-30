package de.ude.sliot.filter_enum

enum class NoiseEnum(val value: Int) {
    VERY_QUIET(1),
    QUIET(2),
    WHISPERING(3),
    AVERAGE(4),
    ROOM_VOLUME(5),
    LOUD(6),
    VERY_LOUD(7);

    companion object {
        fun enumToValue(x: NoiseEnum) : Int {
            //Up until this value (dB), it is ...
            return when(x) {
                VERY_QUIET -> 20
                QUIET -> 30
                WHISPERING -> 35
                AVERAGE -> 40
                ROOM_VOLUME -> 50
                LOUD -> 65
                VERY_LOUD -> Int.MAX_VALUE
            }
        }

        fun valueToEnum(x: Double?) : NoiseEnum {
            x?.let {
                return when {
                    it <= 20.0 -> VERY_QUIET
                    it <= 30.0 -> QUIET
                    it <= 35.0 -> WHISPERING
                    it <= 40.0 -> AVERAGE
                    it <= 50.0 -> ROOM_VOLUME
                    it <= 65.0 -> LOUD
                    else -> VERY_LOUD
                }
            }

            return VERY_LOUD
        }

        fun enumValueToValue(i: Int) : Int {
            return when(i) {
                VERY_QUIET.value -> 20
                QUIET.value -> 30
                WHISPERING.value -> 35
                AVERAGE.value -> 40
                ROOM_VOLUME.value -> 50
                LOUD.value -> 65
                VERY_LOUD.value -> Int.MAX_VALUE
                else -> Int.MAX_VALUE
            }
        }
    }
}