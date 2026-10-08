package com.propel.tiffin

import com.propel.tiffin.util.trialChargeDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

class DateUtilTest {

    @Test
    fun trialChargeDate_addsExactly24Hours() {
        // 2026-10-08 10:00:00 UTC
        val startMillis = 1791453600000L
        val result = trialChargeDate(startMillis, ZoneId.of("UTC"))
        assertEquals("9 Oct 2026", result)
    }

    @Test
    fun trialChargeDate_rollsOverCalendarDate() {
        // 2026-10-08 23:30:00 UTC
        val startMillis = 1791502200000L
        val result = trialChargeDate(startMillis, ZoneId.of("UTC"))
        assertEquals("9 Oct 2026", result)
    }
}
