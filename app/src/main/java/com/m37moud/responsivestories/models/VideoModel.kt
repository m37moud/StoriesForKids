package com.m37moud.responsivestories.models

class VideoModel {
    var id : String? = null
    var title : String? = null
    var timestamp : String? = null
    var videoUri : String? = null

    //fire base require empty constructor
    constructor(){}

    constructor(id: String?, title: String?, timestamp: String?, videoUri: String?) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.videoUri = videoUri
    }

}