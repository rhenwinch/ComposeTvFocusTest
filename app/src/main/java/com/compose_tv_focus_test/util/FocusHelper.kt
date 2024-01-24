package com.compose_tv_focus_test.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
private val LocalLastFocusedItemPerDestination = compositionLocalOf<MutableMap<String, String>?> { null }
private val LocalFocusTransferredOnLaunch = compositionLocalOf<MutableState<Boolean>?> { null }
private val LocalNavHostController = compositionLocalOf<NavHostController?> { null }

@Composable
fun LocalLastFocusedItemPerDestinationProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLastFocusedItemPerDestination provides remember { mutableMapOf() }, content = content)
}

@Composable
fun LocalFocusTransferredOnLaunchProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFocusTransferredOnLaunch provides remember { mutableStateOf(false) }, content = content)
}

@Composable
fun LocalNavHostControllerProvider(navHostController: NavHostController, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavHostController provides navHostController, content = content)
}

@Composable
fun useLocalLastFocusedItemPerDestination(): MutableMap<String, String> {
    return LocalLastFocusedItemPerDestination.current ?: throw RuntimeException("Please wrap your app with LocalLastFocusedItemPerDestinationProvider")
}

@Composable
fun useLocalFocusTransferredOnLaunch(): MutableState<Boolean> {
    return LocalFocusTransferredOnLaunch.current ?: throw RuntimeException("Please wrap your app with LocalLastFocusedItemPerDestinationProvider")
}

@Composable
fun useLocalNavHostController(): NavHostController {
    return LocalNavHostController.current ?: throw RuntimeException("Please wrap your app with LocalNavHostControllerProvider")
}