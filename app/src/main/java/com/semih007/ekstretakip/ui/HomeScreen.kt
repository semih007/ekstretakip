package com.semih007.ekstretakip.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.semih007.ekstretakip.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.semih007.ekstretakip.logic.CardSelector
import com.semih007.ekstretakip.model.Card
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val calendarLocale = Locale.forLanguageTag("tr-TR")
private val monthFormatter = DateTimeFormatter.ofPattern("LLLL uuuu", calendarLocale)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: AppViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val card = CardSelector.pickBest(vm.cards, vm.today)
    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (card == null) {
                Text(stringResource(R.string.empty_hint), Modifier.align(Alignment.Center).padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            } else {
                Column(
                    Modifier.fillMaxSize().padding(top = 64.dp)
                        .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(card.name, style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                        maxLines = 3, overflow = TextOverflow.Ellipsis)
                    CardCalendar(vm.cards, vm.today)
                }
            }
            TopAppBar(
                title = {},
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, stringResource(R.string.menu_desc))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.menu_cards)) }, onClick = {
                                expanded = false
                                vm.screen = Screen.Cards
                            })
                            DropdownMenuItem(text = { Text(stringResource(R.string.menu_about)) }, onClick = {
                                expanded = false
                                vm.screen = Screen.About
                            })
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun CardCalendar(cards: List<Card>, today: LocalDate) {
    var monthOffset by rememberSaveable { mutableIntStateOf(0) }
    var selectedDay by rememberSaveable(today.toEpochDay()) { mutableStateOf(today.toEpochDay()) }
    val month = YearMonth.from(today).plusMonths(monthOffset.toLong())
    val selectedDate = LocalDate.ofEpochDay(selectedDay)
    val markers = remember(cards, month) { CardSelector.markers(cards, month) }
    val firstColumn = month.atDay(1).dayOfWeek.value - 1
    val slots = firstColumn + month.lengthOfMonth()
    val colors = MaterialTheme.colorScheme

    Column(Modifier.widthIn(max = 420.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                monthOffset--
                selectedDay = month.minusMonths(1).atDay(1).toEpochDay()
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, stringResource(R.string.calendar_previous))
            }
            Text(month.format(monthFormatter), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = {
                monthOffset++
                selectedDay = month.plusMonths(1).atDay(1).toEpochDay()
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, stringResource(R.string.calendar_next))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            for (weekday in DayOfWeek.values()) {
                Text(weekday.getDisplayName(TextStyle.SHORT, calendarLocale), Modifier.weight(1f),
                    textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium)
            }
        }
        for (week in 0 until (slots + 6) / 7) {
            Row(Modifier.fillMaxWidth()) {
                for (column in 0..6) {
                    val day = week * 7 + column - firstColumn + 1
                    if (day !in 1..month.lengthOfMonth()) {
                        Spacer(Modifier.weight(1f))
                        continue
                    }
                    val date = month.atDay(day)
                    val selected = date == selectedDate
                    Column(
                        Modifier.weight(1f).heightIn(min = 40.dp)
                            .background(
                                if (selected) colors.surfaceVariant else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedDay = date.toEpochDay() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(day.toString(), fontWeight = if (date == today) FontWeight.Bold else FontWeight.Normal)
                        Row(Modifier.height(4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            if (date in markers.statements) CalendarDot(colors.primary)
                            if (date in markers.payments) CalendarDot(colors.tertiary)
                        }
                    }
                }
            }
        }
        val statementLabel = stringResource(R.string.calendar_statement)
        val paymentLabel = stringResource(R.string.calendar_payment)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CalendarLegend(statementLabel, colors.primary)
            CalendarLegend(paymentLabel, colors.tertiary)
        }
        for (card in markers.statements[selectedDate].orEmpty()) {
            Text(stringResource(R.string.calendar_event_format, statementLabel, card.name))
        }
        for (card in markers.payments[selectedDate].orEmpty()) {
            Text(stringResource(R.string.calendar_event_format, paymentLabel, card.name))
        }
    }
}

@Composable
private fun CalendarLegend(label: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        CalendarDot(color)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun CalendarDot(color: Color) {
    Box(Modifier.size(4.dp).background(color, RoundedCornerShape(50)))
}
