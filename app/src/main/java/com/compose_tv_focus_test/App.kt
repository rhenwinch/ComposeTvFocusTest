package com.compose_tv_focus_test

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.compose_tv_focus_test.ModifierUtils.createInitialFocusRestorerModifiers
import com.compose_tv_focus_test.ModifierUtils.ifElse

data class FocusedItem(
    val row: Int,
    val column: Int
)

@Composable
fun App() {
    val navController = rememberNavController()
    val lastFocusedItem = remember { mutableStateOf(FocusedItem(0, 0)) }

    // To avoid requesting focus on the item more than once
    val itemAlreadyFocused = remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            LaunchedEffect(Unit) {
                // reset the value when home is launched
                itemAlreadyFocused.value = false
            }
            HomePage(
                navController = navController,
                lastFocusedItem = lastFocusedItem,
                itemAlreadyFocused = itemAlreadyFocused,
            )
        }
        composable("movie") {
            BackHandler {
                navController.popBackStack()
            }
            Button(onClick = { navController.popBackStack() }) {
                Text("Home")
            }
        }
    }
}

@Composable
fun HomePage(
    navController: NavController,
    lastFocusedItem: MutableState<FocusedItem>,
    itemAlreadyFocused: MutableState<Boolean>
) {
    TvLazyColumn(
        Modifier.fillMaxSize(),
    ) {
        items(15) { row ->
            MyRow(
                row = row,
                navController = navController,
                lastFocusedItem = lastFocusedItem,
                itemAlreadyFocused = itemAlreadyFocused,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyRow(
    row: Int,
    navController: NavController,
    lastFocusedItem: MutableState<FocusedItem>,
    itemAlreadyFocused: MutableState<Boolean>
) {
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
                val focusRequester = remember { FocusRequester() }

                Card(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.Cyan)
                        .focusRequester(focusRequester)
                        .ifElse(
                            condition = column == 0,
                            ifTrueModifier = focusRestorerModifiers.childModifier
                        )
                        .onPlaced {
                            val shouldFocusThisItem = lastFocusedItem.value.row == row
                                    && lastFocusedItem.value.column == column
                                    && !itemAlreadyFocused.value

                            if (shouldFocusThisItem) {
                                focusRequester.requestFocus()
                            }
                        }
                        .onFocusChanged {
                            if (it.isFocused) {
                                lastFocusedItem.value = FocusedItem(
                                    row = row,
                                    column = column
                                )
                                itemAlreadyFocused.value = true
                            }
                        }
                        .focusProperties {
                            if (row == 0) {
                                up = FocusRequester.Cancel
                            } else if (row == 15) {
                                down = FocusRequester.Cancel
                            }
                        },
                    onClick = { navController.navigate("movie") }
                ) {}
            }
        }
    }
}