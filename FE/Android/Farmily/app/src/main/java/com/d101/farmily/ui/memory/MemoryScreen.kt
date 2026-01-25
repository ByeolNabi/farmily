package com.d101.farmily.ui.memory


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d101.farmily.R
import com.d101.farmily.ui.component.DiaryThumbnail
import com.d101.farmily.ui.component.WideButton
import com.d101.farmily.ui.theme.middleGreen
import com.d101.farmily.ui.userInfo.InfoCard

@Composable
fun MemoryScreen(

) {

    val memories = listOf(
        1,1,1,1,1,1,1,1,1,1,1
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        it

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

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
                            text = "릴리 블리",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Text(
                            text = "산세베리아",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )

                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                    )

                    WideButton(
                        modifier = Modifier
                            .height(50.dp)
                            .weight(2.2f)
                        ,
                        text = "타입 랩스"
                    ) {

                    }

                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                    )

                    WideButton(
                        modifier = Modifier
                            .height(50.dp)
                            .weight(2.2f)

                        ,
                        text = "성장 일기"
                        // 눌러서 가면 하나씩 보여주지 말고 인스타처럼
                        // 해단 일기부터 위아래 스크롤 가능?
                    ) {

                    }
                }
            }

            Text(text = "릴리 블리와 나의 추억🌱🪴💚",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp),
                color = middleGreen,
                modifier = Modifier
                    .padding(top = 42.dp)
                    .padding(bottom = 8.dp)
                    .padding(start = 12.dp)
            )



            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                ,
            ){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)


                ) {
                    items(memories) {
                        DiaryThumbnail()
                    }
                }

            }

        }
    }

}