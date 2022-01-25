package org.techive.onlinecanteenorder.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.orm.SugarContext
import com.orm.SugarRecord
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu_detail.*
import org.by9steps.cityplanetapp.helper.MyBounceInterpolator
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Cart
import org.techive.onlinecanteenorder.model.Menu

class MenuDetail : AppCompatActivity() {

    var quantity: Int = 1
    var count: Long? = null
    var status: Int = 0
    var pid: String? = null
    var model: Menu? = null
    var p = 0
    var type = "S"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)
        SugarContext.init(this)

        model = intent.getSerializableExtra(OCO.MENU_MODEL) as Menu

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = "${model!!.name}'s detail"

        name.text = model!!.name
        price1.text = "S: ${model!!.smallprice.toInt()}"
        price2.text = "M: ${model!!.mediumprice.toInt()}"
        price3.text = "L: ${model!!.fullprice.toInt()}"
        description.text = model!!.desc
        Picasso.get().load(model!!.thumbnail).placeholder(R.drawable.ic_logo).into(image)

        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce)

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        val interpolator = MyBounceInterpolator(0.1, 20.0)
        anim.interpolator = interpolator

        val l = SugarRecord.listAll(Cart::class.java)

        for (c in l) {
            if (c.product_id.equals(model!!.id)) {
                status = 1
                quantity = c.quantity!!.toInt()
                pid = c.id.toString()
            }
        }

        quantity_amount.text = quantity.toString()

        decrease_quantity.setOnClickListener {
            if (quantity > 1) {
                quantity -= 1
                quantity_amount.text = quantity.toString()
            }
        }
        increase_quantity.setOnClickListener {
            if (quantity < model!!.stock!!.toInt()) {
                quantity += 1
                quantity_amount.text = quantity.toString()
            } else {
                toast("Out of Stock!")
            }
        }

        p = model!!.smallprice.toInt()

        type1.setOnClickListener {
            type = "S"
            p = model!!.smallprice.toInt()
            type1.textColor = resources.getColor(R.color.colorAccent)
            type2.textColor = resources.getColor(R.color.colorGrayLight)
            type3.textColor = resources.getColor(R.color.colorGrayLight)
        }

        type2.setOnClickListener {
            type = "M"
            p = model!!.mediumprice.toInt()
            type2.textColor = resources.getColor(R.color.colorAccent)
            type1.textColor = resources.getColor(R.color.colorGrayLight)
            type3.textColor = resources.getColor(R.color.colorGrayLight)
        }

        type3.setOnClickListener {
            type = "F"
            p = model!!.fullprice.toInt()
            type3.textColor = resources.getColor(R.color.colorAccent)
            type1.textColor = resources.getColor(R.color.colorGrayLight)
            type2.textColor = resources.getColor(R.color.colorGrayLight)
        }

        home.setOnClickListener {
            finish()
        }

        order_page.setOnClickListener {
            startActivity(Intent(this@MenuDetail, CartActivity::class.java))
        }

        count = SugarRecord.count<Cart>(Cart::class.java)
        counting.text = count.toString()

        add_to_cart.setOnClickListener {

            if (quantity <= model!!.stock!!.toInt()) {

                val tp: Int = p * quantity

                if (status == 0) {
                    val m = Cart(model!!.id, "${model!!.name!!} ($type)", tp.toString(), quantity.toString(), model!!.thumbnail!!)
                    m.save()

                    status = 1

                    val c = SugarRecord.count<Cart>(Cart::class.java)
                    if (c <= 0) {
                        counting.visibility = View.GONE
                    } else {
                        counting.visibility = View.VISIBLE

                        counting.text = c.toString()
                        counting.startAnimation(anim)
                    }

                    toast("Added successfully!")
                } else if (status == 1) {
                    val u: MutableList<Cart>? = SugarRecord.find(Cart::class.java, pid)

                    for (c in u!!) {
                        if (c.id.toString() == pid) {

                            c.product_id = model!!.id
                            c.name = "${model!!.name!!} ($type)"
                            c.price = tp.toString()
                            c.quantity = quantity.toString()
                            c.image = model!!.thumbnail!!

                            c.save()
                            toast("Updated successfully!")

                        }
                    }

                }

            } else {
                toast("Out of stock!")
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val c = SugarRecord.count<Cart>(Cart::class.java)

        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val interpolator = MyBounceInterpolator(0.5, 20.0)
        anim.interpolator = interpolator

        if (c <= 0) {
            status = 0
            counting.visibility = View.GONE
        } else {
            counting.visibility = View.VISIBLE
        }

        if (c < count!!) {
            counting.startAnimation(anim)
        }

        counting.text = c.toString()

        val l = SugarRecord.listAll(Cart::class.java)
        for (ca in l) {
            if (ca.product_id.equals(model!!.id)) {
                status = 1
                quantity = ca.quantity!!.toInt()
                pid = ca.id.toString()
            }

        }

        quantity_amount.text = quantity.toString()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
