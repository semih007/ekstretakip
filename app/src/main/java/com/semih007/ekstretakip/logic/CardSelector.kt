package com.semih007.ekstretakip.logic

import com.semih007.ekstretakip.model.Card
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class Cycle(val statement: LocalDate, val due: LocalDate?)

data class CalendarMarkers(
    val statements: Map<LocalDate, List<Card>>,
    val payments: Map<LocalDate, List<Card>>
)

object CardSelector {
    private val fixedHolidays = setOf(
        MonthDay.of(1, 1), MonthDay.of(4, 23), MonthDay.of(5, 1),
        MonthDay.of(5, 19), MonthDay.of(7, 15), MonthDay.of(8, 30),
        MonthDay.of(10, 28), MonthDay.of(10, 29)
    )

    private fun isBusinessDay(date: LocalDate): Boolean =
        date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY &&
            MonthDay.from(date) !in fixedHolidays

    private fun adjustedDate(date: LocalDate, adjust: Boolean): LocalDate {
        var result = date
        while (adjust && !isBusinessDay(result)) result = result.plusDays(1)
        return result
    }

    // Include earlier non-working dates whose adjusted date can fall in the requested range.
    private fun earliestOriginalDate(start: LocalDate, adjust: Boolean): LocalDate {
        var date = start
        while (adjust && !isBusinessDay(date.minusDays(1))) date = date.minusDays(1)
        return date
    }

    fun cycleFor(card: Card, month: YearMonth): Cycle {
        val originalStatement = month.atDay(minOf(card.statementDay, month.lengthOfMonth()))
        val statement = adjustedDate(originalStatement, card.adjustToBusinessDay)
        val due = card.paymentOffset?.let { days ->
            adjustedDate(statement.plusDays(days.toLong()), card.adjustToBusinessDay)
        }
        return Cycle(statement, due)
    }

    fun nextCycle(card: Card, today: LocalDate): Cycle {
        val month = YearMonth.from(earliestOriginalDate(today, card.adjustToBusinessDay))
        val cycle = cycleFor(card, month)
        return if (cycle.statement < today) cycleFor(card, month.plusMonths(1)) else cycle
    }

    fun nextDueDate(card: Card, today: LocalDate): LocalDate? {
        val offset = card.paymentOffset ?: return null
        val firstStatement = earliestOriginalDate(today, card.adjustToBusinessDay).minusDays(offset.toLong())
        return nextCycle(card, firstStatement).due
    }

    fun daysUntilStatement(card: Card, today: LocalDate): Long =
        ChronoUnit.DAYS.between(today, nextCycle(card, today).statement)

    // En uygun kart: bugünkü harcamanın düşeceği ekstre kesim tarihi
    // (ötelenmişse ötelenmiş hâli) bugünden en uzak olan kart.
    fun pickBest(cards: List<Card>, today: LocalDate): Card? =
        cards.maxByOrNull { daysUntilStatement(it, today) }

    fun markers(cards: List<Card>, month: YearMonth): CalendarMarkers {
        val statements = mutableMapOf<LocalDate, MutableList<Card>>()
        val payments = mutableMapOf<LocalDate, MutableList<Card>>()
        val lastDay = month.atEndOfMonth()
        for (card in cards) {
            val firstDay = earliestOriginalDate(month.atDay(1), card.adjustToBusinessDay)
            var cycle = nextCycle(card, month.atDay(1))
            while (cycle.statement <= lastDay) {
                statements.getOrPut(cycle.statement) { mutableListOf() }.add(card)
                cycle = nextCycle(card, cycle.statement.plusDays(1))
            }
            val offset = card.paymentOffset?.toLong() ?: continue
            cycle = nextCycle(card, firstDay.minusDays(offset))
            var due = cycle.due
            while (due != null && due <= lastDay) {
                payments.getOrPut(due) { mutableListOf() }.add(card)
                cycle = nextCycle(card, cycle.statement.plusDays(1))
                due = cycle.due
            }
        }
        return CalendarMarkers(statements, payments)
    }
}
