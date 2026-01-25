package com.d101.farmily.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d101.farmily.ui.component.InfoBox

@Composable
fun EnvInfoScreen(

) {

    val context = LocalContext.current

    val envInfoViewModel: EnvInfoViewModel = viewModel()

    val envInfoList by envInfoViewModel.envInfoList.collectAsState()

    var plantNickName : String = "릴리 블리  🌿"
    var plantType : String = "산세베리아"

    LaunchedEffect (Unit){
        envInfoViewModel.startMqtt()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        )  {

            Text(
                text = plantNickName,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(start = 24.dp)
            )

            Text(
                text = plantType,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .padding(vertical = 12.dp)
            )

            if(envInfoList.isEmpty()) {
                Box(

                ) {
                    Text(
                        text = "... 환경 정보를 불러오는 중 입니다."
                    )
                }
            } else {
                envInfoList.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        rowItems.forEach { item ->
                            InfoBox(

                                item,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }


//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .padding(top = 32.dp)
//            ) {
//
//                InfoBox(
//
//                envInfoList[0],
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f)
//                )
//
//                InfoBox(
//
//                    envInfoList[1],
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f)
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//            ) {
//                InfoBox(
//
//                    envInfoList[2],
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f)
//                )
//
//                InfoBox(
//
//                    envInfoList[3],
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f)
//                )
//            }
        }
    }

}