package com.example.gagyeboost.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.local.AccountBookDatabase
import com.example.gagyeboost.model.remote.KakaoAPIClient
import com.example.gagyeboost.model.remote.HeaderInterceptor
import com.example.gagyeboost.ui.home.AddViewModel
import com.example.gagyeboost.ui.home.HomeViewModel
import com.example.gagyeboost.ui.map.MapViewModel
import okhttp3.logging.HttpLoggingInterceptor
import com.example.gagyeboost.ui.home.detail.RecordDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(get(), AccountBookDatabase::class.java, "accountBookDatabase")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('뷰티/미용', '\uD83D\uDC84', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('식비', '\uD83C\uDF7D', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('주거/통신', '\uD83C\uDFE0', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('여행', '✈️️', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('회비', '\uD83D\uDC65', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('선물', '\uD83C\uDF81', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('패션/쇼핑', '\uD83D\uDECD', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('생활', '\uD83E\uDDF4', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('교통', '\uD83D\uDE8C', $EXPENSE);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('기타', '➖', $EXPENSE);")

                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('월급', '🏢', $INCOME);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('용돈', '\uD83D\uDC5B', $INCOME);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('이월', '📩', $INCOME);")
                    db.execSQL("INSERT INTO CATEGORY (category_name, emoji, money_type) values ('기타', '➖', $INCOME);")
                }
            }).build()
    }

    single {
        get<AccountBookDatabase>().accountBookDAO()
    }

    single {
        Repository(get(), get())
    }

    single {
        HeaderInterceptor()
    }

    single {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    }

    single {
        KakaoAPIClient(get(), get())
    }
}

val viewModelModule = module {
    viewModel { AddViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { (id: Int) -> RecordDetailViewModel(get(), id) }
    viewModel { MapViewModel(get()) }
}
