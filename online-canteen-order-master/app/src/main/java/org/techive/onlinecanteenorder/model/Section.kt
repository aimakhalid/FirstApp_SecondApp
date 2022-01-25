package org.techive.onlinecanteenorder.model

class Section {
    var name: String? = null
    var mList: MutableList<Menu>? = null

    constructor()
    constructor(name: String?, mList: MutableList<Menu>) {
        this.name = name
        this.mList = mList
    }

}