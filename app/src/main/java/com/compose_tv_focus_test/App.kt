package com.compose_tv_focus_test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.compose_tv_focus_test.util.LocalFocusTransferredOnLaunchProvider
import com.compose_tv_focus_test.util.LocalLastFocusedItemPerDestinationProvider
import com.compose_tv_focus_test.util.LocalNavHostControllerProvider
import com.compose_tv_focus_test.util.createInitialFocusRestorerModifiers
import com.compose_tv_focus_test.util.focusOnMount
import com.compose_tv_focus_test.util.ifElse
import com.compose_tv_focus_test.util.shouldPaginate
import com.compose_tv_focus_test.util.useLocalLastFocusedItemPerDestination
import com.compose_tv_focus_test.util.useLocalNavHostController

data class FocusedItem(
    val row: Int,
    val column: Int
)

@Composable
fun App() {
    val navController = rememberNavController()

    LocalNavHostControllerProvider(navController) {
        LocalLastFocusedItemPerDestinationProvider {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    LocalFocusTransferredOnLaunchProvider {
                        HomePage()
                    }
                }
                composable("movie") {
                    LocalFocusTransferredOnLaunchProvider {
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Home")
                        }
                    }
                }
            }
        }
    }
}

const val KEY_FORMAT = "row=%d, column=%d"
const val MAX_ITEMS = 15

@Composable
fun HomePage() {
    val lastItemFocused = useLocalLastFocusedItemPerDestination()

    val rowListState = rememberTvLazyListState()

    var rowItems by remember { mutableIntStateOf(MAX_ITEMS) }

    LaunchedEffect(Unit) {
        val defaultValue = String.format(KEY_FORMAT, 0, 0)
        lastItemFocused.getOrPut("home") { defaultValue }
    }


    val shouldStartPaginate by remember {
        derivedStateOf {
            rowListState.shouldPaginate()
        }
    }

    LaunchedEffect(shouldStartPaginate) {
        if(shouldStartPaginate) {
            rowItems += MAX_ITEMS
        }
    }

    TvLazyColumn(
        Modifier.fillMaxSize(),
        state = rowListState
    ) {
        items(rowItems) { i ->
            val row = i % MAX_ITEMS

            Row(row)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Row(row: Int) {
    val navController = useLocalNavHostController()

    val focusRestorerModifiers = createInitialFocusRestorerModifiers()

    val columnListState = rememberTvLazyListState()
    val firstInitialIndex = remember { columnListState.firstVisibleItemIndex }
    var columnItems by remember { mutableIntStateOf(MAX_ITEMS) }

    val shouldStartPaginate by remember {
        derivedStateOf {
            columnListState.shouldPaginate()
        }
    }

    LaunchedEffect(shouldStartPaginate) {
        if(shouldStartPaginate) {
            columnItems += MAX_ITEMS
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Row $row",
            style = MaterialTheme.typography.bodySmall
        )

        TvLazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            state = columnListState,
            modifier = focusRestorerModifiers.parentModifier
                .padding(bottom = 10.dp),
            pivotOffsets = PivotOffsets(0.003F),
            contentPadding = PaddingValues(start = 3.dp)
        ) {
            items(columnItems) { i ->
                val column = i % MAX_ITEMS
                val key = String.format(KEY_FORMAT, row, column)

                Card(
                    modifier = Modifier
                        .size(150.dp)
                        .ifElse(
                            condition = column == firstInitialIndex,
                            ifTrueModifier = focusRestorerModifiers.childModifier
                        )
                        .focusOnMount(key)
                        .focusProperties {
                            if (row == 0) {
                                up = FocusRequester.Cancel
                            } else if (row == 15) {
                                down = FocusRequester.Cancel
                            }
                        },
                    scale = CardDefaults.scale(focusedScale = 1F),
                    onClick = { navController.navigate("movie") },
                    colors = CardDefaults.colors(containerColor = Color.Cyan)
                ) {
                    Text("Column = $column")
                }
            }
        }
    }
}
