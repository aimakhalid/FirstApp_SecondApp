package org.techive.onlinecanteenorder.model

import com.orm.SugarRecord

class Cart : SugarRecord {
    var product_id: String? = null
    var name: String? = null
    var price: String? = null
    var quantity: String? = null
    var image: String? = null

    constructor() {

    }

    constructor(product_id: String?, name: String, price: String, quantity: String, image: String) : super() {
        this.product_id = product_id
        this.name = name
        this.price = price
        this.quantity = quantity
        this.image = image
    }


}