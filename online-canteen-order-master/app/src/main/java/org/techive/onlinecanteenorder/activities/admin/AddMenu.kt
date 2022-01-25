package org.techive.onlinecanteenorder.activities.admin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_menu.*
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.helpers.InputValidation
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.model.Category
import org.techive.onlinecanteenorder.model.Menu
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddMenu : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var n = ""      //menu name
    var ps = ""      //menu price for small
    var pm = ""      //menu price for small
    var pl = ""      //menu price for small
    var s = ""      //menu stock
    var c = ""      //menu category
    var d = ""      //menu description

    var model: Menu? = null

    var mList: MutableList<Category> = ArrayList()
    var filter: MutableList<String> = ArrayList()
    var noList: Array<String> = arrayOf("Empty List")

    private val GALLERY = 100
    private val CAMERA = 10

    internal var encodedImage = "thumbnail"
    var filepath: Uri? = null

    //permission
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val rationale = "Please provide location permission so that you can ..."
    val options = Permissions.Options()
        .setRationaleDialogTitle("Info")
        .setSettingsDialogTitle("Warning")

    var inputValidation: InputValidation? = null
    private var mProgress: ProgressDialog? = null

    internal lateinit var arrayAdapter: ArrayAdapter<*>

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(adapterView: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        c = adapterView!!.getItemAtPosition(position).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        inputValidation = InputValidation(this)
        supportActionBar!!.elevation = OCO.ELEVATION
        mProgress = ProgressDialog(this)

        mProgress!!.setTitle("loading...")
        mProgress!!.setCancelable(false)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val status = intent.getStringExtra(OCO.MENU_STATUS)

        if (status == "0") {
            //add
            supportActionBar!!.setTitle(R.string.add_menu)
        } else {
            //update
            model = intent.getSerializableExtra("menu-item") as Menu?
            supportActionBar!!.setTitle(R.string.update_menu)
            menu_name.setText(model!!.name)
            menu_price1.setText(model!!.smallprice.toString())
            menu_price2.setText(model!!.mediumprice.toString())
            menu_price3.setText(model!!.fullprice.toString())
            menu_stock.setText(model!!.stock)
            menu_desc.setText(model!!.desc)
            Picasso.get().load(model!!.thumbnail).placeholder(R.drawable.ic_logo).into(menu_thumbnail)
            add_menu.setText(R.string.update_menu)

        }

        add_menu.setOnClickListener {
            addMenu(status)
        }

        browse_menu_image.setOnClickListener {
            getPermission()
        }

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

                    for (c in mList) {
                        filter.add(c.name!!)
                    }
                    filter.sort()

                    category_spinner.onItemSelectedListener = this@AddMenu
                    if (filter.size > 0) {
                        arrayAdapter = ArrayAdapter(this@AddMenu, R.layout.spinner_list, filter)
                    } else {
                        arrayAdapter = ArrayAdapter(this@AddMenu, R.layout.spinner_list, noList)

                    }
                    arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
                    category_spinner.adapter = arrayAdapter

                    if (status == "1")
                        category_spinner.setSelection(filter.indexOf(model!!.category))

                }

            })


    }

    @SuppressLint("SimpleDateFormat")
    fun addMenu(status: String) {
        n = menu_name.text.toString()
        ps = menu_price1.text.toString()
        pm = menu_price2.text.toString()
        pl = menu_price3.text.toString()
        s = menu_stock.text.toString()
        d = menu_desc.text.toString()

        if (filepath == null) {
            toast("Please select an image")
        } else
            if (!inputValidation!!.isInputEditTextFilled(menu_name, getString(R.string.error_message_name))) {
                return
            } else
                if (!inputValidation!!.isInputEditTextFilled(menu_price1, getString(R.string.error_message_price))) {
                    return
                } else
                    if (!inputValidation!!.isInputEditTextFilled(
                            menu_price2,
                            getString(R.string.error_message_price)
                        )
                    ) {
                        return
                    } else
                        if (!inputValidation!!.isInputEditTextFilled(
                                menu_price3,
                                getString(R.string.error_message_price)
                            )
                        ) {
                            return
                        } else
                            if (!inputValidation!!.isInputEditTextFilled(
                                    menu_stock,
                                    getString(R.string.error_message_stock)
                                )
                            ) {
                                return
                            } else
                                if (!inputValidation!!.isInputEditTextFilled(
                                        menu_desc,
                                        getString(R.string.error_message_desc)
                                    )
                                ) {
                                    return
                                } else {
                                    //...
                                    var key = ""
                                    var time = ""
                                    if (status == "0") {
                                        mProgress!!.setTitle("Adding")
                                        key = OCO.getDatabaseRef().child(OCO.MENUS).push().key
                                        time =
                                            SimpleDateFormat("EEE, d MMM yyyy")
                                                .format(Date(System.currentTimeMillis()))
                                    } else {
                                        mProgress!!.setTitle("Updating")
                                        key = model!!.id!!
                                        time = model!!.createdAt!!
                                    }

                                    mProgress!!.setMessage("Please wait while we check your credentials.")
                                    mProgress!!.setCanceledOnTouchOutside(false)
                                    mProgress!!.show()

                                    val ref = OCO.getStorageRef().child("images").child(key)
                                    val uploadTask = ref.putFile(filepath!!)
                                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                            if (!task.isSuccessful) {
                                                task.exception?.let {
                                                    throw it
                                                }
                                            }
                                            return@Continuation ref.downloadUrl
                                        }).addOnCompleteListener { t ->
                                            if (t.isSuccessful) {
                                                mProgress!!.dismiss()
                                                val downloadUri = t.result
                                                //...
                                                val menu = Menu(
                                                    key,
                                                    n,
                                                    c,
                                                    ps.toFloat(),
                                                    pm.toFloat(),
                                                    pl.toFloat(),
                                                    s,
                                                    d,
                                                    downloadUri.toString(),
                                                    time
                                                )

                                                OCO.getDatabaseRef().child(OCO.MENUS)
                                                    .child(key)
                                                    .setValue(menu)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            mProgress!!.dismiss()
                                                            finish()
                                                            if (status == "0") {
                                                                toast("Menu added successfully!")
                                                            } else {
                                                                toast("Menu updated successfully!")
                                                            }
                                                        } else {
                                                            toast("Something went wrong")
                                                            mProgress!!.dismiss()
                                                        }
                                                    }

                                            } else {
                                                // Handle failures
                                                toast("Something went wrong!")
                                                mProgress!!.dismiss()
                                            }
                                        }

                                }
    }

    fun getPermission() {
        Permissions.check(this/*context*/, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                imageDialog()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                getPermission()
            }
        })
    }

    fun imageDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")

        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {

                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, CAMERA)

            } else if (options[item] == "Choose from Gallery") {

                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY)

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }

        builder.show()
    }

    //Get images from gallery and camera
    fun getImage(data: Intent, code: Int) {
        val contentURI: Uri?
        val bundle: Bundle?

        var bit: Bitmap? = null
        if (code == GALLERY) {
            contentURI = data.data
            try {
                bit = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (code == CAMERA) {
            bundle = data.extras
            if (bundle!!.get("data") != null)
                bit = bundle.get("data") as Bitmap
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        bit!!.compress(Bitmap.CompressFormat.JPEG, 7, byteArrayOutputStream)
        menu_thumbnail!!.setImageBitmap(bit)
        encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        filepath = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bit, null, null))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY) run {
            if (data != null) {
                getImage(data, requestCode)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA) {
            if (data != null) {
                getImage(data, requestCode)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
