package com.d101.farmily.ui.plantInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d101.farmily.R
import com.d101.farmily.base.ApplicationClass
import com.d101.farmily.ui.component.WideButton
import com.d101.farmily.ui.login.ButtonWide
import com.d101.farmily.ui.theme.borderGreen
import com.d101.farmily.ui.theme.deepGreen
import com.d101.farmily.ui.theme.middleGreen

@Composable
fun PlantInfoScreen(
    navToMain : () -> Unit
) {

    val context = LocalContext.current

    var plantType by remember { mutableStateOf("") }
    var plantNickName by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    val OPTIONS = listOf("몬스테라", "장미", "다육이", "스투키", "상추")

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        innerPadding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center

        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .wrapContentHeight()
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .border(0.25.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.templogo),
                        contentDescription = "로고",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(180.dp)
                    )

                    Text(
                        text = "Farm-liy",
                        modifier = Modifier
                            .padding(top = 10.dp),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp),
                        color = deepGreen
                    )


                    Text(
                        text = "반려 식물을 등록해주세요",
                        color = borderGreen
                    )

                    Text(
                        text = "식물 종류",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 24.dp)
                            .align(Alignment.Start),
                        color = middleGreen
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp,)
                            .height(IntrinsicSize.Min)
                    ) {

                        OutlinedTextField(
                            value = plantType,
                            onValueChange = {
                                plantType = it
                            },
                            label = {
                                Text(
                                    text = "예: 몬스테라, 스투키"
                                )
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 16.dp,),

                            shape = RoundedCornerShape(21.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = borderGreen.copy(alpha = 0.33f),
                                unfocusedLabelColor = borderGreen.copy(alpha = 0.66f)
                            )
                        )
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .border(1.dp, borderGreen,  RoundedCornerShape(21.dp))
                                .fillMaxHeight()
                                .align(Alignment.Bottom)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "열기",
                                tint = borderGreen
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                                //.fillMaxWidth()
                                .heightIn(max = 220.dp),
                            //offset = DpOffset(x = 0.dp, y = 10.dp)// 배경색은 보통 흰색 권장
                        ) {
                            OPTIONS.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        plantType = option // 선택한 값을 텍스트 필드에 넣기
                                        expanded = false
                                        }
                                )
                            }
                        }

                    }

                    Text(
                        text = "식물 애칭",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 12.dp)
                            .align(Alignment.Start),
                        color = middleGreen
                    )

                    OutlinedTextField(
                        value = plantNickName,
                        onValueChange = {
                            plantNickName = it
                        },
                        label = {
                            Text(
                                text = "반려 식물의 애칭을 지어주세요"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = borderGreen.copy(alpha = 0.33f),

                            unfocusedLabelColor = borderGreen.copy(alpha = 0.66f)

                        )

                    )

                    ButtonWide()
                    WideButton(
                        "시작하기",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        backgroundColor = if(plantType.isEmpty() || plantNickName.isEmpty()) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.primary

                        ) {
                        if(plantType.isEmpty() || plantNickName.isEmpty()) {

                        } else {
                            ApplicationClass.sharedPreferencesUtil.addPlantName(plantNickName)
                            ApplicationClass.sharedPreferencesUtil.addPlantType(plantType)

                            navToMain()
                        }


                    }

                }
            }

        }

    }

}