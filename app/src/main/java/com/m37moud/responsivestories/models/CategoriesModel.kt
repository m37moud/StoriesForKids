package com.m37moud.responsivestories.models

class CategoriesModel {
    var categoryId: String? = null
    var categoryName: String? = null
    var categoryImage: String? = null


    constructor() {}

    constructor(
        id: String?,
        categoryName: String?,
        categoryImage: String?
    ) {
        this.categoryId = id
        this.categoryName = categoryName
        this.categoryImage = categoryImage
    }
}