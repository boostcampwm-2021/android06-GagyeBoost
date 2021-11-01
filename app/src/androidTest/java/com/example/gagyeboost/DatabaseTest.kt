package com.example.gagyeboost

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.local.AccountBookDAO
import com.example.gagyeboost.model.local.AccountBookDatabase
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
        Category(id = 1, categoryName = "식비", emoji = "\uD83C\uDF5A"),
        Category(id = 2, categoryName = "여가", emoji = "\uD83C\uDFBE"),
        Category(id = 3, categoryName = "교통", emoji = "\uD83D\uDE8C")
    )

    private val baseAccountBookList = listOf(
        AccountBook(
            id = 1,
            moneyType = 1.toByte(),
            money = 1000,
            category = 1,
            address = "a",
            latitude = 30f,
            longitude = 30f,
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
            latitude = 31f,
            longitude = 30f,
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
            latitude = 31f,
            longitude = 30f,
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
            latitude = 30f,
            longitude = 30f,
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
            latitude = 30f,
            longitude = 30f,
            content = "",
            year = 2021,
            month = 9,
            day = 1
        ),
    )

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        accountBookDatabase = AccountBookDatabase.getInstance(context)
        accountBookDao = accountBookDatabase.accountBookDAO()
        baseCategoryList.forEach { accountBookDao.addCategoryData(it) }
        baseAccountBookList.forEach { accountBookDao.addAccountBookData(it) }
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
        //Given

        //When
        val dataList = accountBookDao.getDayData(2021, 11, 1)

        //Then
        assertThat(dataList.size, `is`(2))
    }

    @Test
    fun getDayDataListSize_OctoberOne_Zero() {
        //Given

        //When
        val dataList = accountBookDao.getDayData(2021, 10, 1)

        //Then
        assertThat(dataList.size, `is`(0))
    }

    @Test
    fun getCategoryAllData_isContainsAll_True() {
        //Given

        //When
        val dataList = accountBookDao.getCategoryAllData()

        //Then
        assertThat(dataList.containsAll(baseCategoryList), `is`(true))
    }

    @Test
    fun addCategory_categorySize_Four() {
        //Given
        accountBookDao.addCategoryData(
            Category(
                id = 4,
                categoryName = "의료",
                emoji = "\uD83C\uDFE5"
            )
        )

        //When
        val size = accountBookDao.getCategoryAllData().size

        //Then
        assertThat(size, `is`(4))
    }

    @Test
    fun getAccountBookAllData_isContainsAll_True() {
        //Given

        //When
        val dataList = accountBookDao.getSearchData(categoryList = listOf(1, 2, 3))

        //Then
        assertThat(dataList.containsAll(baseAccountBookList), `is`(true))
    }

    @Test
    fun getOctoberIncome_income_zero() {
        //Given

        //When
        val income = accountBookDao.getMonthIncome(2021, 10)

        //Then
        assertThat(income, `is`(0))
    }

    @Test
    fun getOctoberExpense_expense_sixThousand() {
        //Given

        //When
        val expense = accountBookDao.getMonthExpense(2021, 10)

        //Then
        assertThat(expense, `is`(6000))
    }

    @Test
    fun isExistCategoryName_isExist_true() {
        //Given

        //When
        val isExist = accountBookDao.isExistCategoryName("교통")

        //Then
        assertThat(isExist, `is`(true))
    }

    @Test
    fun isExistCategoryName_isExist_false() {
        //Given

        //When
        val isExist = accountBookDao.isExistCategoryName("의료")

        //Then
        assertThat(isExist, `is`(false))
    }

    @Test
    fun getCategoryData_isSameCategory_true() {
        //Given

        //When
        val category = accountBookDao.getCategoryData(1)

        //Then
        assertThat(category == baseCategoryList[0], `is`(true))
    }

    @Test
    fun updateAccountData_isUpdated_true() {
        //Given
        val updatedData = baseAccountBookList[1].copy()
        updatedData.money = 100000

        //When
        accountBookDao.updateAccountBookData(updatedData)
        val isUpdated = (accountBookDao.getSearchData(
            startMoney = 100000,
            endMoney = 100000,
            categoryList = listOf(1, 2, 3)
        ))[0] == updatedData

        //Then
        assertThat(isUpdated, `is`(true))
    }
}