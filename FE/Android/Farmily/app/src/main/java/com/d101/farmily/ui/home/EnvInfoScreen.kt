package com.d101.farmily.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d101.farmily.data.model.EnvInfo
import com.d101.farmily.ui.component.InfoBox

@Composable
fun EnvInfoScreen(

) {

    val context = LocalContext.current

    var plantNickName : String = "릴리 블리  🌿"
    var plantType : String = "산세베리아"

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
                    .padding(top = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
            ) {
                InfoBox(
                        context,
                EnvInfo(
                    type = "temperature",
                    value = 26.5,
                    state = "적정"
                ),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                )

                InfoBox(
                    context,
                    EnvInfo(
                        type = "temperature",
                        value = 26.5,
                        state = "적정"
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                InfoBox(
                    context,
                    EnvInfo(
                        type = "temperature",
                        value = 26.5,
                        state = "적정"
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )

                InfoBox(
                    context,
                    EnvInfo(
                        type = "temperature",
                        value = 26.5,
                        state = "적정"
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }
    }

}