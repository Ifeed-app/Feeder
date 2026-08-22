package com.example.feeder.feature.feed.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.feeder.proto.content.v1.FeedVideo
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun VideoPage(
    video: FeedVideo,
    isActive: Boolean,
    isLiked: Boolean,
    likeCount: Long,
    isInteractionLoading: Boolean,
    onLikeClick: () -> Unit,
    onWatchSessionFinished: (
        watchedSeconds: Int,
        completed: Boolean,
        startedAt: Instant,
        endedAt: Instant
    ) -> Unit
) {
    val context = LocalContext.current

    val player = remember(video.id) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(
                    MediaItem.fromUri(video.videoUrl)
                )

                repeatMode = ExoPlayer.REPEAT_MODE_ONE
                prepare()
            }
    }

    DisposableEffect(isActive, video.id) {
        if (!isActive) {
            player.playWhenReady = false

            onDispose { }
        } else {
            val startedAt = Instant.now()

            player.playWhenReady = true

            onDispose {
                val endedAt = Instant.now()

                player.pause()

                val watchedDuration = Duration.between(
                    startedAt,
                    endedAt
                )

                val watchedMillis = watchedDuration
                    .toMillis()
                    .coerceAtLeast(0L)

                val watchedSeconds = watchedDuration
                    .seconds
                    .coerceAtLeast(0L)
                    .coerceAtMost(Int.MAX_VALUE.toLong())
                    .toInt()

                val videoDurationMillis = player.duration
                    .takeIf { duration ->
                        duration > 0L
                    }

                val completed = videoDurationMillis?.let { duration ->
                    watchedMillis >= (duration * 0.9).toLong()
                } ?: false

                onWatchSessionFinished(
                    watchedSeconds,
                    completed,
                    startedAt,
                    endedAt
                )
            }
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { androidContext ->
                PlayerView(androidContext).apply {
                    this.player = player
                    useController = false

                    resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.player = player
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 20.dp,
                    end = 88.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (video.description.isNotBlank()) {
                Text(
                    text = video.description,
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onLikeClick,
                enabled = !isInteractionLoading,
                modifier = Modifier.size(56.dp)
            ) {
                if (isInteractionLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = if (isLiked) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = if (isLiked) {
                            "Unlike video"
                        } else {
                            "Like video"
                        },
                        tint = if (isLiked) {
                            Color.Red
                        } else {
                            Color.White
                        },
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = formatLikeCount(likeCount),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatLikeCount(
    likeCount: Long
): String {
    return when {
        likeCount >= 1_000_000 -> {
            String.format(
                "%.1fM",
                likeCount / 1_000_000.0
            )
        }

        likeCount >= 1_000 -> {
            String.format(
                "%.1fK",
                likeCount / 1_000.0
            )
        }

        else -> {
            likeCount.toString()
        }
    }
}