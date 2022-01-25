package org.techive.onlinecanteenorder.adapters

import android.annotation.SuppressLint
import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.order_detail_list.view.*
import org.techive.onlinecanteenorder.R

class OrderAdapter(
    val nameList: List<String>,
    val qtyList: List<String>,
    val priceList: List<String>,
    val mActivity: Activity
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): OrderAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_detail_list, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myViewHolder: OrderAdapter.MyViewHolder, position: Int) {
        val n = nameList[position]
        val q = qtyList[position]
        val p = priceList[position]
        val h = myViewHolder

        if (position == 0){
            h.itemBg.setBackgroundColor(mActivity.resources.getColor(R.color.colorGrayLight))
            h.mName.setTextColor(mActivity.resources.getColor(android.R.color.black))
            h.mQty.setTextColor(mActivity.resources.getColor(android.R.color.black))
            h.mPrice.setTextColor(mActivity.resources.getColor(android.R.color.black))
        }else {
            if (position % 2 != 0) {
                h.itemBg.setBackgroundColor(mActivity.resources.getColor(android.R.color.white))
            } else {
                h.itemBg.setBackgroundColor(mActivity.resources.getColor(R.color.colorLight))
            }
        }

        h.mName.text = n
        h.mPrice.text = p
        h.mQty.text = q


    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    inner class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mName = mView.order_names
        val mQty = mView.order_quantities
        val mPrice = mView.order_prices
        val itemBg = mView.orderlinear
    }
}
