package com.example.a2048.scenarios

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.a2048.TestTags
import com.example.a2048.data.db.AppDatabase
import com.example.a2048.data.db.GameStateEntity
import com.example.a2048.fixtures.DeviceOrientation
import com.example.a2048.support.AppRule
import com.example.a2048.support.UiActions
import com.example.a2048.support.UiTestConfig
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import java.io.Closeable
import kotlinx.coroutines.runBlocking

class PrepareLargeBoardScenario(
    private val rule: AppRule,
    private val ui: UiActions,
) : Scenario(), Closeable {
    private lateinit var database: AppDatabase

    lateinit var stateBeforeRotation: GameStateEntity
        private set

    fun currentState(): GameStateEntity? = runBlocking { database.gameStateDao().get() }

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Подготовка: открыть базу игры и установить портретную ориентацию") {
            val context = ApplicationProvider.getApplicationContext<Context>()
            database = Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()
            DeviceOrientation.rotate(rule, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, this)
            rule.waitUntil(UiTestConfig.UI_TIMEOUT_MS) { currentState() != null }
        }
        step("Подготовка: выбрать поле 6×6") {
            while (currentState()!!.size != 6) {
                val oldSize = currentState()!!.size
                ui.game.changeSize()
                if (rule.onAllNodesWithTag(TestTags.CONFIRM_YES).fetchSemanticsNodes().isNotEmpty()) {
                    ui.confirmation.confirm()
                }
                rule.waitUntil(UiTestConfig.UI_TIMEOUT_MS) { currentState()!!.size != oldSize }
            }
        }
        step("Подготовка: сохранить состояние перед поворотом") {
            stateBeforeRotation = currentState()!!
        }
    }

    override fun close() {
        if (::database.isInitialized) database.close()
    }
}
