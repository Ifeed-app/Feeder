package com.example.feeder.feature.feed.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feeder.proto.content.v1.FeedVideo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedContent(
    videos: List<FeedVideo>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { videos.size }
    )

    LaunchedEffect(
        pagerState.currentPage,
        videos.size
    ) {
        if (
            videos.isNotEmpty() &&
            pagerState.currentPage >= videos.size - 3
        ) {
            onLoadMore()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            VideoPage(
                video = videos[page],
                isActive = pagerState.currentPage == page
            )
        }

        if (isLoadingMore) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
