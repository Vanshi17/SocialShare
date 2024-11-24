package com.projects.SocialShare.models

class Reel {
    var reelURL:String=""
    var caption:String=""
    var profileLink:String?= null
    var time: Long = 0L
    constructor()
    constructor(reelUrl: String, caption: String) {
        this.reelURL = reelUrl
        this.caption = caption
    }

     constructor(reelURL: String, caption: String, profileLink: String,time:Long) {
        this.reelURL = reelURL
        this.caption = caption
        this.profileLink = profileLink
         this.time = time
    }


}