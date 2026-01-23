package com.d101.farmily

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.d101.farmily.base.ApplicationClass
import com.d101.farmily.ui.login.JoinScreen
import com.d101.farmily.ui.login.LoginScreen
import com.d101.farmily.ui.plantInfo.PlantInfoScreen
import com.d101.farmily.ui.theme.FarmilyTheme

private const val TAG = "Farm"
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(ApplicationClass.sharedPreferencesUtil.userExist()) {

            openMainActivity()
        }

        setContent {
            FarmilyTheme {
                val navController = rememberNavController()

                LoginNavHost(navController, ::openMainActivity)
            }
        }
    }

    fun openMainActivity() {
        Intent(this, MainActivity::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
    }
}

@Composable
fun LoginNavHost(navController: NavHostController, openMain : () -> Unit) {

    NavHost(navController = navController, startDestination = LoginNavScreen.Login.route) {

        composable(LoginNavScreen.Login.route) {
            LoginScreen (
                navToJoinScreen = {navController.navigate(LoginNavScreen.Join.route)},
                navToInfoScreen = {
                    navController.navigate(LoginNavScreen.Info.route) {
                        popUpTo(LoginNavScreen.Login.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )


        }

        composable(LoginNavScreen.Join.route) {
            JoinScreen()
        }

        composable(LoginNavScreen.Main.route){
            openMain()
        }

        composable(LoginNavScreen.Info.route){
            PlantInfoScreen{openMain()}

        }
    }
}

sealed class LoginNavScreen(val route: String) {
    data object Login : LoginNavScreen("login")
    data object Join : LoginNavScreen("join")
    data object Main : LoginNavScreen("Main")

    data object Info : LoginNavScreen("Info")
}
