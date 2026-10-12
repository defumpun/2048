package com.example.a2048.fixtures

import android.graphics.Bitmap
import android.content.Context
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.a2048.support.checkUi
import com.kaspersky.kaspresso.flakysafety.FlakySafetyProvider
import java.io.File

object TestSnapshots {
    fun save(rule: ComposeContentTestRule, name: String, safety: FlakySafetyProvider) {
        val image = safety.checkUi { rule.onRoot().captureToImage() }
        val context = ApplicationProvider.getApplicationContext<Context>()
        File(context.externalCacheDir, name).outputStream().use {
            image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
