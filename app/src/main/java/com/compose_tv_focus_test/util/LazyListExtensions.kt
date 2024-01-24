package com.compose_tv_focus_test.util

import androidx.tv.foundation.lazy.list.TvLazyListState

fun TvLazyListState.shouldPaginate(toDeduct: Int = 6) = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -9) >= (layoutInfo.totalItemsCount - toDeduct) || !canScrollForward