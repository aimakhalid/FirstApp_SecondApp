package org.techive.onlinecanteenorder.model

import java.io.Serializable

class Notification : Serializable {

    var uid: String? = null
    var user_id: String? = null
    var order_id: String? = null
    var message: String? = null
    var by: String? = null
    var createdAt: String? = null

    constructor() {

    }

    constructor(uid: String?, user_id: String?, order_id: String?, message: String?, by: String?, createdAt: String?) {
        this.uid = uid
        this.user_id = user_id
        this.order_id = order_id
        this.message = message
        this.by = by
        this.createdAt = createdAt
    }


}
