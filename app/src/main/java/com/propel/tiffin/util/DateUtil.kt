package com.propel.tiffin.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun trialChargeDate(startMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
    val chargeInstant = Instant.ofEpochMilli(startMillis).plus(Duration.ofHours(24))
    val localDate = chargeInstant.atZone(zone).toLocalDate()
    return localDate.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH))
}
