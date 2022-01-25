package org.techive.onlinecanteenorder.model

import java.io.Serializable

class User : Serializable {
    //personal detail
    var uid: String? = null
    var key: String? = null     //image key
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var phone: String? = null
    var thumbnail: String? = null
    var type: String? = null

    constructor() {

    }

    constructor(
        uid: String?,
        key: String?,
        name: String?,
        email: String?,
        password: String?,
        phone: String?,
        thumbnail: String?,
        type: String?
    ) {
        this.uid = uid
        this.key = key
        this.name = name
        this.email = email
        this.password = password
        this.phone = phone
        this.thumbnail = thumbnail
        this.type = type
    }


}
