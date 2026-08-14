package com.example.progettowoc.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.progettowoc.ui.theme.GreenAcceso
import androidx.compose.ui.draw.clip


// ripple non funziona perche on click è dentro modifier, non ce soluzione per ora con compose?
@Composable
fun ElevatedCardComp(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    contents: @Composable () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .then(
                if (isSelected)
                    Modifier.border(
                        width = 2.dp,
                        color = GreenAcceso,
                        shape = RoundedCornerShape(12.dp)
                    )
                else Modifier
            )
            .then(
                if (onClick != null || onLongClick != null)
                    Modifier
                        .combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = { onLongClick?.invoke() }
                    )
                else Modifier
            ),
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            contents()
        }
    }
}