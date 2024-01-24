package com.compose_tv_focus_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
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
                        HomePage(
                            navController = navController,
                        )
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePage(
    navController: NavController,
) {
    TvLazyColumn(
        Modifier.fillMaxSize(),
    ) {
        items(15) { row ->
            val focusRestorerModifiers = createInitialFocusRestorerModifiers()

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Row $row",
                    style = MaterialTheme.typography.bodySmall
                )

                TvLazyRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = focusRestorerModifiers.parentModifier
                        .padding(bottom = 10.dp)
                ) {
                    items(15) { column ->
                        val key = "row=$row, column=$column"

                        Card(
                            modifier = Modifier
                                .size(150.dp)
                                .ifElse(
                                    condition = column == 0,
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
                            onClick = { navController.navigate("movie") },
                            colors = CardDefaults.colors(containerColor = Color.Cyan)
                        ) {
                            Text("Column = $column")
                        }
                    }
                }
            }
        }
    }
}