package com.example.gagyeboost.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category

@Dao
interface AccountBookDAO {
    //선택한 일자의 모든 데이터
    @Query("SELECT * FROM account_book WHERE year=:year AND month=:month AND day=:day")
    fun getDayData(year: Int, month: Int, day: Int): List<AccountBook>

    //선택한 달의 수입 총 합
    @Query("SELECT SUM(money) FROM account_book WHERE year=:year AND month=:month AND money_type=1")
    suspend fun getMonthIncome(year: Int, month: Int): Int?

    //선택한 달의 지출 총 합
    @Query("SELECT SUM(money) FROM account_book WHERE year=:year AND month=:month AND money_type=0")
    suspend fun getMonthExpense(year: Int, month: Int): Int?

    @Query("SELECT * FROM category")
    suspend fun getCategoryAllData(): List<Category>

    @Insert
    suspend fun addCategoryData(category: Category)

    @Update
    suspend fun updateCategoryData(category: Category)

    @Query("DELETE FROM category WHERE  category_name=:categoryName")
    fun deleteCategoryData(categoryName: String)

    //카테고리 이름으로 존재하는지 검사
    @Query("SELECT EXISTS (SELECT * FROM category WHERE category_name=:categoryName) as isExist")
    fun isExistCategoryName(categoryName: String): Boolean

    @Query("SELECT * FROM category WHERE id=:id")
    fun getCategoryData(id: Int): Category

    @Insert
    suspend fun addAccountBookData(accountBook: AccountBook)

    @Update
    fun updateAccountBookData(accountBook: AccountBook)

    @Query("DELETE FROM account_book WHERE id=:id")
    fun deleteAccountBookData(id: Int)

    //선택한 월의 모든 데이터
    @Query("SELECT * FROM account_book WHERE year=:year AND month=:month")
    fun getMonthData(year: Int, month: Int): List<AccountBook>

    //검색 및 지도필터 결과(키워드?)
    @Query(
        """SELECT * FROM account_book 
        WHERE (year BETWEEN :startYear AND :endYear) AND 
        (month BETWEEN :startMonth AND :endMonth) AND 
        (day BETWEEN :startDay AND :endDay) AND
         category IN(:categoryList) AND
         (money BETWEEN :startMoney AND :endMoney)"""
    )
    fun getSearchData(
        startYear: Int = 1900,
        startMonth: Int = 1,
        startDay: Int = 1,
        endYear: Int = 2500,
        endMonth: Int = 12,
        endDay: Int = 31,
        categoryList: List<Int>,
        startMoney: Int = 0,
        endMoney: Int = (1 shl 30)
    ): List<AccountBook>

    /*
    //선택한 월과 위치범위에 따른 데이터(클러스터링에서 사용?)
    @Query("SELECT *FROM account_book WHERE year=:year AND month=:month AND getDist(:center,latitude,longitude)<=:radius")
    fun getMonthPositionData(
        year: Int,
        month: Int,
        center: Pair<Float, Float>,
        radius: Float
    ): List<AccountBook>

    private fun getDist(center: Pair<Float, Float>, latitude: Float, longitude: Float) =
        sqrt((center.first - latitude) * (center.first - latitude) + (center.second - longitude) * (center.second - longitude))
     */
}
