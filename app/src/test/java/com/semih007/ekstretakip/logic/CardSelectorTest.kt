package com.semih007.ekstretakip.logic

import com.semih007.ekstretakip.model.Card
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CardSelectorTest {
    private fun card(name: String, day: Int, offset: Int? = null, adjust: Boolean = false) =
        Card(name, name, day, offset, adjust)

    @Test
    fun twoCardsAcrossNovemberAndYearEnd() {
        val a = card("A", 16)
        val b = card("B", 29)
        val cards = listOf(a, b)
        for (day in 1..30) {
            val today = LocalDate.of(2026, 11, day)
            assertEquals(today.toString(), if (day in 17..29) a else b, CardSelector.pickBest(cards, today))
        }
        assertEquals(b, CardSelector.pickBest(cards, LocalDate.parse("2026-12-31")))
    }

    @Test
    fun threeCardsAcrossNovemberAndYearEnd() {
        val a = card("A", 5)
        val b = card("B", 16)
        val c = card("C", 27)
        val cards = listOf(a, b, c)
        for (day in 1..30) {
            val today = LocalDate.of(2026, 11, day)
            val expected = when (day) {
                in 6..16 -> a
                in 17..27 -> b
                else -> c
            }
            assertEquals(today.toString(), expected, CardSelector.pickBest(cards, today))
        }
        assertEquals(c, CardSelector.pickBest(cards, LocalDate.parse("2026-12-31")))
    }

    @Test
    fun monthEndCards() {
        val x = card("X", 31)
        val y = card("Y", 15)
        for ((date, expected) in listOf("2026-11-15" to x, "2026-11-16" to y,
            "2026-11-30" to y, "2026-12-01" to x, "2027-02-28" to y)) {
            assertEquals(date, expected, CardSelector.pickBest(listOf(x, y), LocalDate.parse(date)))
        }
    }

    @Test
    fun nextStatementIncludesTodayAndClampsShortMonths() {
        for ((today, day, expected) in listOf(
            Triple("2028-02-29", 30, "2028-02-29"),
            Triple("2027-02-28", 29, "2027-02-28"),
            Triple("2027-03-01", 31, "2027-03-31"),
            Triple("2026-12-20", 5, "2027-01-05")
        )) {
            assertEquals(today, LocalDate.parse(expected), CardSelector.nextCycle(card("A", day), LocalDate.parse(today)).statement)
        }
    }

    @Test
    fun tiesFollowInsertionOrderEvenWhenPaymentOffsetsDiffer() {
        val a = card("A", 16, offset = 10)
        val b = card("B", 16, offset = 20)
        for (day in 1..365) {
            val today = LocalDate.ofYearDay(2026, day)
            assertEquals(a, CardSelector.pickBest(listOf(a, b), today))
            assertEquals(b, CardSelector.pickBest(listOf(b, a), today))
        }
    }

    @Test
    fun emptyAndSingleCard() {
        val only = card("A", 31)
        for (day in 1..365) {
            val today = LocalDate.ofYearDay(2026, day)
            assertNull(CardSelector.pickBest(emptyList(), today))
            assertEquals(only, CardSelector.pickBest(listOf(only), today))
        }
    }

    @Test
    fun longerPaymentOffsetDoesNotDetermineRecommendation() {
        val a = card("A", 20, offset = 1)
        val b = card("B", 12, offset = 60)
        val today = LocalDate.parse("2026-11-05")
        assertEquals(15L, CardSelector.daysUntilStatement(a, today))
        assertEquals(7L, CardSelector.daysUntilStatement(b, today))
        assertEquals(a, CardSelector.pickBest(listOf(a, b), today))
    }

    @Test
    fun adjustedStatementCanChangeRecommendation() {
        val a = card("A", 14, offset = 10, adjust = true)
        val b = card("B", 15, offset = 10)
        val today = LocalDate.parse("2026-11-14")
        assertEquals(2L, CardSelector.daysUntilStatement(a, today))
        assertEquals(1L, CardSelector.daysUntilStatement(b, today))
        assertEquals(a, CardSelector.pickBest(listOf(a, b), today))
        assertEquals(b, CardSelector.pickBest(listOf(a.copy(adjustToBusinessDay = false), b), today))
    }

    @Test
    fun datesMoveForwardAcrossConsecutiveHolidaysAndWeekend() {
        val a = card("A", 28, offset = 5, adjust = true)
        val statement = LocalDate.parse("2027-11-01")
        val due = LocalDate.parse("2027-11-08")
        assertEquals(Cycle(statement, due), CardSelector.cycleFor(a, YearMonth.of(2027, 10)))
        assertEquals(statement, CardSelector.nextCycle(a, statement).statement)
        assertEquals(due, CardSelector.nextDueDate(a, LocalDate.parse("2027-11-02")))

        val markers = CardSelector.markers(listOf(a), YearMonth.of(2027, 11))
        assertEquals(listOf(a), markers.statements[statement])
        assertEquals(listOf(a), markers.payments[due])
    }

    @Test
    fun markersIncludePaymentsForEarlierStatementsAndSkipMissingOffsets() {
        val legacy = card("Eski", 1)
        val a = card("A", 4, offset = 60, adjust = true)
        val day = LocalDate.parse("2027-01-04")
        val markers = CardSelector.markers(listOf(legacy, a), YearMonth.from(day))
        assertEquals(listOf(legacy), markers.statements[LocalDate.parse("2027-01-01")])
        assertEquals(listOf(a), markers.statements[day])
        assertEquals(listOf(a), markers.payments[day])
        assertNull(CardSelector.nextDueDate(legacy, day))
    }
}
