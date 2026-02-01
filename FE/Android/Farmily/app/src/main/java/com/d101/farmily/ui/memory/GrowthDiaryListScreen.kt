package com.d101.farmily.ui.memory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GrowthDiaryListScreen (
    //diaryItems: List<DiaryData>,
    initialIndex: Int // 사용자가 리스트에서 클릭하고 들어온 인덱스
) {
    // 1. 리스트의 상태(스크롤 위치 등)를 관리하는 state 생성
    val listState = rememberLazyListState()

    // 2. 화면이 처음 등장할 때, 클릭한 인덱스로 즉시 이동
    LaunchedEffect(key1 = initialIndex) {
        listState.scrollToItem(initialIndex)
        // 애니메이션 효과를 원하면 animateScrollToItem(initialIndex) 사용
    }

    Scaffold { padding ->
        // 3. LazyColumn에 state를 연결
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp) // 인스타 피드 느낌의 여백
        ) {
//            items(diaryItems) { item ->
//                GrowthDiary(1) // 아까 만든 인스타 스타일 아이템
//            }
        }
    }
}