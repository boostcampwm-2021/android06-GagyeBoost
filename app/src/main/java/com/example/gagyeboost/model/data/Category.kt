package com.example.gagyeboost.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "category_name")
    var categoryName: String,

    @ColumnInfo(name = "emoji")
    var emoji: String
) {
    constructor() : this(0, "", "")
}
