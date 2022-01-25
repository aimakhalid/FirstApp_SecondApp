package org.techive.onlinecanteenorder.activities.admin

import android.app.ProgressDialog
import android.os.Bundle
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_category_list.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.adapters.RecyclerViewAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Category

class CategoryActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var mListType = RecyclerViewAdapter.VIEW_TYPE_CATEGORIES
    private var mAdapter: RecyclerViewAdapter? = null
    var mList: MutableList<Any> = ArrayList<Any>()

    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.menu_category)

        mProgress = ProgressDialog(this)
        mProgress!!.setTitle("loading...")

        OCO.getDatabaseRef().child(OCO.CATEGORIES)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    mProgress!!.dismiss()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    mList.clear()
                    for (dataSnapshot1 in dataSnapshot!!.children) {
                        val model = dataSnapshot1.getValue(Category::class.java)!!
                        mList.add(model)
                    }
                    mProgress!!.dismiss()
                    mAdapter = RecyclerViewAdapter(mList, mListType, this@CategoryActivity)

                    with(category_list!!) {
                        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

                        adapter = mAdapter
                        setEmptyView(empty_category_view!!)
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

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.queryHint = getString(R.string.search_category)
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when (mListType) {
            RecyclerViewAdapter.VIEW_TYPE_CATEGORIES -> {

                val newList = ArrayList<Any>()
                for (model in mList) {
                    model as Category
                    val name = model.name!!.toLowerCase()
                    if (name.contains(newText!!.toLowerCase()))
                        newList.add(model)
                }
                mAdapter!!.setFilter(newList)
            }
        }
        return true
    }
}
