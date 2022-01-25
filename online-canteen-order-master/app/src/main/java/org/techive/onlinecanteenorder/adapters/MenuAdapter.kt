package org.techive.onlinecanteenorder.adapters

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_main.view.*
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.activities.MainActivity
import org.techive.onlinecanteenorder.activities.MenuDetail
import org.techive.onlinecanteenorder.activities.admin.AddMenu
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Menu

class MenuAdapter(
    val mList: MutableList<Menu>?,
    val mActivity: MainActivity
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MenuAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_main, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myViewHolder: MenuAdapter.MyViewHolder, position: Int) {
        val m = this.mList?.get(position) as Menu
        val h = myViewHolder

        h.mName.text = m.name
        h.mStock.text = "S: ${m.stock}"
        Picasso.get().load(m.thumbnail).placeholder(R.drawable.oco_placeholder).into(h.mImage)

        h.itemView.setOnClickListener {
            val intent = Intent(mActivity, MenuDetail::class.java)
            intent.putExtra(OCO.MENU_MODEL,m)
            mActivity.startActivity(intent)
        }

        if (OCO.u!!.type == "admin")
        h.itemView.setOnLongClickListener {
            val intent = Intent(mActivity, AddMenu::class.java)
            intent.putExtra(OCO.MENU_STATUS, "1")
            intent.putExtra("menu-item",m)
            mActivity.startActivity(intent)

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    inner class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mName = mView.menuname
        val mImage = mView.menuthumbnail
        val mStock = mView.menustock
    }
}
