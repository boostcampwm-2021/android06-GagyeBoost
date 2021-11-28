package com.example.gagyeboost.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.gagyeboost.common.EXPENSE

@Entity(
    tableName = "account_book",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category"],
            onDelete = CASCADE
        )
    ]
)
data class AccountBook(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "money_type")
    var moneyType: Int,

    @ColumnInfo(name = "money")
    var money: Int,

    @ColumnInfo(name = "category")
    var category: Int,

    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "content")
    var content: String,

    @ColumnInfo(name = "year")
    var year: Int,

    @ColumnInfo(name = "month")
    var month: Int,

    @ColumnInfo(name = "day")
    var day: Int
) {
    constructor() : this(0, EXPENSE, 0, 0, 0.0, 0.0, "", "", 0, 0, 0)
}
