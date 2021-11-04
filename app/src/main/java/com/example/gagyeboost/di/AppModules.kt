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
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('회비', '\uD83D\uDC65');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('선물', '\uD83C\uDF81');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('패션/쇼핑', '\uD83D\uDECD');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('생활', '\uD83E\uDDF4');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('교통', '\uD83D\uDE8C');")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji) values ('기타', '➖');")
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
