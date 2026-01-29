package com.d101.farmily.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d101.farmily.data.model.AchievInfo

@Composable
fun AchievBox(
    context : Context,
    item: AchievInfo,
    modifier: Modifier = Modifier
) {

    val resourceId = remember(item.description) {
        context.resources.getIdentifier(
            item.description,
            "drawable",
            context.packageName
        )
    }


    Column(modifier = modifier
        .padding(vertical = 16.dp)
        .padding(horizontal = 8.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(60.dp)
            ,
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = resourceId),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
            )
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(item.name, style = MaterialTheme.typography.titleSmall)
        }
    }
}