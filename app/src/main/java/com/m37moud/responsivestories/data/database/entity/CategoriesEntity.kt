package com.m37moud.responsivestories.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.m37moud.responsivestories.util.Constants


@Entity(tableName = Constants.CATEGORY_TABLE)

class CategoriesEntity (
    @PrimaryKey
    var categoryId: String,

    @ColumnInfo(name = "categoryName")
    var categoryName: String? = null,


    @ColumnInfo(name = "categoryImage")
    var categoryImage: String? = null

)