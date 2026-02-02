package com.d101.farmily.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ChangePasswordDialog(
    currentPasswordInvalid : Boolean,
    onDismiss: () -> Unit,
    onConfirm: (currentPassword : String, newPassword : String) -> Unit // 새 비밀번호를 전달
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // 유효성 검사: 모든 칸이 채워져 있고, 새 비밀번호와 확인이 일치할 때만 버튼 활성화
    val isReady = currentPassword.isNotEmpty() &&
            newPassword.isNotEmpty() &&
            newPassword == confirmPassword

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(), // 내용에 맞게 높이 조절
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "비밀번호 변경",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                // 1. 현재 비밀번호
                PasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "현재 비밀번호 입력"
                )

                if (currentPasswordInvalid) {
                    Text(
                        "현재 비밀번호가 일치하지 않습니다.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                // 2. 새 비밀번호
                PasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "새 비밀번호 입력"
                )

                // 3. 새 비밀번호 확인
                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "새 비밀번호 확인",
                    isError = newPassword != confirmPassword && confirmPassword.isNotEmpty()
                )

                if (newPassword != confirmPassword && confirmPassword.isNotEmpty()) {
                    Text(
                        "새 비밀번호가 일치하지 않습니다.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("취소", color = Color.Gray)
                    }

                    Button(
                        onClick = { onConfirm(currentPassword, newPassword) },
                        enabled = isReady,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("변경")
                    }
                }
            }
        }
    }
}

// 비밀번호 입력용 공통 컴포저블 (코드 중복 방지)
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
        // 입력 내용을 가려주는 설정
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}