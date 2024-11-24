package com.projects.SocialShare.models

class Post {
    var postURL:String=""
    var caption:String=""
    var uid:String=""
    var time:Long = 0L

    constructor()
    constructor(postURL: String, caption: String, uid: String, time: Long) {
        this.postURL = postURL
        this.caption = caption
        this.uid = uid
        this.time = time
    }

}