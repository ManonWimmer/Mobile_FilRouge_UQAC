package com.grman.healthybet.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.ui.theme.GraySubtitle

@Composable
fun CloseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = stringResource(R.string.close_icon_description),
        tint = GraySubtitle,
        modifier = modifier
            .size(24.dp)
            .clickable { onClick() }
    )
}