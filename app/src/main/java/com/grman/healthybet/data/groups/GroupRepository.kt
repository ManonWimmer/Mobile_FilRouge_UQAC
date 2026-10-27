package com.grman.healthybet.data.groups

interface GroupRepository {
    suspend fun getGroups(): GroupsOverview
    suspend fun createGroup(name: String, emoji: String, description: String)
    suspend fun joinGroup(groupId: String)
    suspend fun leaveGroup(groupId: String)
}