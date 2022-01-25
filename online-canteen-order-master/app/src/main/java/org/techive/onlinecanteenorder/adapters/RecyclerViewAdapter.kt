package org.techive.onlinecanteenorder.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mikhaellopez.circularimageview.CircularImageView
import com.orm.SugarRecord
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cart.view.*
import kotlinx.android.synthetic.main.activity_category.view.*
import kotlinx.android.synthetic.main.activity_notification.view.*
import kotlinx.android.synthetic.main.activity_order.view.*
import kotlinx.android.synthetic.main.activity_stock.view.*
import kotlinx.android.synthetic.main.add_category_dialog.view.*
import kotlinx.android.synthetic.main.content_main.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.activities.MenuDetail
import org.techive.onlinecanteenorder.activities.admin.AddMenu
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter(
    private var mValues: MutableList<Any>,
    private val mViewType: Int,
    private val mActivity: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAI14jjSA:APA91bEStFB2EE6qX2POFncCAL1gk_anZsCLYZ4XhGttYRtCsJJLzH5EMl4iVWvZf8TfiT9DG1JdcrInP37pQZgu6KBjqr5cz6_elz404Qic-zXdfdJ2PH-1AXjb9eKFIou2cSR8AYYM"
    private val contentType = "application/json"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (mViewType) {
            VIEW_TYPE_CATEGORIES -> CategoryViewHolder(inflater.inflate(R.layout.activity_category, parent, false))
            VIEW_TYPE_STOCKS -> StockViewHolder(inflater.inflate(R.layout.activity_stock, parent, false))
            VIEW_TYPE_MENUS -> MenuViewHolder(inflater.inflate(R.layout.content_main, parent, false))
            VIEW_TYPE_CART -> CartViewHolder(inflater.inflate(R.layout.activity_cart, parent, false))
            VIEW_TYPE_ORDERS -> OrderViewHolder(inflater.inflate(R.layout.activity_order, parent, false))
            else -> NotificationViewHolder(inflater.inflate(R.layout.activity_notification, parent, false))
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mValues[position]

        when (mViewType) {
            VIEW_TYPE_CATEGORIES -> {
                val m = item as Category
                val h = holder as CategoryViewHolder

                h.mName.text = m.name

                if (position % 2 != 0) {
                    h.itemCard.setCardBackgroundColor(mActivity.resources.getColor(R.color.colorLight))
                } else {
                    h.itemCard.setCardBackgroundColor(mActivity.resources.getColor(android.R.color.white))
                }

                h.mEdit.setOnClickListener {
                    updateCategory(h, m)
                }
            }

            VIEW_TYPE_STOCKS -> {
                val m = item as Menu
                val h = holder as StockViewHolder

                if (position % 2 != 0) {
                    h.itemCard.setCardBackgroundColor(mActivity.resources.getColor(R.color.colorLight))
                } else {
                    h.itemCard.setCardBackgroundColor(mActivity.resources.getColor(android.R.color.white))
                }

                h.mName.text = m.name
                h.mValue.text = m.stock

                h.mEdit.setOnClickListener {
                    updateStock(h, m)
                }

            }

            VIEW_TYPE_MENUS -> {
                val m = item as Menu
                val h = holder as MenuViewHolder

                h.mName.text = m.name
                h.mStock.text = "Stock: ${m.stock}"
                Picasso.get().load(m.thumbnail).placeholder(R.drawable.oco_placeholder).into(h.mImage)

                h.itemView.setOnClickListener {
                    val intent = Intent(mActivity, MenuDetail::class.java)
                    intent.putExtra(OCO.MENU_MODEL, m)
                    mActivity.startActivity(intent)
                }

                if (OCO.u!!.type == "admin")
                    h.itemView.setOnLongClickListener {
                        val intent = Intent(mActivity, AddMenu::class.java)
                        intent.putExtra(OCO.MENU_STATUS, "1")
                        intent.putExtra("menu-item", m)
                        mActivity.startActivity(intent)

                        return@setOnLongClickListener true
                    }
            }

            VIEW_TYPE_CART -> {
                var m = item as Cart
                val h = holder as CartViewHolder

                h.mName.text = m.name
                h.mPrice.text = "Dhs. ${m.price}"
                h.mQuantity.text = m.quantity
                Picasso.get().load(m.image).placeholder(R.drawable.oco_placeholder).into(h.mImage)

                h.mRemove.setOnClickListener {

                    mActivity.alert {
                        title = "Are you sure?"
                        message = "Are you sure to remove from cart?"
                        positiveButton("Delete") {
                            m = SugarRecord.findById(Cart::class.java, m.id)
                            m.delete()
                            mValues.removeAt(position)
                            notifyDataSetChanged()

                            if (listener != null)
                                listener!!.onItemClick(m, h.adapterPosition, h.itemView)

                            mActivity.toast("Deleted!")
                        }
                        negativeButton("Cancel") {
                            mActivity.toast("You cancelled the dialog.")
                        }
                    }.show()
                }
            }

            VIEW_TYPE_ORDERS -> {
                val m = item as Order
                val h = holder as OrderViewHolder
                FirebaseMessaging.getInstance().subscribeToTopic("/topics/onlinecanteenorder")

                h.mId.text = "Order # ${m.order_id}"
                h.mBy.text = "By: ${m.user_name}"
                h.mPrice.text = m.total_price
                h.mQty.text = m.total_quantity
                h.mTime.text = m.time
                Picasso.get().load(m.user_image).placeholder(R.drawable.oco_placeholder).into(h.mImage)

                val nameList = m.name!!.split("-")
                val qtyList = m.quantities!!.split("-")
                val priceList = m.prices!!.split("-")
                val ids = m.ids!!.split(" ")

                val mAdapter = OrderAdapter(nameList, qtyList, priceList, mActivity)
                h.mRecycler.adapter = mAdapter

                if (m.status == "pending") {
                    h.mBtn.visibility = View.VISIBLE
                } else {
                    h.mBtn.visibility = View.GONE
                }

                h.mBtn.setOnClickListener {
                    var s = 0
                    var ss = true
                    h.sl.clear()
                    h.il.clear()
                    OCO.getDatabaseRef()
                        .child(OCO.ORDERS)
                        .child(m.order_id)
                        .child("status")
                        .setValue("approved")
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                OCO.getDatabaseRef()
                                    .child(OCO.MENUS)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {

                                        }

                                        override fun onDataChange(dataSnapshot: DataSnapshot?) {

                                            for (dataSnapshot1 in dataSnapshot!!.children) {
                                                val model = dataSnapshot1.getValue(Menu::class.java)!!
                                                for (i in ids.indices) {
                                                    if (model.id == ids[i]) {
                                                        val stock = model.stock
                                                        Log.e("ids", ids[i])
                                                        Log.e("qty", qtyList[i])
                                                        Log.e("stock", stock)

                                                        s = stock!!.toInt() - qtyList[i].toInt()
                                                        h.il.add(ids[i])
                                                        h.sl.add(s.toString())
                                                    }
                                                }
                                            }
                                            if (ss) {
                                                for (k in h.il.indices) {
                                                    Log.e(h.il[k], h.sl[k])
                                                    OCO.getDatabaseRef()
                                                        .child(OCO.MENUS)
                                                        .child(h.il[k])
                                                        .child("stock")
                                                        .setValue(h.sl[k])
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {

                                                                mActivity.toast("Order Confirmed!")
                                                            } else {
                                                                mActivity.toast("Something went wrong.")
                                                            }
                                                        }
                                                }
                                                ss = false
                                            }
                                        }

                                    })

                                //...
                                //Inflate the dialog with custom view
                                val mDialogView =
                                    LayoutInflater.from(mActivity).inflate(R.layout.add_category_dialog, null)
                                //AlertDialogBuilder
                                val mBuilder = AlertDialog.Builder(mActivity)
                                    .setView(mDialogView)
                                    .setTitle("Time")
                                    .setMessage("Time taken to ready order.")
                                    .setCancelable(false)

                                mDialogView.dialog_title.text = "Minute *"
                                mDialogView.dialog_name.inputType = InputType.TYPE_CLASS_NUMBER
                                mDialogView.dialog_add.text = "Send Notification"
                                mDialogView.dialog_name.hint = "Minute"

                                //show dialog
                                val mAlertDialog = mBuilder.show()

                                mDialogView.dialog_add.setOnClickListener {

                                    val min = mDialogView.dialog_name.text.toString()
                                    val key = OCO.getDatabaseRef().child(OCO.NOTIFICATIONS).push().key
                                    val time =
                                        SimpleDateFormat("MMM dd, yyyy, HH:mm:ss")
                                            .format(Date(System.currentTimeMillis()))

                                    val message =
                                        "Thank You ${m.user_name}!, Your order is confirmed. Please wait for $min minutes only"
                                    val notifications =
                                        Notification(key, m.user_id, m.order_id, message, OCO.u!!.name, time)

                                    OCO.getDatabaseRef().child(OCO.NOTIFICATIONS)
                                        .child(m.user_id)
                                        .child(key)
                                        .setValue(notifications)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                mActivity.toast("Notification sent to customer.")


                                                val topic = "/topics/onlinecanteenorder" //topic has to match what the receiver subscribed to

                                                val notification = JSONObject()
                                                val notifcationBody = JSONObject()

                                                try {
                                                    notifcationBody.put("title", m.order_id)
                                                    notifcationBody.put("message", message)   //Enter your notification message
                                                    notifcationBody.put("receiverid", m.user_id)
                                                    notification.put("to", topic)
                                                    notification.put("data", notifcationBody)
                                                    Log.e("TAG", "try")
                                                } catch (e: JSONException) {
                                                    Log.e("TAG", "onCreate: " + e.message)
                                                }

                                                sendNotification(notification)

                                                mAlertDialog.dismiss()
                                            } else {
                                                mActivity.toast("Something went wrong")
                                                mAlertDialog.dismiss()
                                            }
                                        }
                                }
                                //...end

                            } else {
                                mActivity.toast("Something went wrong.")
                            }
                        }
                }
            }

            else -> {
                val m = item as Notification
                val h = holder as NotificationViewHolder
                h.mBy.text = "By: ${m.by}"
                h.mMsg.text = m.message
                h.mOrder.text = "Order # ${m.order_id}"
                h.mTime.text = m.createdAt

            }
        }

    }

    private fun updateCategory(
        h: CategoryViewHolder,
        m: Category
    ) {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(mActivity).inflate(R.layout.add_category_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(mActivity)
            .setView(mDialogView)
            .setTitle("Edit category")

        //show dialog
        val mAlertDialog = mBuilder.show()
        mDialogView.dialog_name.setText(m.name)
        mDialogView.dialog_add.text = "Update category"
        mDialogView.dialog_add.setOnClickListener {
            h.mProgress.setTitle("Updating")
            h.mProgress.setMessage("Please wait while we check your credentials.")
            h.mProgress.setCanceledOnTouchOutside(false)
            h.mProgress.show()

            val c = mDialogView.dialog_name.text.toString()

            OCO.getDatabaseRef().child(OCO.CATEGORIES)
                .child(m.uid)
                .child("name")
                .setValue(c)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        h.mProgress.dismiss()
                        mActivity.toast("Category updated successfully!")
                        mAlertDialog.dismiss()
                    } else {
                        mActivity.toast("Something went wrong")
                        h.mProgress.dismiss()
                        mAlertDialog.dismiss()
                    }
                }
        }
    }

    private fun updateStock(
        h: StockViewHolder,
        m: Menu
    ) {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(mActivity).inflate(R.layout.add_category_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(mActivity)
            .setView(mDialogView)
            .setTitle("Edit stock")

        //show dialog
        val mAlertDialog = mBuilder.show()
        mDialogView.dialog_name.setText(m.stock)
        mDialogView.dialog_title.text = "Stock *"
        mDialogView.dialog_add.text = "Update stock"
        mDialogView.dialog_name.inputType = InputType.TYPE_CLASS_NUMBER
        mDialogView.dialog_add.setOnClickListener {
            h.mProgress.setTitle("Updating")
            h.mProgress.setMessage("Please wait while we check your credentials.")
            h.mProgress.setCanceledOnTouchOutside(false)
            h.mProgress.show()

            val c = mDialogView.dialog_name.text.toString()

            OCO.getDatabaseRef().child(OCO.MENUS)
                .child(m.id)
                .child("stock")
                .setValue(c)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        h.mProgress.dismiss()
                        mActivity.toast("Category updated successfully!")
                        mAlertDialog.dismiss()
                    } else {
                        mActivity.toast("Something went wrong")
                        h.mProgress.dismiss()
                        mAlertDialog.dismiss()
                    }
                }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class CategoryViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mName = mView.category_name
        val mEdit = mView.edit_category
        val itemCard = mView.category_card
        val mProgress = ProgressDialog(mActivity)
    }

    inner class StockViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mName = mView.stock_name
        val mValue = mView.stock_value
        val mEdit = mView.edit_stock
        val itemCard = mView.stock_card
        val mProgress = ProgressDialog(mActivity)
    }

    inner class MenuViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mName = mView.menuname
        val mImage = mView.menuthumbnail
        val mStock = mView.menustock
    }

    inner class CartViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mName: TextView = mView.item_name
        val mPrice: TextView = mView.item_price
        val mImage: ImageView = mView.item_image
        val mQuantity: TextView = mView.item_quantity
        val mRemove: ImageButton = mView.item_remove
    }

    inner class OrderViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mId: TextView = mView.order_id
        val mBy: TextView = mView.order_by
        val mImage: ImageView = mView.order_image
        val mRecycler: RecyclerView = mView.orderdetail_list
        val mPrice: TextView = mView.total_price
        val mQty: TextView = mView.total_quantities
        val mBtn: Button = mView.approve_order
        val mTime: TextView = mView.time

        var sl: MutableList<String> = ArrayList<String>()
        var il: MutableList<String> = ArrayList<String>()
    }

    inner class NotificationViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mBy: TextView = mView.n_by
        val mOrder: TextView = mView.n_order
        val mTime: TextView = mView.n_time
        val mMsg: TextView = mView.n_text
        val mImage: CircularImageView = mView.n_image
    }

    companion object {
        val VIEW_TYPE_CATEGORIES = 0
        val VIEW_TYPE_STOCKS = 1
        val VIEW_TYPE_MENUS = 2
        val VIEW_TYPE_CART = 3
        val VIEW_TYPE_ORDERS = 4
        val VIEW_TYPE_NOTIFICATIONS = 5
    }


    //load fragment on recyclerView OnClickListner
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {

        fun onItemClick(model: Any, position: Int, itemView: View)
    }

    fun setFilter(newList: ArrayList<Any>) {
        mValues = ArrayList()
        (mValues as ArrayList<Any>).addAll(newList)
        notifyDataSetChanged()
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(mActivity)
    }
    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Toast.makeText(mActivity, "Request error", Toast.LENGTH_LONG).show()
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
