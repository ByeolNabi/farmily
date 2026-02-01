package com.d101.farmily.ui.userInfo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d101.farmily.R
import com.d101.farmily.base.ApplicationClass
import com.d101.farmily.data.remote.model.AchievInfo
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.StatInfo
import com.d101.farmily.ui.component.AchievBox
import com.d101.farmily.ui.component.StatBox
import com.d101.farmily.ui.theme.middleGreen

@Composable
fun UserInfoScreen(
    onLogout: () -> Unit,
    onWithdraw: () -> Unit,
    navToPlantInfo : () -> Unit
) {
    val context = LocalContext.current

    val userInfoViewModel : UserInfoViewModel = viewModel()

    val withdrawal by userInfoViewModel.withdrawal.collectAsState()

    var showWithdrawConfirmDialog by remember { mutableStateOf(false) }

    var plantName : String = ApplicationClass.sharedPreferencesUtil.getPlantName()!!
    var plantType : String = ApplicationClass.sharedPreferencesUtil.getPlantType()!!


    // Mock data
    val interactions = remember {
        object {
            val petCount = 47
            val photoCount = 12
            val waterCount = 23
            val chatDays = 15
            val daysTogether = 28
        }
    }

    val stats = listOf(
        StatInfo("쓰다듬기", "${interactions.petCount}"),
        StatInfo("사진찍기", "${interactions.photoCount}"),
        StatInfo("물 주기", "${interactions.waterCount}"),
        StatInfo("대화하기", "${interactions.chatDays}"),
        StatInfo("만난 지", "${interactions.daysTogether}")
    )

    val achievements = listOf(
        AchievInfo("첫 접촉", "fst"),
        AchievInfo("첫 추억", "fca"),
        AchievInfo("첫 식사", "fwa"),
        AchievInfo("첫 대화", "fch"),
        AchievInfo("첫 만남", "fme")
    )

    LaunchedEffect(withdrawal) {

        if(withdrawal) onWithdraw()
    }

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState()) // overflow-y-auto 대응
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 헤더 영역
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "나와 식물의 성장 기록",
                    style = MaterialTheme.typography.bodySmall,
                    color = middleGreen
                )
            }


            InfoCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.templogo),
                            contentDescription = null,
                            modifier = Modifier
                                .width(180.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                            ,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = plantName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Text(
                            text = plantType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Row(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .height(12.dp)

                            ,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Magenta,
                                modifier = Modifier.size(20.dp))

                            LinearProgressIndicator(
                                progress = {0.77f},
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)

                            )

                            Text(
                                text = "77",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }

                    }
                }
            }


            Text(text = "함께한 추억",
                style = MaterialTheme.typography.bodySmall,
                color = middleGreen)

            LazyRow(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp)
                ,
                ) {
                items(stats) { item ->
                    StatBox(
                        item = item,
                        modifier = Modifier
                            .fillParentMaxWidth(0.2f)
                    )
                }
            }

            Text(text = "달성한 업적",
                style = MaterialTheme.typography.bodySmall,
                color = middleGreen)



            LazyRow(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp)
                ,
               ) {
                items(achievements) { item ->
                    AchievBox(
                        context,
                        item = item,
                        modifier = Modifier
                            .fillParentMaxWidth(0.2f)
                    )
                }
            }

            Text(text = "일반", style = MaterialTheme.typography.labelLarge, color = Color.Gray)


            SettingItemCard(
                icon = Icons.Default.Edit,
                title = "식물 변경",
                subtitle = "식물 종류 및 이름 수정",
                onClick = { navToPlantInfo()  }
            )


            Text(text = "계정", style = MaterialTheme.typography.labelLarge, color = Color.Gray)

            ChangePasswordButton({})

            LogoutButton(onClick = onLogout)

            WithdrawButton(onClick = {showWithdrawConfirmDialog = true})


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("© 2026 Fram-ily App", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if(showWithdrawConfirmDialog) {
            WithdrawalDialog(
                {showWithdrawConfirmDialog = false},
                {
                    userInfoViewModel.withdraw(Auth(ApplicationClass.sharedPreferencesUtil.getUserEmail()!!))
                }
            )
        }
    }

}

@Composable
fun InfoCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = { content() }
    )
}

data class StatItem(val icon: ImageVector, val label: String, val value: String, val color: Color, val bgColor: Color)

@Composable
fun StatCard(item: StatItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(item.bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(item.label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(item.value, style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
fun SettingItemCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFF0FDF4), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF22C55E))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ChangePasswordButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFEBFE)),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFEBFC), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("비밀번호 변경", color = Color.Black, style = MaterialTheme.typography.titleSmall)
                Text("비밀번호를 변경합니다", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
@Composable
fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFEBEE), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("로그아웃", color = Color.Black, style = MaterialTheme.typography.titleSmall)
                Text("계정에서 로그아웃합니다", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun WithdrawButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFEBEE), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("회원 탈퇴", color = Color.Black, style = MaterialTheme.typography.titleSmall)
                Text("회원 정보를 삭제합니다", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun WithdrawalDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var inputEmail by remember { mutableStateOf("") }
    // 이메일이 정확히 일치해야 버튼 활성화
    val isReadyToWithdraw = inputEmail == ApplicationClass.sharedPreferencesUtil.getUserEmail()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f), // 화면의 50% 차지
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 헤더 영역
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "회원 탈퇴",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "탈퇴 시 모든 데이터가 삭제됩니다.\n확인을 위해 이메일을 입력해 주세요.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // 입력 영역 (OutlinedTextField 사용)
                OutlinedTextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("이메일 입력") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD32F2F),
                        focusedLabelColor = Color(0xFFD32F2F),
                        cursorColor = Color(0xFFD32F2F)
                    )
                )

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("취소", color = Color.Gray)
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = isReadyToWithdraw,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            disabledContainerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Text(
                            "탈퇴하기",
                            color = if (isReadyToWithdraw) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    }
}