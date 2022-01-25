package org.techive.onlinecanteenorder.model

import java.io.Serializable

class Menu : Serializable{
    var id: String? = null
    var name: String? = null
    var category: String? = null
    var smallprice: Float = 0.0f
    var mediumprice: Float = 0.0f
    var fullprice: Float = 0.0f
    var stock: String? = null
    var desc: String? = null
    var thumbnail: String? = null
    var createdAt: String? = null

    constructor() {

    }

    constructor(
        id: String?,
        name: String?,
        category: String?,
        smallprice: Float,
        mediumprice: Float,
        fullprice: Float,
        stock: String?,
        desc: String?,
        thumbnail: String?,
        createdAt: String?
    ) {
        this.id = id
        this.name = name
        this.category = category
        this.smallprice = smallprice
        this.mediumprice = mediumprice
        this.fullprice = fullprice
        this.stock = stock
        this.desc = desc
        this.thumbnail = thumbnail
        this.createdAt = createdAt
    }


}