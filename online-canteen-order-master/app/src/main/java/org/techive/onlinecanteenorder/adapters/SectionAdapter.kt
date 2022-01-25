package org.techive.onlinecanteenorder.adapters

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.section_header.view.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.activities.MainActivity
import org.techive.onlinecanteenorder.activities.MenuActivity
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Section

class SectionAdapter(
    val mList: MutableList<Any>,
    val mActivity: MainActivity
) : RecyclerView.Adapter<SectionAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): SectionAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_header, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: SectionAdapter.MyViewHolder, position: Int) {
        val m = mList[position] as Section
        val h = myViewHolder

        h.mName.text = m.name

        h.mViewAll.setOnClickListener {
            val intent = Intent(mActivity, MenuActivity::class.java)
            intent.putExtra("s", "0")
            intent.putExtra(OCO.CATEGORY, m.name)
            mActivity.startActivity(intent)
        }

        val mAdapter = MenuAdapter(m.mList, mActivity)
        h.mRecycler.adapter = mAdapter
        with(h.mRecycler!!) {
//            layoutManager = android.support.v7.widget.GridLayoutManager(context, 3)

            adapter = mAdapter
            setEmptyView(h.emptyView!!)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mName = mView.header_name
        val mViewAll = mView.view_all
        val mRecycler = mView.menu_list
        val emptyView = mView.empty_section_view
    }
}
