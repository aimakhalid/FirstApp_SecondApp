package org.techive.onlinecanteenorder.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_menu_list.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.adapters.RecyclerViewAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Menu

class MenuActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var mListType = RecyclerViewAdapter.VIEW_TYPE_MENUS
    private var mAdapter: RecyclerViewAdapter? = null
    var mList: MutableList<Any> = ArrayList<Any>()

    private var mProgress: ProgressDialog? = null

    var category = ""
    var status = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_list)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION

        status = intent.getStringExtra("s") //0: category.  1: all menu

        if (status == "0") {
            category = intent.getStringExtra(OCO.CATEGORY)
            title = category
        }else{
            title = "Menu"
        }

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
                        if (status == "0") {
                            if (model.category!!.toLowerCase() == category.toLowerCase())
                                mList.add(model)
                        }else{
                            mList.add(model)
                        }
                    }
                    mProgress!!.dismiss()
                    mAdapter = RecyclerViewAdapter(mList, mListType, this@MenuActivity)

                    with(menulist!!) {
                        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
                        }else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 4)
                        }
                        adapter = mAdapter
                        setEmptyView(emptymenuview!!)
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
        searchView.queryHint = getString(R.string.search_menu)
        searchView.setOnQueryTextListener(this)
        if (status=="1")
        {
            searchView.requestFocus()
            searchView.setQuery("",true)
        }
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when (mListType) {
            RecyclerViewAdapter.VIEW_TYPE_MENUS -> {

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
