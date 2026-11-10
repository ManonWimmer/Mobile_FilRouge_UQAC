package com.grman.healthybet.items.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail
import com.grman.healthybet.items.common.CloseButton

// Titre + bouton fermer (si onClose) + bouton libre à droite (si trailing)
@Composable
fun ScreenHeader(
    title: String,
    onClose: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        if (onClose != null) CloseButton(onClick = onClose)
        trailing()
    }
}

// Titre de section (avec espace dessous)
@Composable
fun SectionTitle(text: String, spacing: Dp = 20.dp) {
    Text(text = text, color = Color.White, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(spacing))
}

// Texte affiché quand une liste est vide
@Composable
fun EmptyText(text: String, centered: Boolean = false) {
    Text(
        text = text,
        color = GraySubtitle,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = if (centered) TextAlign.Center else TextAlign.Unspecified,
        modifier = if (centered) Modifier.fillMaxWidth().padding(vertical = 32.dp) else Modifier
    )
}

// Bouton plein (loading = spinner à la place du texte)
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    enabled: Boolean = true,
    loading: Boolean = false,
    fillWidth: Boolean = true,
    containerColor: Color = GreenAccent,
    contentColor: Color = Color.Unspecified, // Unspecified = couleur par défaut du thème
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height),
        shape = LittleRoundedCornerShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.3f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.Black)
        } else {
            Text(text = text, style = textStyle)
        }
    }
}

// Bouton avec contour
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(height),
        shape = LittleRoundedCornerShape,
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(text = text, style = textStyle)
    }
}

// Bouton carré avec une icône (header : quitter, ajouter...)
@Composable
fun SquareIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FieldBg,
    tint: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(containerColor, MediumRoundedCornerShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = tint, modifier = Modifier.size(20.dp))
    }
}

// Onglets (ex : Mes groupes / Découvrir)
@Composable
fun SegmentedTabs(tabs: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FieldBg, MediumRoundedCornerShape)
            .border(1.dp, Color.White.copy(alpha = 0.1f), MediumRoundedCornerShape)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected) GreenAccent.copy(alpha = 0.15f) else Color.Transparent,
                        LittleRoundedCornerShape
                    )
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) GreenAccent else GraySubtitle,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// Pop-up de confirmation (action dangereuse en rouge)
@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmText, color = RedFail) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

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

@Composable
internal fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(FieldBg, MediumRoundedCornerShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}