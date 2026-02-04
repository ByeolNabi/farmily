package com.d101.farmily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ResetPasswordDialog(
    onDismiss: () -> Unit,
    onGetVerify: (email : String) -> Unit,
    onConfirm: (email : String, code : String, new : String) -> Unit // 이메일과 새 비밀번호 전달
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val verifyCode = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // 모든 조건이 충족되어야 '재설정' 버튼 활성화
    val isReady = email.isNotEmpty() &&
            newPassword.isNotEmpty() &&
            newPassword == confirmPassword

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "비밀번호 재설정",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                // 1. 이메일 입력
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

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

                // 3. 새 비밀번호 입력
                PasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "새 비밀번호"
                )

                // 4. 새 비밀번호 확인
                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "새 비밀번호 확인",
                    isError = newPassword != confirmPassword && confirmPassword.isNotEmpty()
                )

                // 버튼 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("취소", color = Color.Gray)
                    }

                    Button(
                        onClick = { onGetVerify(email) },
                        enabled = email.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("인증번호 받기")
                    }

                    Button(
                        onClick = { onConfirm(email, verifyCode.joinToString(""), newPassword) },
                        enabled = isReady,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("재설정")
                    }
                }
            }
        }
    }
}