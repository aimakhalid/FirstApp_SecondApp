package org.techive.onlinecanteenorder.activities

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_history_list.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.adapters.RecyclerViewAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Order

class HistoryActivity : AppCompatActivity() {

    private var mListType = RecyclerViewAdapter.VIEW_TYPE_ORDERS
    private var mAdapter: RecyclerViewAdapter? = null
    var mList: MutableList<Any> = ArrayList<Any>()

    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.menu_history)

        mProgress = ProgressDialog(this)
        mProgress!!.setTitle("loading...")

        OCO.getDatabaseRef().child(OCO.ORDERS)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    mProgress!!.dismiss()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    mList.clear()
                    for (dataSnapshot1 in dataSnapshot!!.children) {
                        val model = dataSnapshot1.getValue(Order::class.java)!!
                        if (model.status!!.toLowerCase() != "pending"
                            && model.user_id!!.toLowerCase() == OCO.getUserId().toLowerCase())
                            mList.add(0,model)
                    }
                    mProgress!!.dismiss()
                    mAdapter = RecyclerViewAdapter(mList, mListType, this@HistoryActivity)

                    with(history_list!!) {
                        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

                        adapter = mAdapter
                        setEmptyView(empty_history_view!!)
                    }

                }

            })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
