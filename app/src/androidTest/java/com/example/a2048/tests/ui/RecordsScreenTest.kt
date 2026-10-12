package com.example.a2048.tests.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.a2048.*
import com.example.a2048.data.db.AppDatabase
import com.example.a2048.data.repo.RecordsRepositoryImpl
import com.example.a2048.domain.usecase.ClearRecordsUseCase
import com.example.a2048.domain.usecase.ObserveRecordsUseCase
import com.example.a2048.fixtures.*
import com.example.a2048.presentation.records.RecordsViewModel
import com.example.a2048.support.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordsScreenTest : BaseComponentTest() {
    private lateinit var database: AppDatabase
    private lateinit var repository: RecordsRepositoryImpl
    private lateinit var viewModel: RecordsViewModel

    @Test
    fun emptyRecordsShowMessageAndDisabledClearButton() {
        before {
            composeRule.setContent { RecordsScreen(onBack = {}) }
        }.run {
            step("Проверка: пустой список и недоступная очистка") {
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.empty_records)).assertIsDisplayed() }
                checkUi { composeRule.onNodeWithTag(TestTags.CLEAR_RECORDS_BUTTON).assertIsNotEnabled() }
            }
        }
    }

    @Test
    fun recordsFromDatabaseAppearInScoreOrder() {
        before {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            repository = RecordsRepositoryImpl(database.recordsDao())
            runBlocking {
                repository.add(100, 4)
                repository.add(300, 4)
                repository.add(200, 6)
                viewModel = withContext(Dispatchers.Main) {
                    RecordsViewModel(ObserveRecordsUseCase(repository), ClearRecordsUseCase(repository))
                }
            }
            composeRule.setContent { RecordsRoute(onBack = {}, viewModel = viewModel) }
        }.after {
            if (::viewModel.isInitialized) composeRule.runOnIdle { viewModel.viewModelScope.cancel() }
            if (::database.isInitialized) database.close()
        }.run {
            step("Проверка: рекорды поля 4 × 4 отсортированы по очкам") {
                val firstScore = composeRule.activity.getString(R.string.points, 300)
                val secondScore = composeRule.activity.getString(R.string.points, 100)
                checkUi { composeRule.onNodeWithText(secondScore).assertIsDisplayed() }
                val first = checkUi { composeRule.onNodeWithText(firstScore).assertIsDisplayed().fetchSemanticsNode() }
                val second = checkUi { composeRule.onNodeWithText(secondScore).assertIsDisplayed().fetchSemanticsNode() }
                assertTrue("Higher score must appear first", first.boundsInRoot.top < second.boundsInRoot.top)
                checkUi { composeRule.onAllNodesWithText(composeRule.activity.getString(R.string.record_board, 4))[0].assertIsDisplayed() }
                ui.saveSnapshot("records-preview.png")
            }
        }
    }

    @Test
    fun selectingBoardSizeFiltersRecordsAndReturningRestoresList() {
        before {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            repository = RecordsRepositoryImpl(database.recordsDao())
            runBlocking {
                repository.add(100, 4)
                repository.add(300, 4)
                repository.add(200, 6)
                viewModel = withContext(Dispatchers.Main) {
                    RecordsViewModel(ObserveRecordsUseCase(repository), ClearRecordsUseCase(repository))
                }
            }
            composeRule.setContent { RecordsRoute(onBack = {}, viewModel = viewModel) }
        }.after {
            if (::viewModel.isInitialized) composeRule.runOnIdle { viewModel.viewModelScope.cancel() }
            if (::database.isInitialized) database.close()
        }.run {
            step("Действие: выбрать поле 6 × 6") {
                ui.records.selectSize(6)
            }
            step("Проверка: показаны только рекорды поля 6 × 6") {
                ui.records.assertScore(200)
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.points, 300)).assertDoesNotExist() }
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.points, 100)).assertDoesNotExist() }
            }
            step("Действие: вернуться к полю 4 × 4") {
                ui.records.selectSize(4)
            }
            step("Проверка: рекорды поля 4 × 4 снова доступны") {
                ui.records.assertScore(300)
                ui.records.assertScore(100)
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.points, 200)).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun cancellingClearKeepsRecords() {
        before {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            repository = RecordsRepositoryImpl(database.recordsDao())
            runBlocking {
                repository.add(100, 4)
                repository.add(300, 4)
                repository.add(200, 6)
                viewModel = withContext(Dispatchers.Main) {
                    RecordsViewModel(ObserveRecordsUseCase(repository), ClearRecordsUseCase(repository))
                }
            }
            composeRule.setContent { RecordsRoute(onBack = {}, viewModel = viewModel) }
        }.after {
            if (::viewModel.isInitialized) composeRule.runOnIdle { viewModel.viewModelScope.cancel() }
            if (::database.isInitialized) database.close()
        }.run {
            step("Действие: запросить очистку рекордов") {
                ui.records.requestClear()
            }
            step("Проверка: показано подтверждение очистки") {
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed() }
            }
            step("Действие: отменить очистку") {
                ui.confirmation.cancel()
            }
            step("Проверка: рекорды сохранены") {
                ui.records.assertScore(300)
                ui.records.assertScore(100)
                runBlocking { assertEquals(3, repository.observe().first().size) }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun confirmingClearRemovesRecordsWithoutLeavingScreen() {
        before {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            repository = RecordsRepositoryImpl(database.recordsDao())
            runBlocking {
                repository.add(100, 4)
                repository.add(300, 4)
                repository.add(200, 6)
                viewModel = withContext(Dispatchers.Main) {
                    RecordsViewModel(ObserveRecordsUseCase(repository), ClearRecordsUseCase(repository))
                }
            }
            composeRule.setContent { RecordsRoute(onBack = {}, viewModel = viewModel) }
        }.after {
            if (::viewModel.isInitialized) composeRule.runOnIdle { viewModel.viewModelScope.cancel() }
            if (::database.isInitialized) database.close()
        }.run {
            step("Действие: запросить очистку рекордов") {
                ui.records.requestClear()
            }
            step("Проверка: показано подтверждение очистки") {
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed() }
            }
            step("Действие: подтвердить очистку") {
                ui.confirmation.confirm()
            }
            step("Проверка: список пуст, экран рекордов открыт") {
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.empty_records)).assertIsDisplayed() }
                checkUi { composeRule.onNodeWithTag(TestTags.CLEAR_RECORDS_BUTTON).assertIsNotEnabled() }
                checkUi { composeRule.onNodeWithTag(TestTags.RECORD_SCREEN).assertIsDisplayed() }
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.points, 300)).assertDoesNotExist() }
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.points, 100)).assertDoesNotExist() }
                runBlocking { assertEquals(0, repository.observe().first().size) }
                checkUi { composeRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun recordsTitleIsCentered() {
        before {
            composeRule.setContent { RecordsScreen(onBack = {}) }
        }.run {
            step("Проверка: заголовок по центру, возврат обозначен доступной стрелкой") {
                val root = checkUi { composeRule.onRoot().fetchSemanticsNode().boundsInRoot }
                val title = checkUi { composeRule.onNodeWithTag(TestTags.RECORD_TITLE).fetchSemanticsNode().boundsInRoot }
                assertTrue("Title must be centered", kotlin.math.abs(title.center.x - root.center.x) < composeRule.density.density)
                checkUi { composeRule.onNodeWithText(composeRule.activity.getString(R.string.records_back)).assertDoesNotExist() }
            }
        }
    }

    @Test
    fun recordsBackArrowInvokesCallbackOnce() {
        var backs = 0
        before {
            composeRule.setContent { RecordsScreen(onBack = { backs++ }) }
        }.run {
            step("Действие: нажать доступную стрелку назад") {
                composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.records_back)).performClick()
            }
            step("Проверка: обработчик возврата вызван ровно один раз") {
                checkUi { composeRule.runOnIdle { assertEquals(1, backs) } }
            }
        }
    }

}
