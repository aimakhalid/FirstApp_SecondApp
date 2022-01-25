package org.techive.onlinecanteenorder.model

import java.io.Serializable

class Category : Serializable {

    var uid: String? = null
    var name: String? = null
    var createdAt: String? = null

    constructor() {

    }

    constructor(uid: String?, name: String?, createdAt: String?) {
        this.uid = uid
        this.name = name
        this.createdAt = createdAt
    }
}
