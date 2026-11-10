package com.grman.healthybet.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.items.common.BackButton
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail

// Layout commun : contenu centré, scrollable, adapté au clavier.
// onBackClick = null : pas de bouton retour
@Composable
fun AuthLayout(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // BoxWithConstraints donne la hauteur dispo (maxHeight) pour centrer le contenu
    // même quand la Column est scrollable
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(top = 72.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }

        if (onBackClick != null) {
            BackButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp)
            )
        }
    }
}

// Logo + nom de l'app
@Composable
fun LogoHeader(nameStyle: TextStyle = MaterialTheme.typography.titleMedium) {
    Box(
        modifier = Modifier.size(64.dp).background(GreenAccent, MediumRoundedCornerShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.logo_app_description),
            modifier = Modifier.size(40.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = stringResource(R.string.app_name),
        color = Color.White,
        style = nameStyle,
        textAlign = TextAlign.Center
    )
}

// Titre + sous-titre centrés
@Composable
fun AuthTitle(title: String, subtitle: String) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = subtitle,
        color = GraySubtitle,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

// Message d'erreur animé (rien d'affiché si message = null)
@Composable
fun ErrorMessage(message: String?) {
    AnimatedVisibility(visible = message != null) {
        Column {
            Text(text = message.orEmpty(), color = RedFail, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}