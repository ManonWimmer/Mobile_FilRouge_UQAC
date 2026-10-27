package com.grman.healthybet.data.groups

import com.google.firebase.firestore.DocumentId

data class Group(
    @DocumentId val id: String = "",   // id du document rempli automatiquement
    val name: String = "",
    val emoji: String = "",
    val description: String = "",
    val ownerId: String = "",
    val members: List<String> = emptyList() // uid des membres
) {
    val memberCount: Int get() = members.size

    val memberEmojis: List<String> get() = members.take(3).map { "😄" } // todo: photos de profil via la collection "members"
}
