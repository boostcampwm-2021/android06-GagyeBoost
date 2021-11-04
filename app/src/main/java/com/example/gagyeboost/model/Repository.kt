package com.example.gagyeboost.model

import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.local.AccountBookDAO

class Repository(private val accountBookDao: AccountBookDAO) {

    suspend fun getMonthIncome(year: Int, month: Int) = accountBookDao.getMonthIncome(year, month)

    suspend fun addAccountBookData(accountBook: AccountBook) {
        accountBookDao.addAccountBookData(accountBook)
    }

    suspend fun addCategoryData(category: Category) {
        accountBookDao.addCategoryData(category)
    }

    suspend fun getMonthExpense(year: Int, month: Int) = accountBookDao.getMonthExpense(year, month)

    suspend fun loadCategoryList(moneyType: Byte) = accountBookDao.getCategoryAllData(moneyType)

    suspend fun updateCategoryData(category: Category) = accountBookDao.updateCategoryData(category)

    suspend fun loadDayData(year: Int, month: Int, day: Int): List<AccountBook> {
        return accountBookDao.getDayData(year, month, day)
    }
}
