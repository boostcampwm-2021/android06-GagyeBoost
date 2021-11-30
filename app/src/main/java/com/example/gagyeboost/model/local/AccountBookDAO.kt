package com.example.gagyeboost.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gagyeboost.model.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountBookDAO {
    //선택한 일자의 모든 데이터
    @Query("SELECT * FROM account_book WHERE year=:year AND month=:month AND day=:day")
    suspend fun loadDayData(year: Int, month: Int, day: Int): List<AccountBook>

    @Query("SELECT SUM(CASE WHEN money_type=0 THEN money else null END) as expenseMoney, SUM(CASE WHEN money_type=1 THEN money else null END) as incomeMoney FROM account_book WHERE year=:year AND month=:month AND day=:day")
    suspend fun loadDayTotalMoney(year: Int, month: Int, day: Int): DayTotalMoney?

    @Query(
        """SELECT account_book.id, category.emoji, category.category_name, account_book.content, account_book.money, account_book.money_type 
        FROM account_book, category
        WHERE account_book.category = category.id AND
        year=:year AND month=:month AND day=:day"""
    )
    fun flowLoadDayData(year: Int, month: Int, day: Int): Flow<List<DateDetailItem>>

    @Query("SELECT * FROM category WHERE money_type=:moneyType")
    suspend fun loadCategoryAllData(moneyType: Byte): List<Category>

    @Query("SELECT * FROM category WHERE money_type=:moneyType")
    fun flowLoadCategoryAllData(moneyType: Byte): Flow<List<Category>>

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

    @Query("SELECT * FROM account_book, category WHERE account_book.id=:id AND category.id=account_book.category")
    suspend fun loadRecordDetailData(id: Int): RecordDetailData

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
            WHERE year * 10000 + month * 100 + day >= :startYear * 10000 + :startMonth * 100 + :startDay AND
            year * 10000 + month * 100 + day <= :endYear * 10000 + :endMonth * 100 + :endDay AND
            (category IN (:categoryList)) AND
            (money BETWEEN :startMoney AND :endMoney) AND
            content LIKE '%'||:keyword||'%'"""
    )
    suspend fun loadSearchDataWithKeyword(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int,
        categoryList: List<Int>,
        startMoney: Int,
        endMoney: Int,
        keyword: String
    ): List<AccountBook>
}
