package com.example.healthybet.items

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.healthybet.ui.theme.GreenAccent
import com.example.healthybet.ui.theme.GraySubtitle
import com.example.healthybet.ui.theme.FieldBg

enum class BottomNavItem(val label: String, val iconSelected: ImageVector, val iconUnselected: ImageVector){
    Accueil("Accueil", Icons.Filled.Home, Icons.Outlined.Home),
    Reveils("Réveils", Icons.Filled.WatchLater, Icons.Outlined.WatchLater),
    Groupes("Groupes", Icons.Filled.Groups, Icons.Outlined.Groups),
    Profil("Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun BottomNavBar(selected: BottomNavItem, onItemSelected: (BottomNavItem) -> Unit){
    NavigationBar(containerColor = FieldBg) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = item == selected

            // Change icon color on click
            NavigationBarItem(selected = isSelected, onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label, style = MaterialTheme.typography.labelSmall)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenAccent,
                    selectedTextColor = GreenAccent,
                    unselectedIconColor = GraySubtitle,
                    unselectedTextColor = GraySubtitle,
                    indicatorColor = FieldBg
                )
            )
        }
    }
}