package com.example.gagyeboost.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category

@Database(
    entities = [
        AccountBook::class,
        Category::class
    ],
    version = 1
)
abstract class AccountBookDatabase : RoomDatabase() {
    abstract fun accountBookDAO(): AccountBookDAO

    companion object {
        private var instance: AccountBookDatabase? = null

        fun getInstance(context: Context): AccountBookDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AccountBookDatabase::class.java, "accountBookDatabase"
        ).build()
    }
}