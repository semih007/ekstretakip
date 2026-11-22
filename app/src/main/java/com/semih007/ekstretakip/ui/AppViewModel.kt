package com.semih007.ekstretakip.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.semih007.ekstretakip.data.CardRepository
import com.semih007.ekstretakip.model.Card
import java.time.LocalDate
import java.util.UUID

sealed interface Screen {
    object Home : Screen
    object Cards : Screen
    data class CardForm(val cardId: String?) : Screen
    object About : Screen
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CardRepository(application)
    var cards by mutableStateOf(repository.load())
        private set
    var today by mutableStateOf(LocalDate.now())
        private set
    var screen by mutableStateOf<Screen>(Screen.Home)

    fun refreshToday() {
        today = LocalDate.now()
    }

    private fun save(updated: List<Card>) {
        repository.save(updated)
        cards = updated
    }

    fun addCard(name: String, day: Int, paymentOffset: Int, adjustToBusinessDay: Boolean) =
        save(cards + Card(UUID.randomUUID().toString(), name.trim(), day, paymentOffset, adjustToBusinessDay))

    fun updateCard(id: String, name: String, day: Int, paymentOffset: Int, adjustToBusinessDay: Boolean) = save(cards.map {
        if (it.id == id) Card(id, name.trim(), day, paymentOffset, adjustToBusinessDay) else it
    })

    fun deleteCard(id: String) = save(cards.filterNot { it.id == id })

    fun nameExists(name: String, exceptId: String?): Boolean = cards.any {
        it.id != exceptId && it.name.trim().equals(name.trim(), ignoreCase = true)
    }

    fun back() {
        screen = if (screen is Screen.CardForm) Screen.Cards else Screen.Home
    }
}
