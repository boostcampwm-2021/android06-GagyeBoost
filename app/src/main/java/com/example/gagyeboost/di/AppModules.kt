package com.example.gagyeboost.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.local.AccountBookDatabase
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(get(), AccountBookDatabase::class.java, "accountBookDatabase")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // TODO 초기 데이터 추가
                }
            }).build()
    }

    single {
        get<AccountBookDatabase>().accountBookDAO()
    }

    single {
        Repository(get())
    }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}
