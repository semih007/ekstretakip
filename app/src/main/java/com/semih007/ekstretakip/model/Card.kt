package com.semih007.ekstretakip.model

data class Card(
    val id: String,
    val name: String,
    val statementDay: Int,
    val paymentOffset: Int? = null,
    val adjustToBusinessDay: Boolean = false
)
