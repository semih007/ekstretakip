package com.semih007.ekstretakip.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.semih007.ekstretakip.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CardFormScreen(vm: AppViewModel, cardId: String?) {
    val card = vm.cards.find { it.id == cardId }
    var name by rememberSaveable(cardId) { mutableStateOf(card?.name.orEmpty()) }
    var day by rememberSaveable(cardId) { mutableStateOf(card?.statementDay?.toString().orEmpty()) }
    var paymentOffset by rememberSaveable(cardId) { mutableStateOf(card?.paymentOffset?.toString().orEmpty()) }
    var adjust by rememberSaveable(cardId) { mutableStateOf(card?.adjustToBusinessDay ?: false) }
    var nameError by rememberSaveable(cardId) { mutableStateOf<Int?>(null) }
    var dayError by rememberSaveable(cardId) { mutableStateOf(false) }
    var offsetError by rememberSaveable(cardId) { mutableStateOf(false) }

    Scaffold(topBar = { BackBar(if (cardId == null) R.string.add_card else R.string.edit_card, vm::back) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(30); nameError = null },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.field_name)) },
                isError = nameError != null,
                supportingText = nameError?.let { error -> { Text(stringResource(error)) } }
            )
            OutlinedTextField(
                value = day,
                onValueChange = { day = it.filter(Char::isDigit).take(2); dayError = false },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.field_day)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = dayError,
                supportingText = if (dayError) ({ Text(stringResource(R.string.err_day_invalid)) }) else null
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .toggleable(value = adjust, role = Role.Switch, onValueChange = { adjust = it }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(stringResource(R.string.field_adjust), Modifier.weight(1f))
                Switch(checked = adjust, onCheckedChange = null)
            }
            OutlinedTextField(
                value = paymentOffset,
                onValueChange = {
                    paymentOffset = it.filter(Char::isDigit)
                    offsetError = false
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.field_payment_offset)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = offsetError,
                supportingText = if (offsetError) ({ Text(stringResource(R.string.err_offset_invalid)) }) else null
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val number = day.toIntOrNull()
                    val offset = paymentOffset.toIntOrNull()
                    nameError = when {
                        name.isBlank() -> R.string.err_name_empty
                        vm.nameExists(name, cardId) -> R.string.err_name_duplicate
                        else -> null
                    }
                    dayError = number == null || number !in 1..31
                    offsetError = offset == null || offset < 1
                    if (nameError == null && !dayError && !offsetError && number != null && offset != null) {
                        if (cardId == null) vm.addCard(name, number, offset, adjust)
                        else vm.updateCard(cardId, name, number, offset, adjust)
                        vm.screen = Screen.Cards
                    }
                }
            ) { Text(stringResource(R.string.save)) }
        }
    }
}
