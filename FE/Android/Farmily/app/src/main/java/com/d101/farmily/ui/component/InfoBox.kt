package com.d101.farmily.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d101.farmily.data.model.EnvInfo

@Composable
fun InfoBox(
    context : Context,
    envInfo: EnvInfo,
    modifier: Modifier
) {

    val resourceId = remember(envInfo.type) {
        context.resources.getIdentifier(
            envInfo.type,      // 파일 이름 (확장자 제외)
            "drawable",    // 리소스 폴더명
            context.packageName
        )
    }

    Box(
        modifier = modifier
            .padding(all = 8.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(0.25.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)) // 보더 추가
            .background(Color.White)
    ) {

        Row(

        ) {

            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .weight(1f)
                    //.background(MaterialTheme.colorScheme.surface),
                    .fillMaxHeight(),

                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        //.background(MaterialTheme.colorScheme.primary)

                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFFFFF7ED),
                                RoundedCornerShape(35.dp))
                        ,contentAlignment = (Alignment.Center)
                    ) {
                        Icon(
                            painter = painterResource(id = resourceId),
                            contentDescription = envInfo.type,
                            tint = Color(0xFFed8936),
                            modifier = Modifier
                                .size(42.dp)
                            //.background(Color(0xFFFFF7ED))
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxWidth(0.7f)
                        .background(MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = envInfo.state,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }



            }

            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .weight(1f)
                    .fillMaxHeight()
                    //.background(MaterialTheme.colorScheme.surface)
                    ,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Text(
                    text = envInfo.type,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = envInfo.value.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }

    }

}