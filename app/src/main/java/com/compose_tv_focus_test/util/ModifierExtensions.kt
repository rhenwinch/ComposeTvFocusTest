package com.compose_tv_focus_test.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * [FocusRequesterModifiers] defines a set of modifiers which can be used for restoring focus and
 * specifying the initially focused item.
 *
 * @param [parentModifier] is added to the parent container.
 * @param [childModifier] is added to the item that needs to first gain focus.
 *
 * For example, if you want the item at index 0 to get focus for the first time,
 * you can do the following:
 *
 * LazyRow(modifier.then(modifiers.parentModifier) {
 *   item1(modifier.then(modifiers.childModifier) {...}
 *   item2 {...}
 *   item3 {...}
 *   ...
 * }
 */
data class FocusRequesterModifiers(
    val parentModifier: Modifier,
    val childModifier: Modifier
)

/**
 * Returns a set of modifiers [FocusRequesterModifiers] which can be used for restoring focus and
 * specifying the initially focused item.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun createInitialFocusRestorerModifiers(): FocusRequesterModifiers {
    val focusRequester = remember { FocusRequester() }
    val childFocusRequester = remember { FocusRequester() }

    val parentModifier = Modifier
        .focusRequester(focusRequester)
        .focusProperties {
            exit = {
                try {
                    focusRequester.saveFocusedChild()
                } catch (e: Exception) {
                    Log.e("FOCUS_ERROR", e.stackTraceToString())
                }
                FocusRequester.Default
            }
            enter = {
                try {
                    if (focusRequester.restoreFocusedChild()) FocusRequester.Cancel
                    else childFocusRequester
                } catch (e: Exception) {
                    Log.e("FOCUS_ERROR", e.stackTraceToString())
                    FocusRequester.Default
                }
            }
        }

    val childModifier = Modifier.focusRequester(childFocusRequester)

    return FocusRequesterModifiers(parentModifier, childModifier)
}

fun Modifier.ifElse(
    condition: Boolean,
    ifTrueModifier: Modifier,
    ifFalseModifier: Modifier = Modifier
): Modifier = then(if (condition) ifTrueModifier else ifFalseModifier)

@Composable
fun Modifier.focusOnMount(itemKey: String): Modifier {
    val focusRequester = remember { FocusRequester() }
    val isInitialFocusTransferred = useLocalFocusTransferredOnLaunch()
    val lastFocusedItemPerDestination = useLocalLastFocusedItemPerDestination()
    val navHostController = useLocalNavHostController()
    val currentDestination = remember(navHostController) { navHostController.currentDestination?.route }


    return this
        .focusRequester(focusRequester)
        .onGloballyPositioned {
            val lastFocusedKey = lastFocusedItemPerDestination[currentDestination]
            if (!isInitialFocusTransferred.value && lastFocusedKey == itemKey) {
                focusRequester.requestFocus()
                isInitialFocusTransferred.value = true
            }
        }
        .onFocusChanged {
            if (it.isFocused) {
                lastFocusedItemPerDestination[currentDestination ?: ""] = itemKey
                isInitialFocusTransferred.value = true
            }
        }
}