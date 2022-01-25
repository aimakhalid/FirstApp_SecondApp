package org.techive.onlinecanteenorder.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import com.orm.SugarContext
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_cart_list.*
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.adapters.RecyclerViewAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Cart
import org.techive.onlinecanteenorder.model.Order
import java.text.SimpleDateFormat
import java.util.*


class CartActivity : AppCompatActivity() {


    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAI14jjSA:APA91bEStFB2EE6qX2POFncCAL1gk_anZsCLYZ4XhGttYRtCsJJLzH5EMl4iVWvZf8TfiT9DG1JdcrInP37pQZgu6KBjqr5cz6_elz404Qic-zXdfdJ2PH-1AXjb9eKFIou2cSR8AYYM"
    private val contentType = "application/json"

    var tp: Int = 0 //total_price
    var ti: Int = 0 //total_items

    var mList: MutableList<Cart>? = null
    private var mListType = RecyclerViewAdapter.VIEW_TYPE_CART
    private var mAdapter: RecyclerViewAdapter? = null

    private var mProgress: ProgressDialog? = null

    internal lateinit var arrayAdapter: ArrayAdapter<*>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        mProgress = ProgressDialog(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.action_Cart)
        SugarContext.init(this)
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/onlinecanteenorder")

        mList = SugarRecord.listAll(Cart::class.java) as MutableList<Cart>

        for (c in mList!!) {

            ti += c.quantity!!.toInt()
            tp += c.price!!.toInt()
        }

        total_price.text = "Total price: Dhs. $tp"
        total_items.text = "Total items: $ti"

        mAdapter = RecyclerViewAdapter(mList as MutableList<Any>, mListType, this@CartActivity)

        with(cart_list!!) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            adapter = mAdapter
            setEmptyView(empty_cart_view!!)
        }

        mAdapter!!.setOnItemClickListener(object : RecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(model: Any, position: Int, itemView: View) {

                model as Cart
                ti -= model.quantity!!.toInt()
                tp -= model.price!!.toInt()

                total_price.text = "Total price: Dhs. $tp"
                total_items.text = "Total items: $ti"

            }
        })

        order_now.setOnClickListener {
            val count = SugarRecord.count<Cart>(Cart::class.java)

            if (count > 0) {

                mProgress!!.setTitle("Sending...")
                mProgress!!.setMessage("Please wait while we check your credentials.")
                mProgress!!.setCanceledOnTouchOutside(false)
                mProgress!!.show()

                val list = SugarRecord.listAll(Cart::class.java)
                val cal = Calendar.getInstance()
                @SuppressLint("SimpleDateFormat")
                val df = SimpleDateFormat("MMM dd, yyyy, HH:mm:ss")
                val time = df.format(cal.time)

                var n = "MENU"  //order names
                var q = "QTY"  //order quantities
                var p = "Rs"  //order prices
                var t = 0   //total price
                var tq = 0  //total quantities
                var id = "ID"

                for (c in list) {

                    n += "-" + c.name
                    q += "-" + c.quantity
                    p += "-" + c.price
                    id += " " + c.product_id
                    t += c.price!!.toInt()
                    tq += c.quantity!!.toInt()
                }
                val order_no = OCO.getDatabaseRef().child(OCO.ORDERS).push().key

                val order = Order(
                    order_no,
                    time,
                    OCO.getUserId(),
                    OCO.u!!.name,
                    OCO.u!!.thumbnail,
                    n,
                    q,
                    id,
                    p,
                    tq.toString(),
                    t.toString(),
                    "pending"
                )

                OCO.getDatabaseRef()
                    .child(OCO.ORDERS)
                    .child(order_no)
                    .setValue(order)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mProgress!!.dismiss()
                            toast("order placed")

                            val topic = "/topics/onlinecanteenorder" //topic has to match what the receiver subscribed to

                            val notification = JSONObject()
                            val notifcationBody = JSONObject()

                            try {
                                notifcationBody.put("title", order_no)
                                notifcationBody.put("message", "New order receive from ${OCO.u!!.name}")   //Enter your notification message
                                notifcationBody.put("receiverid", "OhIh0bxkbgNyK91dOtD2LJibMKG3")
                                notification.put("to", topic)
                                notification.put("data", notifcationBody)
                                Log.e("TAG", "try")
                            } catch (e: JSONException) {
                                Log.e("TAG", "onCreate: " + e.message)
                            }

                            sendNotification(notification)

                            SugarRecord.deleteAll(Cart::class.java)
                            mList!!.clear()
                            total_price.text = "Total price: Dhs. 0"
                            total_items.text = "Total items: 0"
                            mAdapter!!.notifyDataSetChanged()

                        } else {
                            toast("Something went wrong")
                            mProgress!!.dismiss()
                        }
                    }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }
    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Toast.makeText(this@CartActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
}
