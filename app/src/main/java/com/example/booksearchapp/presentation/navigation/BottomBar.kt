package com.example.booksearchapp.presentation.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booksearchapp.presentation.ui.theme.Blue
import com.example.booksearchapp.presentation.ui.theme.Regular_14
import com.example.booksearchapp.presentation.ui.theme.White
import com.example.linguareader.R

@Composable
fun BottomBar(navController: NavController) {
    val iconSize = 24.dp

    NavigationBar(
        containerColor = White
    ) {
        val selectedRoute = navController.currentDestination?.route

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = if (selectedRoute == "search")
                        colorResource(R.color.blue)
                    else
                        colorResource(R.color.light_gray),
                    modifier = Modifier.size(iconSize)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.search),
                    style = Regular_14.copy(color = Blue)
                )
            },
            selected = selectedRoute == "search",
            onClick = { navController.navigate("search") },
            interactionSource = remember { MutableInteractionSource() },
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = if (selectedRoute == "favorites")
                        colorResource(R.color.blue)
                    else
                        colorResource(R.color.light_gray),
                    modifier = Modifier.size(iconSize)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.favorites),
                    style = Regular_14.copy(color = Blue)
                )
            },
            selected = selectedRoute == "favorites",
            onClick = { navController.navigate("favorites") },
            interactionSource = remember { MutableInteractionSource() },
        )
    }
}