package com.d101.farmily.ui.lidarMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d101.farmily.ui.component.WideButton

@Composable
fun LidarMapScreen(

) {

    val myRoomData = createRoomMap()


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        it



        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxSize()
                //.background(Color.Gray)
        ) {

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.White) // 1. 바닥은 그냥 배경색으로 해결!
            ) {
                val cellSize = size.width / 100f

                for (r in 0 until 100) {
                    for (c in 0 until 100) {
                        // 2. 값이 1(벽)인 경우만 검은색으로 그리기
                        if (myRoomData[r][c] == 1) {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(c * cellSize, r * cellSize),
                                // 3. 아주 미세하게 겹치게 그려서 벽끼리의 연결을 매끄럽게 함
                                size = Size(cellSize + 0.5f, cellSize + 0.5f)
                            )
                        }
                    }
                }
            }

            WideButton(
                modifier = Modifier
                    .padding(top = 25.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                ,
                text = "릴리 블리 부르기",
            ) { }

            WideButton(
                modifier = Modifier
                    .padding(top = 25.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                ,
                text = "현관 문 지정하기",
            ) { }

            WideButton(
                modifier = Modifier
                    .padding(top = 25.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                ,
                text = "구역 설정하기",
            ) { }
        }
    }

}

fun createRoomMap(): Array<IntArray> {
    val size = 100
    val map = Array(size) { IntArray(size) { 0 } }

    for (r in 0 until size) {
        for (c in 0 until size) {
            when {
                // 1. 외곽 벽
                r == 0 || r == size - 1 || c == 0 || c == size - 1 -> map[r][c] = 1

                // 2. 메인 복도 가로 벽 (방을 위아래로 분리)
                r == 40 && (c < 30 || c > 50) -> map[r][c] = 1

                // 3. 왼쪽 방 (Room 1) 세로 분리벽
                c == 30 && r < 40 -> map[r][c] = 1

                // 4. 오른쪽 아래 방 (Room 2, 3) 분리벽 (지그재그 랜덤 느낌)
                c == 60 && r > 40 && r != 70 -> map[r][c] = 1 // r!=70은 문 위치

                // 5. 방 내부의 랜덤한 기둥이나 장애물 (조금 더 '랜덤'한 느낌 추가)
                (r == 20 && c == 15) || (r == 80 && c == 80) -> map[r][c] = 1
                (r in 19..21 && c in 14..16) -> map[r][c] = 1

                // 6. 각 방의 문 (0으로 비워두기)
                (r == 40 && c == 40) -> map[r][c] = 0 // 메인 문
                (r == 20 && c == 30) -> map[r][c] = 0 // 왼쪽 방 문
            }
        }
    }
    return map
}
