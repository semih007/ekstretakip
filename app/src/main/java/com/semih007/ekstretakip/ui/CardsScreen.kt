package com.semih007.ekstretakip.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.semih007.ekstretakip.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.saveable.rememberSaveable
import com.semih007.ekstretakip.logic.CardSelector
import java.time.format.DateTimeFormatter

private val cardDateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")

@Composable
fun CardsScreen(vm: AppViewModel) {
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }
    Scaffold(
        topBar = { BackBar(R.string.cards_title, vm::back) },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.screen = Screen.CardForm(null) }) {
                Icon(Icons.Default.Add, stringResource(R.string.add_card))
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (vm.cards.isEmpty()) {
                Text(stringResource(R.string.cards_empty), Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
                    items(vm.cards, key = { it.id }) { card ->
                        val edit = { vm.screen = Screen.CardForm(card.id) }
                        ListItem(
                            modifier = Modifier.clickable(onClick = edit),
                            headlineContent = { Text(card.name) },
                            supportingContent = {
                                val statement = CardSelector.nextCycle(card, vm.today).statement
                                val due = CardSelector.nextDueDate(card, vm.today)
                                Column {
                                    Text(stringResource(R.string.statement_day_format, card.statementDay))
                                    Text(stringResource(R.string.next_statement_format, statement.format(cardDateFormatter)))
                                    Text(
                                        stringResource(R.string.next_payment_format, due.format(cardDateFormatter))
                                    )
                                }
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = edit) {
                                        Icon(Icons.Default.Edit, stringResource(R.string.edit_desc))
                                    }
                                    IconButton(onClick = { deleteId = card.id }) {
                                        Icon(Icons.Default.Delete, stringResource(R.string.delete_desc))
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    vm.cards.find { it.id == deleteId }?.let { card ->
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_message, card.name)) },
            confirmButton = {
                TextButton(onClick = { vm.deleteCard(card.id); deleteId = null }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteId = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
