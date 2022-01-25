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
import kotlinx.android.synthetic.main.activity_stock_list.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.adapters.RecyclerViewAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Menu

class StockActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private var mListType = RecyclerViewAdapter.VIEW_TYPE_STOCKS
    private var mAdapter: RecyclerViewAdapter? = null
    var mList: MutableList<Any> = ArrayList<Any>()

    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.menu_stocks)
        mProgress = ProgressDialog(this)
        mProgress!!.setTitle("loading...")

        OCO.getDatabaseRef().child(OCO.MENUS)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    mProgress!!.dismiss()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    mList.clear()
                    for (dataSnapshot1 in dataSnapshot!!.children) {
                        val model = dataSnapshot1.getValue(Menu::class.java)!!
                        mList.add(model)
                    }
                    mProgress!!.dismiss()
                    mAdapter = RecyclerViewAdapter(mList, mListType, this@StockActivity)

                    with(stock_list!!) {
                        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

                        adapter = mAdapter
                        setEmptyView(empty_stock_view!!)
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
        searchView.queryHint = getString(R.string.search_stock)
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when (mListType) {
            RecyclerViewAdapter.VIEW_TYPE_STOCKS -> {

                val newList = ArrayList<Any>()
                for (model in mList) {
                    model as Menu
                    val name = model.name!!.toLowerCase()
                    val category = model.category!!.toLowerCase()
                    val price1 = model.smallprice.toString().toLowerCase()
                    val price2 = model.mediumprice.toString().toLowerCase()
                    val price3 = model.fullprice.toString().toLowerCase()
                    val stock = model.stock!!.toLowerCase()
                    if (name.contains(newText!!.toLowerCase())
                        || category.contains(newText.toLowerCase())
                        || price1.contains(newText.toLowerCase())
                        || price2.contains(newText.toLowerCase())
                        || price3.contains(newText.toLowerCase())
                        || stock.contains(newText.toLowerCase())
                    )
                        newList.add(model)
                }
                mAdapter!!.setFilter(newList)
            }
        }
        return true
    }
}
