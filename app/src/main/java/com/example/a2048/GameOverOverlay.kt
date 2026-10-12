package com.example.a2048

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverOverlay(currentScore: Int, onRestart: () -> Unit, onOpenRecords: () -> Unit, onDismiss: () -> Unit) {
    val dismiss by rememberUpdatedState(onDismiss)
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.10f))
            .testTag(TestTags.GAME_OVER_BACKDROP)
            .pointerInput(Unit) { detectTapGestures(onTap = { dismiss() }) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).widthIn(max = 340.dp).fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color(0xB31C4A3B), Color(0xC214382F))),
                    RoundedCornerShape(28.dp)
                )
                .border(1.dp, Color(0x667FCB94), RoundedCornerShape(28.dp))
                .pointerInput(Unit) { detectTapGestures(onTap = {}) }
                .testTag(TestTags.GAME_OVER_SCREEN)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.game_finished), color = Color(0xFFF0F7D5),
                fontSize = 22.sp, fontWeight = FontWeight.SemiBold
            )
            Text(stringResource(R.string.no_moves_left), color = Color(0xFFCCDFD1), fontSize = 13.sp)
            Text(
                text = stringResource(R.string.points, currentScore),
                color = Color(0xFFFFD166),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag(TestTags.GAME_OVER_SCORE)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CompactActionButton(
                    text = stringResource(R.string.restart), onClick = onRestart,
                    modifier = Modifier.weight(1f).testTag(TestTags.GAME_OVER_RESTART), emphasized = true
                )
                CompactActionButton(
                    text = stringResource(R.string.record_text_button), onClick = onOpenRecords,
                    modifier = Modifier.weight(1f).testTag(TestTags.GAME_OVER_RECORDS)
                )
            }
        }
    }
}
