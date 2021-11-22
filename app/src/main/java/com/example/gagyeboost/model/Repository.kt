package com.example.gagyeboost.model

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.Filter
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.model.local.AccountBookDAO
import com.example.gagyeboost.model.remote.KakaoAPIClient
import com.example.gagyeboost.ui.home.selectPosition.AddressPagingSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class Repository(
    private val accountBookDao: AccountBookDAO,
    private val client: KakaoAPIClient
) {

    suspend fun addAccountBookData(accountBook: AccountBook) {
        accountBookDao.addAccountBookData(accountBook)
    }

    suspend fun addCategoryData(category: Category) {
        accountBookDao.addCategoryData(category)
    }

    suspend fun loadMonthExpense(year: Int, month: Int) =
        accountBookDao.loadMonthExpense(year, month)

    suspend fun loadCategoryList(moneyType: Byte) = accountBookDao.loadCategoryAllData(moneyType)

    suspend fun updateCategoryData(category: Category) = accountBookDao.updateCategoryData(category)

    suspend fun loadDayData(year: Int, month: Int, day: Int) =
        accountBookDao.loadDayData(year, month, day)

    suspend fun loadCategoryData(id: Int) = accountBookDao.loadCategoryData(id)

    suspend fun loadAccountBookData(id: Int) = accountBookDao.loadAccountBookData(id)

    suspend fun deleteAccountBookData(id: Int) = accountBookDao.deleteAccountBookData(id)

    suspend fun updateAccountBookData(accountBook: AccountBook) =
        accountBookDao.updateAccountBookData(accountBook)

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

    suspend fun loadFilterDataWithKeyword(filter: Filter, keyword: String) =
        accountBookDao.loadSearchDataWithKeyword(
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
            filter.endLongitude,
            keyword
        )

    fun fetchPlaceListFromKeyword(
        input: String,
        latLng: LatLng,
        callback: (Boolean) -> Unit
    ): Flow<PagingData<PlaceDetail>> {
        return Pager(PagingConfig(1)) {
            AddressPagingSource(client.getKakaoApiService(), input, latLng, callback)
        }.flow
    }

    suspend fun loadCategoryMap(): HashMap<Int, Category> {
        val categoryMap = HashMap<Int, Category>()
        val categoryList =
            accountBookDao.loadCategoryAllData(EXPENSE) + accountBookDao.loadCategoryAllData(INCOME)
        categoryList.forEach {
            categoryMap[it.id] = it
        }
        return categoryMap
    }

    suspend fun loadMonthData(year: Int, month: Int) = accountBookDao.loadMonthData(year, month)

    suspend fun deleteCategory(selectedCategoryId: Int) {
        accountBookDao.deleteCategoryData(selectedCategoryId)
    }

    suspend fun isExistAccountBookDataByCategory(selectedCategoryId: Int) =
        accountBookDao.isExistAccountBookDataByCategoryId(selectedCategoryId)
}
