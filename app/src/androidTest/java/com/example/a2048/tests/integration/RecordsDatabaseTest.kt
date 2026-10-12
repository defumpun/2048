package com.example.a2048.tests.integration

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.a2048.Direction
import com.example.a2048.data.repo.GameStateRepositoryImpl
import com.example.a2048.data.repo.RecordsRepositoryImpl
import com.example.a2048.domain.engine.GameEngine
import com.example.a2048.domain.usecase.AddRecordUseCase
import com.example.a2048.domain.usecase.GetGameStateUseCase
import com.example.a2048.domain.usecase.ObserveGameStateUseCase
import com.example.a2048.domain.usecase.SaveGameStateUseCase
import com.example.a2048.fixtures.GameFixtures
import com.example.a2048.fixtures.RecordsDatabaseFixture
import com.example.a2048.presentation.game.GameViewModel
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordsDatabaseTest : TestCase() {
    @Test
    fun recordsSurviveReopeningAndAreSortedByScore() {
        val fixture = RecordsDatabaseFixture()
        var databaseOpened = false

        before {
            fixture.open()
            databaseOpened = true
            runBlocking {
                val repository = RecordsRepositoryImpl(fixture.database.recordsDao())
                repository.add(100, 4)
                repository.add(400, 6)
                repository.add(200, 5)
            }
        }.after {
            if (databaseOpened) fixture.close()
        }.run {
            step("Действие: закрыть и заново открыть базу") {
                fixture.database.close()
                fixture.open()
            }
            step("Проверка: рекорды сохранены и отсортированы") {
                runBlocking {
                    val saved = RecordsRepositoryImpl(fixture.database.recordsDao()).observe().first()
                    assertEquals(listOf(400, 200, 100), saved.map { it.score })
                    assertEquals(listOf(6, 5, 4), saved.map { it.size })
                }
            }
        }
    }

    @Test
    fun clearingRecordsKeepsSavedGame() {
        val fixture = RecordsDatabaseFixture()
        var databaseOpened = false
        lateinit var game: GameStateRepositoryImpl
        lateinit var records: RecordsRepositoryImpl
        val state = GameEngine().newGame(4)

        before {
            fixture.open()
            databaseOpened = true
            game = GameStateRepositoryImpl(fixture.database.gameStateDao())
            records = RecordsRepositoryImpl(fixture.database.recordsDao())
            runBlocking {
                game.save(state)
                records.add(100, 4)
            }
        }.after {
            if (databaseOpened) fixture.close()
        }.run {
            step("Действие: очистить рекорды") {
                runBlocking { records.clear() }
            }
            step("Проверка: рекорды удалены, игра сохранена") {
                runBlocking {
                    assertEquals(emptyList<Any>(), fixture.database.recordsDao().observeAll().first())
                    assertEquals(state, game.get())
                }
            }
        }
    }

    @Test
    fun finishingGameSavesExactlyOneRecordEvenWithRepeatedMove() {
        val fixture = RecordsDatabaseFixture()
        var databaseOpened = false
        lateinit var records: RecordsRepositoryImpl
        lateinit var viewModel: GameViewModel
        var viewModelCreated = false

        before {
            fixture.open()
            databaseOpened = true
            val game = GameStateRepositoryImpl(fixture.database.gameStateDao())
            records = RecordsRepositoryImpl(fixture.database.recordsDao())
            runBlocking {
                game.save(GameFixtures.beforeLastMove())
                viewModel = withContext(Dispatchers.Main) {
                    GameViewModel(ObserveGameStateUseCase(game), GetGameStateUseCase(game),
                        SaveGameStateUseCase(game), AddRecordUseCase(records), GameEngine())
                }
                viewModelCreated = true
                withTimeout(5_000) { viewModel.state.first { it.score == 123 } }
            }
        }.after {
            if (viewModelCreated) viewModel.viewModelScope.cancel()
            if (databaseOpened) fixture.close()
        }.run {
            step("Действие: дважды сделать завершающий ход") {
                runBlocking {
                    val moves = withContext(Dispatchers.Main) {
                        val existingJobs = viewModel.viewModelScope.coroutineContext[Job]!!.children.toSet()
                        viewModel.onMove(Direction.LEFT)
                        viewModel.onMove(Direction.LEFT)
                        viewModel.viewModelScope.coroutineContext[Job]!!.children.filter { it !in existingJobs }.toList()
                    }
                    withTimeout(5_000) { moves.joinAll() }
                }
            }
            step("Проверка: сохранён ровно один рекорд") {
                runBlocking {
                    val saved = records.observe().first()
                    assertEquals(1, saved.size)
                    assertEquals(123, saved.single().score)
                    assertEquals(4, saved.single().size)
                }
            }
        }
    }
}
