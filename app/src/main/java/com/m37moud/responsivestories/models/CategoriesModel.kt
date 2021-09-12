package com.m37moud.responsivestories.models

import android.os.Parcel
import android.os.Parcelable

class CategoriesModel() : Parcelable {
    var categoryId: String? = null
    var categoryName: String? = null
    var categoryImage: String? = null


    constructor(parcel: Parcel) : this() {
        categoryId = parcel.readString()
        categoryName = parcel.readString()
        categoryImage = parcel.readString()
    }

    constructor(categoryId: String?, categoryName: String?, categoryImage: String?) : this(){
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.categoryImage = categoryImage
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryId)
        parcel.writeString(categoryName)
        parcel.writeString(categoryImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoriesModel> {
        override fun createFromParcel(parcel: Parcel): CategoriesModel {
            return CategoriesModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoriesModel?> {
            return arrayOfNulls(size)
        }
    }
}