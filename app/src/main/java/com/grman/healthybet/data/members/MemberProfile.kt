package com.grman.healthybet.data.members

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grman.healthybet.R

data class MemberProfile(val username: String, val emoji: String)

// Récupère le pseudo et l'emoji de l'utilisateur connecté depuis Firestore.
// Valeurs par défaut affichées tant que la réponse n'est pas arrivée.
@Composable
fun rememberMemberProfile(): MemberProfile {
    val user = FirebaseAuth.getInstance().currentUser
    val defaultUsername = stringResource(R.string.username_not_found)
    val defaultEmoji = stringResource(R.string.default_emoji)

    var profile by remember {
        mutableStateOf(MemberProfile(user?.displayName ?: defaultUsername, defaultEmoji))
    }

    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("members")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                profile = MemberProfile(
                    username = doc.getString("username") ?: defaultUsername,
                    emoji = doc.getString("emoji") ?: defaultEmoji
                )
            }
    }

    return profile
}