package com.example.feeder.feature.feed.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feeder.feature.interaction.presentation.InteractionViewModel
import com.example.feeder.feature.interaction.presentation.VideoInteractionState
import com.example.feeder.proto.content.v1.FeedVideo

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedContent(
    videos: List<FeedVideo>,
    isLoadingMore: Boolean,
    debugMessage: String?,
    interactionViewModel: InteractionViewModel,
    onLoadMore: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { videos.size }
    )

    val interactionStates by
    interactionViewModel.interactionStates.collectAsState()

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

            val video = videos[page]

            Log.d(
                "VIDEO_DEBUG",
                "id=${video.id}, storageKey=${video.stroageKey}, title=${video.title}"
            )

            val interactionState =
                interactionStates[video.id]
                    ?: VideoInteractionState(
                        videoId = video.id
                    )

            LaunchedEffect(video.id) {
                interactionViewModel.loadInteractionSummary(
                    videoId = video.id
                )
            }

            VideoPage(
                video = video,
                isActive = pagerState.currentPage == page,
                isLiked = interactionState.isLiked,
                likeCount = interactionState.likeCount,
                isInteractionLoading = interactionState.isLoading,

                onLikeClick = {
                    interactionViewModel.toggleLike(
                        videoId = video.id
                    )
                },

                onWatchSessionFinished = {
                        watchedSeconds,
                        completed,
                        startedAt,
                        endedAt ->

                    val wasQuickSkip =
                        !completed &&
                                watchedSeconds < 3

                    if (wasQuickSkip) {
                        interactionViewModel.recordSkip(
                            videoId = video.id
                        )
                    }

                    if (watchedSeconds > 0) {
                        interactionViewModel.recordWatchSession(
                            videoId = video.id,
                            watchedSeconds = watchedSeconds,
                            completed = completed,
                            startedAt = startedAt,
                            endedAt = endedAt
                        )
                    }
                }
            )
        }

        debugMessage?.let { message ->
            Text(
                text = message,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        color = Color.Black.copy(
                            alpha = 0.8f
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                color = Color.White,
                fontSize = 14.sp
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