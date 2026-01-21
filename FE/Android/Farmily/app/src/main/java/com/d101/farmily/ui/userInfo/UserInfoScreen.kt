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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d101.farmily.R
import com.d101.farmily.data.model.StatInfo
import com.d101.farmily.ui.component.StatBox
import com.d101.farmily.ui.theme.middleGreen

@Composable
fun UserInfoScreen(
    onLogout: () -> Unit
) {

    var plantType by remember { mutableStateOf("산세베리아") }
    var plantName by remember { mutableStateOf("릴리 블리") }

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
        StatInfo("stroking", "${interactions.petCount}"),
        StatInfo("photo", "${interactions.photoCount}"),
        StatInfo("watering", "${interactions.waterCount}"),
        StatInfo("chatting", "${interactions.chatDays}"),
        StatInfo("만난 지", "${interactions.daysTogether}")
    )

    Scaffold(
        bottomBar = { /* 바텀 네비게이션이 있다면 여기에 추가 */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                //horizontalArrangement = Arrangement.spacedBy(12.dp),
                //contentPadding = PaddingValues(horizontal = 4.dp) // 양 끝에 약간의 여백
            ) {
                items(stats) { item ->
                    StatBox(
                        item = item,
                        modifier = Modifier
                            .fillParentMaxWidth(0.2f)// 적당한 가로 크기 지정
                    )
                }
            }

            Text(text = "일반", style = MaterialTheme.typography.labelLarge, color = Color.Gray)

            // 식물 변경 클릭 가능한 카드
            SettingItemCard(
                icon = Icons.Default.Edit,
                title = "식물 변경",
                subtitle = "식물 종류 및 이름 수정",
                onClick = {  }
            )

            // 계정 섹션 및 로그아웃
            Text(text = "계정", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            LogoutButton(onClick = onLogout)

            // 앱 정보
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("새싹 식물 관리 v1.0.0", style = MaterialTheme.typography.bodySmall)
                    Text("© 2026 Plant Care App", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }

}

// 공통 UI 컴포넌트들
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