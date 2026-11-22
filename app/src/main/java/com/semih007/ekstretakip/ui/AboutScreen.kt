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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import com.semih007.ekstretakip.BuildConfig

@Composable
fun AboutScreen(back: () -> Unit) {
    Scaffold(topBar = { BackBar(R.string.about_title, back) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.about_intro), style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.about_github_label), style = MaterialTheme.typography.labelLarge)
            SelectionContainer { Text(stringResource(R.string.about_github_url)) }
            Text(stringResource(R.string.about_version_format, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
