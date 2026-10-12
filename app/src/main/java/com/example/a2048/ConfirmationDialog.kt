package com.example.a2048

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmationDialog(question: String, detail: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag(TestTags.CONFIRMATION_DIALOG),
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF173D32),
        titleContentColor = Color(0xFFF0F7D5),
        textContentColor = Color(0xFFBAD3BC),
        title = { Text(question, fontSize = 20.sp) },
        text = { Text(detail) },
        confirmButton = {
            CompactActionButton(
                text = stringResource(R.string.confirm_yes),
                onClick = onConfirm,
                modifier = Modifier.width(88.dp).testTag(TestTags.CONFIRM_YES),
                emphasized = true
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag(TestTags.CONFIRM_NO)) {
                Text(stringResource(R.string.confirm_no), color = Color(0xFFBAD3BC))
            }
        }
    )
}
