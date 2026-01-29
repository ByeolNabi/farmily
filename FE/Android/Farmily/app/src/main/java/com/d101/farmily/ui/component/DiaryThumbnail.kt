package com.d101.farmily.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d101.farmily.R
import com.d101.farmily.ui.theme.borderGreen

@Composable
fun DiaryThumbnail(

) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .clickable(onClick = {  })
            .border(0.25.dp, borderGreen, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(14.dp)

    ){
        Box(modifier = Modifier
            .height(120.dp)
        ){
            Image(
                painter = painterResource(R.drawable.templogo),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        Text(
            text = "제목입니다.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp, 6.dp)
                .fillMaxWidth()
        )
    }
}