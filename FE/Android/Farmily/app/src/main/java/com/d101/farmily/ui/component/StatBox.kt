package com.d101.farmily.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d101.farmily.data.model.StatInfo

@Composable
fun StatBox(
    item: StatInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .padding(vertical = 16.dp)
        .padding(horizontal = 8.dp)
        //.background(Color.Magenta)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(item.backColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(id = item.iconId),
                contentDescription = null,
                tint = item.color,
                modifier = Modifier
                    .size(20.dp)
            )
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(item.type, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(item.value + item.unit, style = MaterialTheme.typography.titleSmall)
        }
    }
}