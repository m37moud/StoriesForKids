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


    //fire base require empty constructor
    constructor() {}

    constructor(
        id: String?, title: String?, timestamp: String?, videoUri: String?,
        video_slide: String?,
        video_type: String?,
        video_thumb: String?,
        video_description: String?,
        video_category: String?
    ) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.videoUri = videoUri
        this.videoSlide = video_slide
        this.videoType = video_type
        this.videoThumb = video_thumb
        this.videoCategory = video_description
        this.videoDescription = video_category

    }

}