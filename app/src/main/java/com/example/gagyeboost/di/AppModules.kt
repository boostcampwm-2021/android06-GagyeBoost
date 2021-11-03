package com.example.gagyeboost.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.local.AccountBookDatabase
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.home.DateDetailAdapter
import com.example.gagyeboost.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(get(), AccountBookDatabase::class.java, "accountBookDatabase")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('뷰티/미용', '\uD83D\uDC84');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('식비', '\uD83C\uDF7D');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('주거/통신', '\uD83C\uDFE0');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('여행', '✈️️');")
                }
            }).build()
    }

    single {
        get<AccountBookDatabase>().accountBookDAO()
    }

    single {
        Repository(get())
    }

    factory {
        DateDetailAdapter()
    }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}
