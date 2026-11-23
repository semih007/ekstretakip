package com.semih007.ekstretakip.logic

import com.semih007.ekstretakip.model.Card
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class Cycle(val statement: LocalDate, val due: LocalDate)

data class CalendarMarkers(
    val statements: Map<LocalDate, List<Card>>,
    val payments: Map<LocalDate, List<Card>>
)

object CardSelector {
    private fun clampToMonth(ym: YearMonth, day: Int): LocalDate =
        ym.atDay(minOf(day, ym.lengthOfMonth()))

    // Include earlier non-working dates whose adjusted date can fall in the requested range.
    private fun earliestOriginalDate(start: LocalDate, card: Card): LocalDate {
        var date = start
        while (TurkishHolidays.isBlocked(date.minusDays(1), card.shiftOnWeekend, card.shiftOnHoliday, card.shiftOnHalfDay)) {
            date = date.minusDays(1)
        }
        return date
    }

    private fun roll(date: LocalDate, card: Card): LocalDate =
        TurkishHolidays.firstOpenDayOnOrAfter(date, card.shiftOnWeekend, card.shiftOnHoliday, card.shiftOnHalfDay)

    fun cycleFor(card: Card, ym: YearMonth): Cycle {
        val statement = roll(clampToMonth(ym, card.statementDay), card)
        val due = roll(statement.plusDays(card.dueOffsetDays.toLong()), card)
        return Cycle(statement, due)
    }

    fun nextCycle(card: Card, today: LocalDate): Cycle {
        val month = YearMonth.from(earliestOriginalDate(today, card))
        val cycle = cycleFor(card, month)
        return if (cycle.statement < today) cycleFor(card, month.plusMonths(1)) else cycle
    }

    fun nextDueDate(card: Card, today: LocalDate): LocalDate {
        val offset = card.dueOffsetDays
        val firstStatement = earliestOriginalDate(today, card).minusDays(offset.toLong())
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
            val firstDay = earliestOriginalDate(month.atDay(1), card)
            var cycle = nextCycle(card, month.atDay(1))
            while (cycle.statement <= lastDay) {
                statements.getOrPut(cycle.statement) { mutableListOf() }.add(card)
                cycle = nextCycle(card, cycle.statement.plusDays(1))
            }
            val offset = card.dueOffsetDays.toLong()
            cycle = nextCycle(card, firstDay.minusDays(offset))
            var due = cycle.due
            while (due <= lastDay) {
                payments.getOrPut(due) { mutableListOf() }.add(card)
                cycle = nextCycle(card, cycle.statement.plusDays(1))
                due = cycle.due
            }
        }
        return CalendarMarkers(statements, payments)
    }
}
