package com.compose_tv_focus_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester

object ModifierUtils {
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
                    focusRequester.saveFocusedChild()
                    FocusRequester.Default
                }
                enter = {
                    if (focusRequester.restoreFocusedChild()) FocusRequester.Cancel
                    else childFocusRequester
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
}