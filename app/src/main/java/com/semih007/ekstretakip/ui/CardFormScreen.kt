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
    var paymentOffset by rememberSaveable(cardId) { mutableStateOf(card?.dueOffsetDays?.toString().orEmpty()) }
    var weekend by rememberSaveable(cardId) { mutableStateOf(card?.shiftOnWeekend ?: true) }
    var holiday by rememberSaveable(cardId) { mutableStateOf(card?.shiftOnHoliday ?: true) }
    var halfDay by rememberSaveable(cardId) { mutableStateOf(card?.shiftOnHalfDay ?: false) }
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShiftOption(R.string.switch_weekend_title, R.string.switch_weekend_desc, weekend) { weekend = it }
                ShiftOption(R.string.switch_holiday_title, R.string.switch_holiday_desc, holiday) { holiday = it }
                ShiftOption(R.string.switch_half_title, R.string.switch_half_desc, halfDay) { halfDay = it }
            }
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
                    offsetError = offset == null || offset !in 1..60
                    if (nameError == null && !dayError && !offsetError && number != null && offset != null) {
                        if (cardId == null) vm.addCard(name, number, offset, weekend, holiday, halfDay)
                        else vm.updateCard(cardId, name, number, offset, weekend, holiday, halfDay)
                        vm.screen = Screen.Cards
                    }
                }
            ) { Text(stringResource(R.string.save)) }
        }
    }
}

@Composable
private fun ShiftOption(title: Int, description: Int, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().toggleable(value = checked, role = Role.Switch, onValueChange = onChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(title))
            Text(stringResource(description), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = null)
    }
}
