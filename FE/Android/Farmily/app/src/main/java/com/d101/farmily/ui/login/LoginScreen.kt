package com.d101.farmily.ui.login

import android.util.Log
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d101.farmily.R
import com.d101.farmily.ui.component.WideButton
import com.d101.farmily.ui.theme.borderGreen
import com.d101.farmily.ui.theme.deepGreen
import com.d101.farmily.ui.theme.middleGreen


@Composable
fun LoginScreen(
    navToJoinScreen : () -> Unit,
    navToInfoScreen : () -> Unit
) {

    val context = LocalContext.current

    var id by  remember { mutableStateOf("")  }
    var pw by remember { mutableStateOf("") }

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
                        contentScale = ContentScale.Crop,//이미지 비율 유지한채로 부모 영역을 꽉 채움
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
                        text = "아이디",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 24.dp)
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
                                text = "아이디를 입력하세요"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp,),
                        shape = RoundedCornerShape(21.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = borderGreen.copy(alpha = 0.33f),
                            //focusedLabelColor = Color(0xFF4CAF50),
                            unfocusedLabelColor = borderGreen.copy(alpha = 0.66f)
                            //cursorColor = Color(0xFF4CAF50)
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
                            //focusedLabelColor = Color(0xFF4CAF50),
                            unfocusedLabelColor = borderGreen.copy(alpha = 0.66f)
                            //cursorColor = Color(0xFF4CAF50)
                        )

                    )

                    ButtonWide()
                    WideButton(
                        "로그인",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp),

                    ) {
                        Log.d("reart", "LoginScreen: login 처리")
                        navToInfoScreen()
                    }

                    //라인하나
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), // 위아래 간격
                        thickness = 0.5.dp, // 선 두께
                        color = MaterialTheme.colorScheme.outlineVariant // 선 색상
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
                                    //Log.d("reart", "LoginScreen: go to join")
                                    navToJoinScreen()
                                },
                            text = "회원가입",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            color = deepGreen
                        )
                    }
                }
            }

        }

    }
}


@Composable
fun ButtonWide() {

}


