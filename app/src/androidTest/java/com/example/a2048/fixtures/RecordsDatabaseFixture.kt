package com.example.a2048.fixtures

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.a2048.data.db.AppDatabase
import java.io.Closeable

class RecordsDatabaseFixture : Closeable {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val name = "records-test-${System.nanoTime()}.db"
    lateinit var database: AppDatabase

    fun openDatabase() = Room.databaseBuilder(context, AppDatabase::class.java, name).build()

    fun open() { database = openDatabase() }

    override fun close() {
        database.close()
        context.deleteDatabase(name)
    }
}
