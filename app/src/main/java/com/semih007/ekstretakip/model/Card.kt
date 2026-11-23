package com.semih007.ekstretakip.model

data class Card(
    val id: String,
    val name: String,
    val statementDay: Int,          // 1..31
    val dueOffsetDays: Int,         // 1..60
    val shiftOnWeekend: Boolean,    // hafta sonu engelli sayılsın mı
    val shiftOnHoliday: Boolean,    // tam gün resmî tatil engelli sayılsın mı
    val shiftOnHalfDay: Boolean     // yarım gün tatil engelli sayılsın mı
)
