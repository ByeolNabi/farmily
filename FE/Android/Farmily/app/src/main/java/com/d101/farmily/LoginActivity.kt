package com.d101.farmily

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

        if(ApplicationClass.sharedPreferencesUtil.getAccessToken() != "" &&
            ApplicationClass.sharedPreferencesUtil.plantExist()
        ) {

            openMainActivity()
            finish()
            return
        }
        else if(ApplicationClass.sharedPreferencesUtil.getAccessToken() != "") {
            setContent {
                FarmilyTheme {
                    PlantInfoScreen {

                        openMainActivity()
                    }
                }
            }

        } else {
            setContent {
                FarmilyTheme {
                    val navController = rememberNavController()

                    LoginNavHost(navController, ::openMainActivity)
                }
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
                onLoginSuccess = {
                    if(ApplicationClass.sharedPreferencesUtil.plantExist()) {
                        openMain()
                    } else {
                        navController.navigate(LoginNavScreen.Info.route) {
                            popUpTo(LoginNavScreen.Login.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }

                }
            )


        }

        composable(LoginNavScreen.Join.route) {
            JoinScreen(
                navToLoginScreen = {
                    navController.navigate(LoginNavScreen.Login.route) {
                        popUpTo(LoginNavScreen.Join.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(LoginNavScreen.Main.route){
            openMain()
        }

        composable(LoginNavScreen.Info.route){
            PlantInfoScreen{

                openMain()
            }

        }
    }
}

sealed class LoginNavScreen(val route: String) {
    data object Login : LoginNavScreen("login")
    data object Join : LoginNavScreen("join")
    data object Main : LoginNavScreen("Main")

    data object Info : LoginNavScreen("Info")
}
