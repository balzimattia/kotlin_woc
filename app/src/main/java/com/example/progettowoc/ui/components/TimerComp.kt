package com.example.progettowoc.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.progettowoc.ui.theme.LightBlue
import kotlinx.coroutines.delay


@Composable
fun TimerComp(
    key: Int = 0,
    secondsRemaining: Int = 0,
    threshold: Int = 0,
    isRunning: Boolean = false,
    onTick: (Int) -> Unit = {},
    onFinish: () -> Unit = {},
    content: @Composable (String, Int) -> Unit
) {
    var formattedTime by rememberSaveable(key) { mutableStateOf(formatTime(secondsRemaining)) }
    var remaining by rememberSaveable(key) { mutableIntStateOf(secondsRemaining) }

    LaunchedEffect(key, isRunning) {
        if (isRunning) {
            val startFrom = remaining // riprende da dove era rimasto
            val startTime = System.currentTimeMillis()
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val remainingSeconds = startFrom - (elapsed / 1000).toInt()
                remaining = remainingSeconds
                formattedTime = formatTime(remainingSeconds)
                if (remainingSeconds <= threshold) {
                    onFinish()
                    break
                }
                onTick(remainingSeconds)
                delay(1000)
            }
        }
    }

    content(formattedTime, remaining)
}


@Composable
fun TimerDisplay(
    formattedTime: String,
    remaining: Int,
    total: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = if (remaining <= 10 && remaining > 0)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (total > 0) {
            LinearProgressIndicator(
                progress = { remaining.toFloat() / total.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .height(6.dp),
                color = LightBlue
            )
        }
    }
}


@SuppressLint("DefaultLocale")
private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}


@Preview(showBackground = true)
@Composable
private fun TimerPreview() {
    TimerComp() { formattedTime, remaining ->
        TimerDisplay(
            formattedTime = formattedTime,
            remaining = remaining,
            total = 120
        )
    }
}