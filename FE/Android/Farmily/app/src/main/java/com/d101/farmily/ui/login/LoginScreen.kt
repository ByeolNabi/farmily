package com.d101.farmily.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d101.farmily.R
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.User
import com.d101.farmily.ui.component.WideButton
import com.d101.farmily.ui.dialog.ResetPasswordDialog
import com.d101.farmily.ui.theme.borderGreen
import com.d101.farmily.ui.theme.deepGreen
import com.d101.farmily.ui.theme.middleGreen


@Composable
fun LoginScreen(
    navToJoinScreen : () -> Unit,
    onLoginSuccess : () -> Unit
) {

    val context = LocalContext.current

    var loginViewModel : LoginViewModel = viewModel()

    var id by  remember { mutableStateOf("")  }
    var pw by remember { mutableStateOf("") }

    var attemptCount by remember { mutableIntStateOf(0) }

    val loginSuccess by loginViewModel.loginSuccess.collectAsState()
    val showResetPasswordDialog by loginViewModel.showResetPasswordDialog.collectAsState()

    LaunchedEffect(loginSuccess) {

        if(loginSuccess) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(Unit) {
        // ViewModel에서 emit(문자열)이 올 때마다 호출됨
        loginViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            // 여기서 변수를 다시 false로 바꿀 필요가 없음!
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        innerPadding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center

        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .wrapContentHeight()
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .border(0.25.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)) // 보더 추가
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.templogo),
                        contentDescription = "로고",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(180.dp)
                    )

                    Text(
                        text = "FARM-ILY",
                        modifier = Modifier
                            .padding(top = 10.dp),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp),
                        color = deepGreen
                    )

                    Text(
                        text = "식물을 가족처럼 키워보세요",
                        color = borderGreen
                    )

                    Text(
                        text = "이메일",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 12.dp)
                            .align(Alignment.Start),
                        color = middleGreen
                    )

                    OutlinedTextField(
                        value = id,
                        onValueChange = {
                            id = it
                        },
                        label = {
                            Text(
                                text = "이메일을 입력하세요"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp,),
                        shape = RoundedCornerShape(21.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = borderGreen.copy(alpha = 0.33f),
                            unfocusedLabelColor = borderGreen.copy(alpha = 0.66f)
                        )
                    )

                    Text(
                        text = "비밀번호",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 12.dp)
                            .align(Alignment.Start),
                        color = middleGreen
                    )

                    OutlinedTextField(
                        value = pw,
                        onValueChange = {
                            pw = it
                        },
                        label = {
                            Text(
                                text = "비밀번호를 입력하세요"
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = borderGreen.copy(alpha = 0.33f),
                            unfocusedLabelColor = borderGreen.copy(alpha = 0.66f)
                        )

                    )

                    ButtonWide()
                    WideButton(
                        "로그인",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                        ,
                        backgroundColor = if(id == "" || pw == "") MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.primary

                    ) {
                        //onLoginSuccess()
                        if(id == "" || pw == "") {

                        } else {
                            attemptCount++
                            loginViewModel.login(User(id, name = "", pw))
                        }

                    }


                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = "아직 계정이 없으신가요?",
                            color = middleGreen
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    navToJoinScreen()
                                },
                            text = "회원가입",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            color = deepGreen
                        )
                    }

                    if(attemptCount >= 2) {
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = "비밀번호를 잊으셨나요?",
                                color = middleGreen
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable {
                                        loginViewModel.openResetPasswordDialog()
                                    },
                                text = "비밀번호 찾기",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                                color = deepGreen
                            )
                        }
                    }

                }
            }

        }

        if(showResetPasswordDialog) {
            ResetPasswordDialog(
                onDismiss = {
                    loginViewModel.closeResetPasswordDialog()
                },
                onGetVerify = { email ->
                    loginViewModel.getEmailVer(Auth(email = email))
                },
                onConfirm = { email, code, new ->
                    loginViewModel.resetPassword(Auth(email,code,new))
                }
            )
        }

    }
}


@Composable
fun ButtonWide() {

}


