package com.example.gagyeboost.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import timber.log.Timber
import java.util.*

@Dao
interface AccountBookDAO {
    //선택한 일자의 모든 데이터
    @Query("SELECT * FROM account_book WHERE year=:year AND month=:month AND day=:day")
    suspend fun loadDayData(year: Int, month: Int, day: Int): List<AccountBook>

    //선택한 달의 수입 총 합
    @Query("SELECT SUM(money) FROM account_book WHERE year=:year AND month=:month AND money_type=1")
    suspend fun loadMonthIncome(year: Int, month: Int): Int?

    //선택한 달의 지출 총 합
    @Query("SELECT SUM(money) FROM account_book WHERE year=:year AND month=:month AND money_type=0")
    suspend fun loadMonthExpense(year: Int, month: Int): Int?

    @Query("SELECT * FROM category WHERE money_type=:moneyType")
    suspend fun loadCategoryAllData(moneyType: Byte): List<Category>

    @Query("SELECT id FROM category")
    suspend fun loadAllCategoryID(): List<Int>

    @Insert
    suspend fun addCategoryData(category: Category)

    @Update
    suspend fun updateCategoryData(category: Category)

//    @Query("DELETE FROM category WHERE  category_name=:categoryName")
//    fun deleteCategoryData(categoryName: String)

    //카테고리 이름으로 존재하는지 검사
//    @Query("SELECT EXISTS (SELECT * FROM category WHERE category_name=:categoryName) as isExist")
//    fun isExistCategoryName(categoryName: String): Boolean

    @Query("SELECT EXISTS (SELECT * FROM account_book WHERE category=:categoryId) as isExist")
    suspend fun isExistAccountBookDataByCategoryId(categoryId: Int): Boolean

    @Query("DELETE FROM category WHERE  id=:id")
    suspend fun deleteCategoryData(id: Int)

    @Query("SELECT * FROM category WHERE id=:id")
    suspend fun loadCategoryData(id: Int): Category

    @Query("SELECT * FROM account_book WHERE id=:id")
    suspend fun loadAccountBookData(id: Int): AccountBook

    @Insert
    suspend fun addAccountBookData(accountBook: AccountBook)

    @Update
    suspend fun updateAccountBookData(accountBook: AccountBook)

    @Query("DELETE FROM account_book WHERE id=:id")
    suspend fun deleteAccountBookData(id: Int)

    //선택한 월의 모든 데이터
    @Query("SELECT * FROM account_book WHERE year=:year AND month=:month")
    suspend fun loadMonthData(year: Int, month: Int): List<AccountBook>

    //검색 및 지도필터 결과(키워드?)
    @Query(
        """SELECT * FROM account_book
            WHERE (money_type=:moneyType) AND
            year*10000+month*100+day>=:startYear*10000+:startMonth*100+:startDay AND
            year*10000+month*100+day<=:endYear*10000+:endMonth*100+:endDay AND
            (category IN (:categoryList)) AND
            (money BETWEEN :startMoney AND :endMoney) AND
            (latitude BETWEEN :startLatitude AND :endLatitude) AND
            (longitude BETWEEN :startLongitude AND :endLongitude)"""
    )
    suspend fun loadSearchData(
        moneyType: Byte,
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int,
        categoryList: List<Int>,
        startMoney: Int,
        endMoney: Int,
        startLatitude: Float,
        startLongitude: Float,
        endLatitude: Float,
        endLongitude: Float,
    ): List<AccountBook>

    @Query(
        """SELECT * FROM account_book
            WHERE (money_type=:moneyType) AND
            year*10000+month*100+day>=:startYear*10000+:startMonth*100+:startDay AND
            year*10000+month*100+day<=:endYear*10000+:endMonth*100+:endDay AND
            (category IN (:categoryList)) AND
            (money BETWEEN :startMoney AND :endMoney) AND
            (latitude BETWEEN :startLatitude AND :endLatitude) AND
            (longitude BETWEEN :startLongitude AND :endLongitude) AND
            content LIKE '%'||:keyword||'%'"""
    )
    suspend fun loadSearchDataWithKeyword(
        moneyType: Byte,
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int,
        categoryList: List<Int>,
        startMoney: Int,
        endMoney: Int,
        startLatitude: Float,
        startLongitude: Float,
        endLatitude: Float,
        endLongitude: Float,
        keyword: String
    ): List<AccountBook>
}
