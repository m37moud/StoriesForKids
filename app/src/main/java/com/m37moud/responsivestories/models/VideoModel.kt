package com.m37moud.responsivestories.models

class VideoModel {
    var id: String? = null
    var title: String? = null
    var timestamp: String? = null
    var videoUri: String? = null
    var videoSlide: String? = null
    var videoThumb: String? = null
    var videoCategory: String? = null
    var videoDescription: String? = null
    var videoType: String? = null
    var videoUpdate: Boolean = false


    //fire base require empty constructor
    constructor() {}

    constructor(
        id: String?,
        title: String?,
        timestamp: String?,
        videoUri: String?,
        videoSlide: String?,
        videoThumb: String?,
        videoCategory: String?,
        videoDescription: String?,
        videoType: String?,
        videoUpdate: Boolean
    ) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.videoUri = videoUri
        this.videoSlide = videoSlide
        this.videoThumb = videoThumb
        this.videoCategory = videoCategory
        this.videoDescription = videoDescription
        this.videoType = videoType
        this.videoUpdate = videoUpdate
    }


}