package com.example.gagyeboost

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.local.AccountBookDAO
import com.example.gagyeboost.model.local.AccountBookDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var accountBookDao: AccountBookDAO
    private lateinit var accountBookDatabase: AccountBookDatabase

    private val baseCategoryList = listOf(
        Category(id = 1, categoryName = "식비", emoji = "\uD83C\uDF5A", moneyType = 1),
        Category(id = 2, categoryName = "여가", emoji = "\uD83C\uDFBE", moneyType = 1),
        Category(id = 3, categoryName = "교통", emoji = "\uD83D\uDE8C", moneyType = 1)
    )

    private val baseAccountBookList = listOf(
        AccountBook(
            id = 1,
            moneyType = 1.toByte(),
            money = 1000,
            category = 1,
            address = "a",
            latitude = 30.0,
            longitude = 30.0,
            content = "",
            year = 2021,
            month = 11,
            day = 1
        ),
        AccountBook(
            id = 2,
            moneyType = 0.toByte(),
            money = 2000,
            category = 2,
            address = "b",
            latitude = 31.0,
            longitude = 30.0,
            content = "",
            year = 2021,
            month = 10,
            day = 27
        ),
        AccountBook(
            id = 3,
            moneyType = 1.toByte(),
            money = 3000,
            category = 3,
            address = "c",
            latitude = 31.0,
            longitude = 30.0,
            content = "",
            year = 2021,
            month = 11,
            day = 1
        ),
        AccountBook(
            id = 4,
            moneyType = 0.toByte(),
            money = 4000,
            category = 1,
            address = "d",
            latitude = 30.0,
            longitude = 30.0,
            content = "",
            year = 2021,
            month = 10,
            day = 30
        ),
        AccountBook(
            id = 5,
            moneyType = 1.toByte(),
            money = 5000,
            category = 3,
            address = "e",
            latitude = 30.0,
            longitude = 30.0,
            content = "",
            year = 2021,
            month = 9,
            day = 1
        ),
    )

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        accountBookDatabase =
            Room.databaseBuilder(context, AccountBookDatabase::class.java, "accountBookDatabase")
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

        accountBookDao = accountBookDatabase.accountBookDAO()

        CoroutineScope(Dispatchers.IO).launch {
            baseAccountBookList.forEach { accountBook ->
                accountBookDao.addAccountBookData(accountBook)
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        accountBookDatabase.clearAllTables()
        accountBookDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun getDayDataListSize_NovemberOne_Two() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given

            //When
            val dataList = accountBookDao.loadDayData(2021, 11, 1)

            //Then
            assertThat(dataList.size, `is`(2))
        }
    }

    @Test
    fun getDayDataListSize_OctoberOne_Zero() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given

            //When
            val dataList = accountBookDao.loadDayData(2021, 10, 1)

            //Then
            assertThat(dataList.size, `is`(0))
        }
    }

    @Test
    fun getCategoryAllData_isContainsAll_True() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given

            //When
            val dataList = accountBookDao.loadCategoryAllData(EXPENSE)

            //Then
            assertThat(dataList.containsAll(baseCategoryList), `is`(true))
        }
    }

    @Test
    fun addCategory_expense_categorySize_Eleven() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given
            accountBookDao.addCategoryData(
                Category(
                    id = 4,
                    categoryName = "의료",
                    emoji = "\uD83C\uDFE5",
                    moneyType = EXPENSE
                )
            )

            //When
            val size = accountBookDao.loadCategoryAllData(EXPENSE).size

            //Then
            assertThat(size, `is`(10))
        }
    }

    @Test
    fun getOctoberIncome_income_zero() = runBlocking {
        //Given

        //When
        val income = accountBookDao.loadMonthData(2021, 10)

        val sum =income.sumOf { if (it.moneyType == EXPENSE) it.money else 0 }

        //Then
        assertThat(sum, `is`(0))
    }

    @Test
    fun getExpenseCategory_meal() = runBlocking {
        // Given

        // When
        val category = accountBookDao.loadCategoryData(2)

        // Then
        assertThat(category.categoryName, `is`("식비"))
    }


    @Test
    fun getOctoberExpense_expense_sixThousand() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given

            //When
            val expense = accountBookDao.loadMonthData(2021, 10)

            val sum = expense.sumOf { if (it.moneyType == EXPENSE) it.money else 0 }

            //Then
            assertThat(sum, `is`(6000))
        }
    }

    @Test
    fun isExistCategoryId_isExist_true() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given

            //When
            val isExist = accountBookDao.isExistAccountBookDataByCategoryId(1)

            //Then
            assertThat(isExist, `is`(true))
        }
    }

    @Test
    fun isExistCategoryId_isExist_false() {
        CoroutineScope(Dispatchers.IO).launch {
            //Given

            //When
            val isExist = accountBookDao.isExistAccountBookDataByCategoryId(12)

            //Then
            assertThat(isExist, `is`(false))
        }
    }
}
