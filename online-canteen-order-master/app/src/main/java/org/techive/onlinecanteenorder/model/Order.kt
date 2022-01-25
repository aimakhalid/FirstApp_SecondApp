package org.techive.onlinecanteenorder.model

import com.orm.SugarRecord

class Order : SugarRecord {
    var order_id: String? = null
    var time: String? = null
    var user_id: String? = null
    var user_name: String? = null
    var user_image: String? = null
    var name: String? = null
    var quantities: String? = null
    var ids: String? = null
    var prices: String? = null
    var total_quantity: String? = null
    var total_price: String? = null
    var status: String? = null

    constructor() {

    }

    constructor(
        order_id: String?,
        time: String?,
        user_id: String?,
        user_name: String?,
        user_image: String?,
        name: String?,
        quantities: String?,
        ids: String?,
        prices: String?,
        total_quantity: String?,
        total_price: String?,
        status: String?
    ) : super() {
        this.order_id = order_id
        this.time = time
        this.user_id = user_id
        this.user_name = user_name
        this.user_image = user_image
        this.name = name
        this.quantities = quantities
        this.ids = ids
        this.prices = prices
        this.total_quantity = total_quantity
        this.total_price = total_price
        this.status = status
    }


}