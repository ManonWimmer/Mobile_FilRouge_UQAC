package com.grman.healthybet.data.groups

// Résultat du chargement firebase : groupes de l'utilisateur et groupes à découvrir
data class GroupsOverview(
    val mine: List<Group>,
    val discover: List<Group>
)
