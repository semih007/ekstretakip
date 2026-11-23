package com.semih007.ekstretakip.logic

import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class TurkishHolidaysTest {
    private val half = LocalDate.parse("2026-10-28")
    private val saturday = LocalDate.parse("2026-11-07")

    @Test
    fun holidayTypesAndCalendarColoring() {
        assertTrue(TurkishHolidays.isHalfDay(half))
        assertFalse(TurkishHolidays.isHalfDay(half.minusDays(1)))
        assertFalse(TurkishHolidays.isHalfDay(half.plusDays(1)))
        assertFalse(TurkishHolidays.isHoliday(half))
        assertTrue(TurkishHolidays.isHoliday(half.plusDays(1)))
        assertFalse(TurkishHolidays.isNonBusinessDay(half))
        assertTrue(TurkishHolidays.isNonBusinessDay(half.plusDays(1)))
        assertTrue(TurkishHolidays.isNonBusinessDay(saturday))
    }

    @Test
    fun blockedOnlyWhenMatchingFlagIsEnabled() {
        assertTrue(TurkishHolidays.isBlocked(half, false, false, true))
        assertFalse(TurkishHolidays.isBlocked(half, true, true, false))
        assertFalse(TurkishHolidays.isBlocked(saturday, false, true, true))
        assertTrue(TurkishHolidays.isBlocked(saturday, true, false, false))
    }

    @Test
    fun stopsAtFirstOpenDateIncludingStart() {
        val monday = LocalDate.parse("2026-11-16")
        val sunday = LocalDate.parse("2025-05-18")
        assertEquals(saturday.plusDays(2), TurkishHolidays.firstOpenDayOnOrAfter(saturday, true, true, true))
        assertEquals(saturday, TurkishHolidays.firstOpenDayOnOrAfter(saturday, false, true, true))
        assertEquals(monday, TurkishHolidays.firstOpenDayOnOrAfter(monday, true, true, true))
        assertEquals(sunday.plusDays(1), TurkishHolidays.firstOpenDayOnOrAfter(sunday, true, false, false))
        assertEquals(sunday.plusDays(2), TurkishHolidays.firstOpenDayOnOrAfter(sunday, true, true, false))
    }
}
