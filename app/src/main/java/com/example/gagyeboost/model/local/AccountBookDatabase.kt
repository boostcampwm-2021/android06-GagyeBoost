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
        private var INSTANCE: AccountBookDatabase? = null

        fun getInstance(context: Context): AccountBookDatabase? {
            if (INSTANCE == null) {
                synchronized(AccountBookDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AccountBookDatabase::class.java,
                        "accountBookDatabase"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}