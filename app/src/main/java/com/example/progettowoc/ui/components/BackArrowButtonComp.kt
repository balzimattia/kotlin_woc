package com.example.progettowoc.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.progettowoc.R


@Composable
fun BackArrowButtonComp(
    onBackClick: () -> Unit
) {
    IconButton(onClick = onBackClick) {
        Icon(
            painter = painterResource(R.drawable.arrow_left),
            contentDescription = "Indietro",
            modifier = Modifier.size(45.dp)
        )
    }
}