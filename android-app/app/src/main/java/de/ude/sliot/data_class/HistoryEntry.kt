package de.ude.sliot.data_class

import java.time.LocalDateTime

class HistoryEntry(parTimestamp : LocalDateTime, parValue : Double) {
    //REMARKS:
    //If no timestamp is present --> don't create
    //If no JSONData value is present for a timestamp --> use Double.NaN

    val timestamp : LocalDateTime = parTimestamp
    val value : Double = parValue
}
