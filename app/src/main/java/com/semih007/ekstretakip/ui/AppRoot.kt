package com.semih007.ekstretakip.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.semih007.ekstretakip.R

@Composable
fun AppRoot(vm: AppViewModel) {
    BackHandler(enabled = vm.screen != Screen.Home, onBack = vm::back)
    when (val screen = vm.screen) {
        Screen.Home -> HomeScreen(vm)
        Screen.Cards -> CardsScreen(vm)
        is Screen.CardForm -> CardFormScreen(vm, screen.cardId)
        Screen.About -> AboutScreen(vm::back)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackBar(title: Int, back: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(title)) },
        navigationIcon = {
            IconButton(onClick = back) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_desc))
            }
        }
    )
}
