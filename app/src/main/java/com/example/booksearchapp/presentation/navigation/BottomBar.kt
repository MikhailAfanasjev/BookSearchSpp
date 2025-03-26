package com.example.booksearchapp.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booksearchapp.presentation.ui.theme.Blue
import com.example.booksearchapp.presentation.ui.theme.LightGray
import com.example.booksearchapp.presentation.ui.theme.Regular_14
import com.example.booksearchapp.presentation.ui.theme.White
import com.example.linguareader.R

object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha() = RippleAlpha(0f, 0f, 0f, 0f)
}

@Composable
fun BottomBar(navController: NavController) {
    val iconSize = 24.dp

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        NavigationBar(
            containerColor = White,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        ) {
            val selectedRoute = navController.currentDestination?.route
            val interactionSource = remember { MutableInteractionSource() }

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
                interactionSource = interactionSource,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
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
                interactionSource = interactionSource,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}