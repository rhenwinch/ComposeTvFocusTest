package com.compose_tv_focus_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.compose_tv_focus_test.ui.theme.ComposeTVFocusTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTVFocusTestTheme {
                App()
            }
        }
    }
}