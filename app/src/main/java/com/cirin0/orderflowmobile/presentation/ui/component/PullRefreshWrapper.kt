package com.cirin0.orderflowmobile.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


data class RefreshHandler(
    val isRefreshing: Boolean,
    val resetRefreshState: () -> Unit
)

val LocalRefreshHandler = compositionLocalOf {
    RefreshHandler(
        isRefreshing = false,
        resetRefreshState = {}
    )
}

@Composable
fun useRefreshHandler(): RefreshHandler = LocalRefreshHandler.current


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshWrapper(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            isRefreshing = false
        }
    }
    val refreshHandler = RefreshHandler(
        isRefreshing = isRefreshing,
        resetRefreshState = {
            isRefreshing = false
        }
    )
    val currentOnRefresh by rememberUpdatedState(onRefresh)
    CompositionLocalProvider(LocalRefreshHandler provides refreshHandler) {
        Box(modifier = modifier) {
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    currentOnRefresh()
                }
            ) {
                content()
            }
        }
    }
}
