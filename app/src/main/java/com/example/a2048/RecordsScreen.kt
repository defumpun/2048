package com.example.a2048

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a2048.domain.model.GameRecord
import com.example.a2048.presentation.records.RecordsViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun RecordsRoute(onBack: () -> Unit, viewModel: RecordsViewModel = hiltViewModel()) {
    val records by viewModel.records.collectAsStateWithLifecycle()
    val clearing by viewModel.clearing.collectAsStateWithLifecycle()
    RecordsScreen(onBack, records, viewModel::onClearRecords, clearing)
}

@Composable
fun RecordsScreen(
    onBack: () -> Unit,
    records: List<GameRecord> = emptyList(),
    onClearRecords: () -> Unit = {},
    clearing: Boolean = false,
) {
    var selectedSize by rememberSaveable { mutableStateOf(4) }
    var showClearConfirmation by rememberSaveable { mutableStateOf(false) }
    val visibleRecords = records.filter { it.size == selectedSize }
    Box(Modifier.fillMaxSize().testTag(TestTags.RECORD_SCREEN)) {
        Image(painterResource(R.drawable.bg2), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        BoxWithConstraints(
            Modifier.fillMaxSize().background(Color(0x99102E27))
                .systemBarsPadding().padding(16.dp)
        ) {
            val isWide = maxWidth > maxHeight
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ScreenHeader(stringResource(R.string.record_text), onBack, Modifier.testTag(TestTags.RECORD_TITLE))
                if (isWide) {
                    Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                            RecordSizes(selectedSize) { selectedSize = it }
                            Spacer(Modifier.weight(1f))
                            ClearRecordsButton(records.isNotEmpty() && !clearing) { showClearConfirmation = true }
                        }
                        RecordList(visibleRecords, Modifier.weight(1f).fillMaxHeight())
                    }
                } else {
                    RecordSizes(selectedSize) { selectedSize = it }
                    RecordList(visibleRecords, Modifier.fillMaxWidth().weight(1f))
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ClearRecordsButton(records.isNotEmpty() && !clearing) { showClearConfirmation = true }
                    }
                }
            }
        }
    }
    if (showClearConfirmation) {
        ConfirmationDialog(
            question = stringResource(R.string.confirm_clear_records),
            detail = stringResource(R.string.clear_records_detail),
            onConfirm = { showClearConfirmation = false; onClearRecords() },
            onDismiss = { showClearConfirmation = false }
        )
    }
}

@Composable
private fun RecordSizes(selectedSize: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (boardSize in 4..6) {
            CompactActionButton(stringResource(R.string.board_size_tab, boardSize), { onSelect(boardSize) },
                Modifier.weight(1f), emphasized = selectedSize == boardSize)
        }
    }
}

@Composable
private fun ClearRecordsButton(enabled: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick, enabled = enabled, modifier = Modifier.testTag(TestTags.CLEAR_RECORDS_BUTTON),
        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF9BB4A7), disabledContentColor = Color(0xFF617D6D))
    ) {
        Text(stringResource(R.string.clear_records), fontSize = 12.sp)
    }
}

@Composable
private fun RecordList(records: List<GameRecord>, modifier: Modifier) {
    if (records.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_records), color = Color(0xFFF0F7D5), fontSize = 20.sp,
                modifier = Modifier.testTag(TestTags.EMPTY_RECORDS))
        }
    } else {
        LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(records, key = { _, record -> record.id }) { index, record ->
                val date = remember(record.createdAt) {
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(record.createdAt))
                }
                Row(
                    Modifier.fillMaxWidth().background(Color(0xE6173D32), RoundedCornerShape(16.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("${index + 1}", color = if (index == 0) Color(0xFFFFD166) else Color(0xFFBAD3BC),
                        fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.points, record.score), color = Color.White,
                            fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.record_board, record.size), color = Color(0xFFF0F7D5), fontSize = 14.sp)
                        Text(date, color = Color(0xFFBAD3BC), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
