package com.semih007.ekstretakip.logic

import java.time.DayOfWeek
import java.time.LocalDate

object TurkishHolidays {
    // Tam gün resmî tatiller (ay, gün): 1 Ocak, 23 Nisan, 1 Mayıs, 19 Mayıs, 15 Temmuz, 30 Ağustos, 29 Ekim
    private val fullDay = setOf(1 to 1, 4 to 23, 5 to 1, 5 to 19, 7 to 15, 8 to 30, 10 to 29)
    // Yarım gün (öğleden sonra) tatiller (ay, gün): 28 Ekim
    private val halfDay = setOf(10 to 28)

    fun isHoliday(d: LocalDate): Boolean = (d.monthValue to d.dayOfMonth) in fullDay
    fun isHalfDay(d: LocalDate): Boolean = (d.monthValue to d.dayOfMonth) in halfDay
    fun isWeekend(d: LocalDate): Boolean =
        d.dayOfWeek == DayOfWeek.SATURDAY || d.dayOfWeek == DayOfWeek.SUNDAY

    // Takvim renklendirmesi için (DEĞİŞMEDİ): hafta sonu veya tam gün resmî tatil.
    fun isNonBusinessDay(d: LocalDate): Boolean = isWeekend(d) || isHoliday(d)

    // Kartın AÇIK anahtarlarına göre bu gün engelli mi?
    fun isBlocked(d: LocalDate, weekend: Boolean, holiday: Boolean, halfDay: Boolean): Boolean =
        (weekend && isWeekend(d)) || (holiday && isHoliday(d)) || (halfDay && isHalfDay(d))

    // d dahil, engelli olmayan ilk gün (gün gün kontrol). Üç anahtar da kapalıysa d'nin kendisi.
    fun firstOpenDayOnOrAfter(d: LocalDate, weekend: Boolean, holiday: Boolean, halfDay: Boolean): LocalDate {
        var x = d
        while (isBlocked(x, weekend, holiday, halfDay)) x = x.plusDays(1)
        return x
    }
}
