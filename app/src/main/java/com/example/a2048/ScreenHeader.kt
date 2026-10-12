package com.example.a2048

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenHeader(title: String, onBack: (() -> Unit)? = null, titleModifier: Modifier = Modifier) {
    Box(Modifier.fillMaxWidth().height(48.dp)) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart).testTag(TestTags.RECORD_BACK_BUTTON)
            ) {
                Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.records_back), tint = Color(0xFFF0F7D5))
            }
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = titleModifier.align(Alignment.Center)
        )
    }
}
