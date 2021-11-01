package com.example.gagyeboost.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id:Int=0,

    @ColumnInfo(name = "category_name")
    val categoryName:String,

    @ColumnInfo(name = "emoji")
    val emoji:String
)
