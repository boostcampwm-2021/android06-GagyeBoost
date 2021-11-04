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
                    val expenseType = 0.toByte()
                    val incomeType = 1.toByte()

                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('뷰티/미용', '\uD83D\uDC84', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('식비', '\uD83C\uDF7D', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('주거/통신', '\uD83C\uDFE0', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('여행', '✈️️', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('회비', '\uD83D\uDC65', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('선물', '\uD83C\uDF81', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('패션/쇼핑', '\uD83D\uDECD', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('생활', '\uD83E\uDDF4', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('교통', '\uD83D\uDE8C', $expenseType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('기타', '➖', $expenseType);")

                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('월급', '🏢', $incomeType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('용돈', '\uD83D\uDC5B', $incomeType);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('기타', '➖', $incomeType);")
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
