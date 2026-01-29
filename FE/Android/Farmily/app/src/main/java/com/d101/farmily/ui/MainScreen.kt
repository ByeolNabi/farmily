package com.d101.farmily.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d101.farmily.R
import com.d101.farmily.ui.home.EnvInfoScreen
import com.d101.farmily.ui.lidarMap.LidarMapScreen
import com.d101.farmily.ui.memory.MemoryScreen
import com.d101.farmily.ui.userInfo.UserInfoScreen

@Composable
fun MainScreen () {

    val context = LocalContext.current
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                EnvInfoScreen()
            }

            composable(BottomNavItem.Map.route) {
                LidarMapScreen()
            }

            composable(BottomNavItem.Memory.route) {
                MemoryScreen()
            }

            composable(BottomNavItem.UserInfo.route) {
                UserInfoScreen(
                    {}
                )
            }
        }

    }

}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem> = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Memory,
        BottomNavItem.UserInfo,
    )
) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar(
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(30.dp),
                        tint = if (selected)  MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondary
                ),
                
                label = {
                    Text(
                        item.label,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    )
                },
                alwaysShowLabel = true
            )
        }
    }

}

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {

    data object Home : BottomNavItem("home", R.drawable.home, "환경 정보")
    data object Map : BottomNavItem("map", R.drawable.map, "실내 지도") //home으로 되어있는 라우트명 싹 다 수정하기
    data object UserInfo : BottomNavItem("userInfo", R.drawable.user, "나의 정보")

    data object Memory : BottomNavItem("memory", R.drawable.water, "우리 추억")
}