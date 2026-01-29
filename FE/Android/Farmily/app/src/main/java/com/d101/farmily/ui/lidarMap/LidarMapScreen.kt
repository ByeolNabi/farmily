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
        ) {

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.White)
            ) {
                val cellSize = size.width / 100f

                for (r in 0 until 100) {
                    for (c in 0 until 100) {
                        if (myRoomData[r][c] == 1) {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(c * cellSize, r * cellSize),
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
                r == 0 || r == size - 1 || c == 0 || c == size - 1 -> map[r][c] = 1

                r == 40 && (c < 30 || c > 50) -> map[r][c] = 1

                c == 30 && r < 40 -> map[r][c] = 1

                c == 60 && r > 40 && r != 70 -> map[r][c] = 1

                (r == 20 && c == 15) || (r == 80 && c == 80) -> map[r][c] = 1
                (r in 19..21 && c in 14..16) -> map[r][c] = 1

                (r == 40 && c == 40) -> map[r][c] = 0
                (r == 20 && c == 30) -> map[r][c] = 0
            }
        }
    }
    return map
}
