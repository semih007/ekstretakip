package com.semih007.ekstretakip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.semih007.ekstretakip.ui.AppRoot
import com.semih007.ekstretakip.ui.AppViewModel
import com.semih007.ekstretakip.ui.theme.EkstreTakipTheme

class MainActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this)[AppViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EkstreTakipTheme { AppRoot(vm) } }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshToday()
    }
}
