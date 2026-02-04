package com.d101.farmily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.d101.farmily.data.remote.model.Achievement

@Composable
fun AchievementDetailDialog(
    item: Achievement?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        // 배경 투명도를 위해 DialogProperties 설정
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        // 1. 배경을 투명하게 설정한 Surface (크기는 화면 너비의 80% 정도가 보기 좋습니다)
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f) // 50%는 너무 작을 수 있어 80%로 예시를 드립니다. 원하시면 0.5f로 변경 가능!
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent // 내용물이 담길 박스 색상
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 상단 이미지
                AsyncImage(
                    model = item?.iconUrl ?: "https://cdn-icons-png.flaticon.com/512/4516/4516955.png",
                    contentDescription = null,
                    modifier = Modifier.size(250.dp)
                )

                // 하단 텍스트 3개
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(24.dp))
                        .border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                        .width(250.dp)
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        text = item?.name ?: "업적명",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item?.description ?: "업적 상세 설명입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if(item != null) {
                        Text(
                            text = "획득일: ${item.createdAt.take(10)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(top = 8.dp)
                        )
                    }

                }

                // 닫기 버튼
                TextButton(onClick = onDismiss) {
                    Text("X")
                }
            }
        }
    }
}