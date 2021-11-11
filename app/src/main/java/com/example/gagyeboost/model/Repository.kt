package com.example.gagyeboost.model

import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.Filter
import com.example.gagyeboost.model.local.AccountBookDAO
import com.example.gagyeboost.model.remote.GooglePlaceClient

class Repository(
    private val accountBookDao: AccountBookDAO,
    private val client: GooglePlaceClient
) {

    suspend fun addAccountBookData(accountBook: AccountBook) {
        accountBookDao.addAccountBookData(accountBook)
    }

    suspend fun addCategoryData(category: Category) {
        accountBookDao.addCategoryData(category)
    }

    suspend fun loadCategoryList(moneyType: Byte) = accountBookDao.loadCategoryAllData(moneyType)

    suspend fun updateCategoryData(category: Category) = accountBookDao.updateCategoryData(category)

    suspend fun loadDayData(year: Int, month: Int, day: Int) =
        accountBookDao.loadDayData(year, month, day)

    suspend fun loadCategoryData(id: Int) = accountBookDao.loadCategoryData(id)

    suspend fun loadAccountBookData(id: Int) = accountBookDao.loadAccountBookData(id)

    suspend fun deleteAccountBookData(id: Int) = accountBookDao.deleteAccountBookData(id)

    suspend fun updateAccountBookData(accountBook: AccountBook) =
        accountBookDao.updateAccountBookData(accountBook)

    suspend fun loadAllCategoryID() = accountBookDao.loadAllCategoryID()

    suspend fun loadFilterData(filter: Filter): List<AccountBook> =
        accountBookDao.loadSearchData(
            filter.moneyType,
            filter.startYear,
            filter.startMonth,
            filter.startDay,
            filter.endYear,
            filter.endMonth,
            filter.endDay,
            filter.categoryList,
            filter.startMoney,
            filter.endMoney,
            filter.startLatitude,
            filter.startLongitude,
            filter.endLatitude,
            filter.endLongitude
        )

    suspend fun getPlaceListFromKeyword(input: String) =
        client.getGooglePlayService().getPlaceListFromKeyword(input)

    suspend fun loadCategoryMap(): HashMap<Int, Category> {
        val categoryMap = HashMap<Int, Category>()
        val categoryList =
            accountBookDao.loadCategoryAllData(EXPENSE) + accountBookDao.loadCategoryAllData(INCOME)
        categoryList.forEach {
            categoryMap.put(it.id, it)
        }
        return categoryMap
    }
}
