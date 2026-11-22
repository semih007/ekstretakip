package com.semih007.ekstretakip.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun EkstreTakipTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colors = if (Build.VERSION.SDK_INT >= 31) {
        val context = LocalContext.current
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (dark) {
        darkColorScheme(primary = Color(0xFF8FD0EA))
    } else {
        lightColorScheme(primary = Color(0xFF1B5E75))
    }
    MaterialTheme(colorScheme = colors, content = content)
}
