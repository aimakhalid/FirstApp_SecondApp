package org.techive.onlinecanteenorder.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_category_dialog.view.*
import kotlinx.android.synthetic.main.content_main_list.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.activities.admin.AddMenu
import org.techive.onlinecanteenorder.activities.admin.CategoryActivity
import org.techive.onlinecanteenorder.activities.admin.OrderActivity
import org.techive.onlinecanteenorder.activities.admin.StockActivity
import org.techive.onlinecanteenorder.adapters.SectionAdapter
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Category
import org.techive.onlinecanteenorder.model.Section
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mProgress: ProgressDialog? = null

    var mCategoryList: MutableList<Category> = ArrayList<Category>()
    var mMenuList: MutableList<org.techive.onlinecanteenorder.model.Menu> = ArrayList()
    var mList: MutableList<Any> = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.elevation = OCO.ELEVATION

        mProgress = ProgressDialog(this)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        if (OCO.u!!.type == "admin") {
            fab.show()
        } else {
            fab.hide()
        }

        fab.setOnClickListener { view ->
            alertDialog()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        headerSetting()

        navView.setNavigationItemSelectedListener(this)

        val nav_Menu = navView.menu

        if (OCO.u!!.type == "admin"){
            nav_Menu.findItem(R.id.nav_notifications).isVisible = true
        }else{
            nav_Menu.findItem(R.id.nav_orders).isVisible = false
            nav_Menu.findItem(R.id.nav_stock).isVisible = false
            nav_Menu.findItem(R.id.nav_category).isVisible = false
        }

        //...first
        OCO.getDatabaseRef().child(OCO.CATEGORIES)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    mCategoryList.clear()
                    for (dataSnapshot1 in dataSnapshot!!.children) {
                        val model = dataSnapshot1.getValue(Category::class.java)!!
                        mCategoryList.add(model)
                    }

                    //...second
                    OCO.getDatabaseRef().child(OCO.MENUS)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                                mProgress!!.dismiss()
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                                mMenuList.clear()
                                for (dataSnapshot1 in dataSnapshot!!.children) {
                                    val model =
                                        dataSnapshot1.getValue(org.techive.onlinecanteenorder.model.Menu::class.java)!!
                                    mMenuList.add(model)
                                }
                                mProgress!!.dismiss()

                                mList.clear()
                                for (c in mCategoryList) {

                                    val list: MutableList<org.techive.onlinecanteenorder.model.Menu> =
                                        ArrayList<org.techive.onlinecanteenorder.model.Menu>()

                                    for (m in mMenuList) {
                                        if (m.category!!.toLowerCase() == c.name!!.toLowerCase()) {
                                            list.add(
                                                org.techive.onlinecanteenorder.model.Menu(
                                                    m.id,
                                                    m.name,
                                                    m.category,
                                                    m.smallprice,
                                                    m.mediumprice,
                                                    m.fullprice,
                                                    m.stock,
                                                    m.desc,
                                                    m.thumbnail,
                                                    m.createdAt
                                                )
                                            )
                                        }
                                    }
                                    mList.add(Section(c.name, list))

                                }

                                val mAdapter = SectionAdapter(mList, this@MainActivity)
                                with(recycler_view!!) {
                                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                                    recycler_view!!.setHasFixedSize(true)
                                    adapter = mAdapter
                                    setEmptyView(empty_section_view!!)
                                }

                                if (OCO.u!!.type == "admin")
                                recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        if (dy>0){
                                            fab.hide()
                                        }else{
                                            fab.show()
                                        }
                                    }
                                })


                            }

                        })
                    //second end

                }

            })
        //first end

    }

    private fun headerSetting() {
        val v = nav_view.getHeaderView(0)

        val name: TextView = v.findViewById(R.id.nav_name)
        val email: TextView = v.findViewById(R.id.nav_email)
        val imageView: CircularImageView = v.findViewById(R.id.nav_image)

        name.text = OCO.u!!.name
        email.text = OCO.u!!.email
        Picasso.get().load(OCO.u!!.thumbnail).placeholder(R.drawable.ic_logo).into(imageView)
    }

    private fun alertDialog() {
        alert {
            title = "Add"
            positiveButton("Category") {
                //Inflate the dialog with custom view
                val mDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.add_category_dialog, null)
                //AlertDialogBuilder
                val mBuilder = AlertDialog.Builder(this@MainActivity)
                    .setView(mDialogView)
                    .setTitle("Add category")

                //show dialog
                val mAlertDialog = mBuilder.show()

                mDialogView.dialog_add.setOnClickListener {
                    mProgress!!.setTitle("Adding")
                    mProgress!!.setMessage("Please wait while we check your credentials.")
                    mProgress!!.setCanceledOnTouchOutside(false)
                    mProgress!!.show()

                    val c = mDialogView.dialog_name.text.toString()
                    val key = OCO.getDatabaseRef().child(OCO.CATEGORIES).push().key
                    val time =
                        SimpleDateFormat("EEE, d MMM yyyy")
                            .format(Date(System.currentTimeMillis()))

                    val category = Category(key, c, time)

                    OCO.getDatabaseRef().child(OCO.CATEGORIES)
                        .child(key)
                        .setValue(category)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mProgress!!.dismiss()
                                toast("Category added successfully!")
                                mAlertDialog.dismiss()
                            } else {
                                toast("Something went wrong")
                                mProgress!!.dismiss()
                                mAlertDialog.dismiss()
                            }
                        }
                }

            }
            negativeButton("Menu") {
                val intent = Intent(this@MainActivity, AddMenu::class.java)
                intent.putExtra(OCO.MENU_STATUS, "0")
                startActivity(intent)
            }
            neutralPressed("Cancel") {}
        }.show()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this@MainActivity, CartActivity::class.java))
                true
            }
            R.id.action_search_ -> {
                val intent = Intent(this,MenuActivity::class.java)
                intent.putExtra("s","1")
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }
            R.id.nav_orders -> {
                startActivity(Intent(this@MainActivity, OrderActivity::class.java))
            }
            R.id.nav_category -> {
                startActivity(Intent(this@MainActivity, CategoryActivity::class.java))
            }
            R.id.nav_notifications -> {
                startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
            }
            R.id.nav_history -> {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }
            R.id.nav_stock -> {
                startActivity(Intent(this@MainActivity, StockActivity::class.java))
            }
            R.id.nav_about -> {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            }
            R.id.nav_logout -> {
                OCO.getAuth().signOut()
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
