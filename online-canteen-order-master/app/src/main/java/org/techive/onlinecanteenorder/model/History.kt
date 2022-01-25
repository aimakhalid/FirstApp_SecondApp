package org.techive.onlinecanteenorder.model

import java.io.Serializable

class History : Serializable {
    var key: String? = null
    var name: String? = null
    var price: String? = null
    var qty: String? = null
    var time: String? = null

    constructor() {

    }

    constructor(key: String, name: String, price: String, qty: String, time: String) {
        this.key = key
        this.name = name
        this.price = price
        this.qty = qty
        this.time = time
    }


}