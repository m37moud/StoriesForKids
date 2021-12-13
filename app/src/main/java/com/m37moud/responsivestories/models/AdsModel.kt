package com.m37moud.responsivestories.models

class AdsModel {
    var activateAds: Boolean = false
    var addRewardAds: String? = null
    var bannerAds: String? = null
    var interstitialAds: String? = null

    constructor() {}

    constructor(
        activateAds: Boolean,
        addRewardAds: String?,
        bannerAds: String?,
        interstitialAds: String
    ) {
        this.activateAds = activateAds
        this.addRewardAds = addRewardAds
        this.bannerAds = bannerAds
        this.interstitialAds = interstitialAds

    }

}
