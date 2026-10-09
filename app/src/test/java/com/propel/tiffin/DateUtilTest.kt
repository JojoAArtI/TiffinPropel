package com.propel.tiffin

import com.propel.tiffin.util.trialChargeDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

class DateUtilTest {

    @Test
    fun trialChargeDate_addsExactly24Hours() {
        // 2026-10-08 10:00:00 UTC -> +24h -> 2026-10-09 10:00 UTC
        val startMillis = 1791453600000L
        assertEquals("9 Oct 2026", trialChargeDate(startMillis, ZoneId.of("UTC")))
    }

    @Test
    fun trialChargeDate_isZoneSensitive() {
        // Same instant, different zone must yield a different local charge date.
        // +24h instant = 2026-10-09 10:00 UTC. In UTC+14 that is 2026-10-10 00:00 local.
        val startMillis = 1791453600000L
        assertEquals("9 Oct 2026", trialChargeDate(startMillis, ZoneId.of("UTC")))
        assertEquals("10 Oct 2026", trialChargeDate(startMillis, ZoneId.of("Pacific/Kiritimati")))
    }

    @Test
    fun trialChargeDate_rollsOverMonthAndYear() {
        // 2026-12-31 15:00:00 UTC -> +24h -> 2027-01-01 15:00 UTC
        val startMillis = 1798729200000L
        assertEquals("1 Jan 2027", trialChargeDate(startMillis, ZoneId.of("UTC")))
    }
}
