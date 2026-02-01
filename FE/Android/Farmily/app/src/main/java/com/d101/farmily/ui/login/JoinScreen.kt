package com.d101.farmily.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d101.farmily.R
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.User
import com.d101.farmily.ui.component.WideButton
import com.d101.farmily.ui.theme.borderGreen
import com.d101.farmily.ui.theme.deepGreen
import com.d101.farmily.ui.theme.middleGreen

@Composable
fun JoinScreen(
    navToLoginScreen : () -> Unit,
) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    val loginViewModel : LoginViewModel = viewModel()

    val showVerifyDialog by loginViewModel.verifyDialog.collectAsState()

    var refresh by remember { mutableStateOf(false) }

    val canJoin by loginViewModel.canJoin.collectAsState()

    LaunchedEffect(canJoin) {

        if(canJoin == 1) {
            Toast.makeText(context, "회원가입이 완료됐습니다.", Toast.LENGTH_SHORT).show()
            navToLoginScreen()
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
                    .border(0.25.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
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
                        text = "회원 가입",
                        modifier = Modifier
                            .padding(top = 10.dp),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp),
                        color = deepGreen
                    )

                    Text(
                        text = "",
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
                        value = email,
                        onValueChange = {
                            email = it
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
                        text = "이름",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 12.dp)
                            .align(Alignment.Start),
                        color = middleGreen
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = {
                            Text(
                                text = "이름을 입력하세요"
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
                        "회원가입",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        backgroundColor = if(email.isEmpty() || name.isEmpty() || pw.isEmpty()) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.primary

                        ) {

                        if(email.isEmpty() || name.isEmpty() || pw.isEmpty()) {
                            Toast.makeText(context, "모든 칸을 채워주세요", Toast.LENGTH_SHORT).show()
                        } else {
                            loginViewModel.getEmailVer(Auth(email))
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
                            text = "환영합니다",
                            color = middleGreen
                        )
                    }

                }
            }


        }

        if(showVerifyDialog) {
            VerificationDialog(  {
                loginViewModel.closeDialog()
            },{code->
                Log.d("Join", "JoinScreen: $code")
                loginViewModel.sendEmailVer(Auth(email, code), User(email,"name", pw))
            })
        }

    }

}

@Composable
fun VerificationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    //var codeList by remember { mutableStateOf(mutableListOf("")) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    val verifyCode = remember { mutableStateListOf("", "", "", "", "", "") }


   // var refresh by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f) // 50%는 6칸에 너무 좁아서 85% 권장해요!
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("인증번호 입력", fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Spacer(modifier = Modifier.height(20.dp))

                // 6개의 입력 칸 Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    verifyCode.forEachIndexed { index, string ->
                        BasicTextField(
                            value = verifyCode[index],
                            onValueChange = { input ->
                                if (input.length <= 1 && input.all { it.isDigit() }) {
                                    verifyCode[index] = input
                                    if (input.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 0.dp)
                                .weight(1f)
                                .focusRequester(focusRequesters[index]),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (string.isNotEmpty()) Color(0xFF4CAF50) else Color.Black
                            ),
                            decorationBox = { innerTextField ->
                                // Column을 사용하여 텍스트 아래에 밑줄 배치
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 텍스트가 찍히는 부분
                                    innerTextField()

                                    Spacer(modifier = Modifier.height(4.dp)) // 글자와 밑줄 사이 간격

                                    // 여기가 핵심: 하단에만 선 긋기
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f) // 밑줄 길이를 칸 너비의 80%로 조절
                                            .height(if (verifyCode[index] != "") 2.5.dp else 1.dp) // 포커스 시 더 두껍게
                                            .background(
                                                color = when {
                                                    verifyCode[index] != "" -> Color(0xFF4CAF50) // 글자 있으면 초록색
                                                    else -> Color.LightGray // 평상시 회색
                                                }
                                            )
                                    )
                                }
                            }

                        )
                    }

                }


                Spacer(modifier = Modifier.height(30.dp))

                // 버튼 영역
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("취소", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(verifyCode.joinToString("")) },
                        enabled = verifyCode.all { it.isNotEmpty() },
                        shape = RoundedCornerShape(13.dp)
                    ) {
                        Text("인증하기")
                    }
                }
            }
        }
    }
}