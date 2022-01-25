package org.techive.onlinecanteenorder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_notification_list.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.adapters.RecyclerViewAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Notification

class NotificationActivity : AppCompatActivity() {

    private var mListType = RecyclerViewAdapter.VIEW_TYPE_NOTIFICATIONS
    private var mAdapter: RecyclerViewAdapter? = null
    var mList: MutableList<Any> = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.action_notifications)

        OCO.getDatabaseRef().child(OCO.NOTIFICATIONS)
            .child(OCO.getUserId())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    mList.clear()
                    for (dataSnapshot1 in dataSnapshot!!.children) {
                        val model = dataSnapshot1.getValue(Notification::class.java)!!
                        mList.add(0,model)
                    }
                    mAdapter = RecyclerViewAdapter(mList, mListType, this@NotificationActivity)

                    with(notifications_list!!) {
                        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

                        adapter = mAdapter
                        setEmptyView(empty_notification_view!!)
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
