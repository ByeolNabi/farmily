package com.d101.farmily.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.d101.farmily.R
import com.d101.farmily.data.remote.model.Achievement
import com.d101.farmily.ui.dialog.AchievementDetailDialog

@Composable
fun AchievBox(
    context : Context,
    item: Achievement?,
    modifier: Modifier = Modifier
) {

    var showAchievementDetailDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .padding(vertical = 16.dp)
        .padding(horizontal = 8.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.
            size(60.dp)
                .clickable(
                    //enabled = item != null,
                    onClick = {
                        showAchievementDetailDialog = true
                    }
                )
            ,
            contentAlignment = Alignment.Center,

        ) {

            Image(
                painter =
                    if(item == null) painterResource(id = R.drawable.yet)
                    else rememberAsyncImagePainter(item.iconUrl?: "https://cdn-icons-png.flaticon.com/512/4516/4516955.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
            )
        }

    }

    if(showAchievementDetailDialog) {
        AchievementDetailDialog(
            item = item,
            onDismiss = {
                showAchievementDetailDialog = false
            }
        )
    }
}