package org.techive.onlinecanteenorder.fragments


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.mikhaellopez.circularimageview.CircularImageView
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.activities.MainActivity
import org.techive.onlinecanteenorder.helpers.InputValidation
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.helpers.Session
import org.techive.onlinecanteenorder.model.User
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class RegisterFragment : Fragment() {

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

    var user_thumbnail: CircularImageView? = null

    var session: Session? = null

    private val GALLERY = 100
    private val CAMERA = 10

    internal var encodedImage = "thumbnail"
    var filepath: Uri? = null

    internal var n: String = ""
    internal var e: String = ""
    internal var p: String = ""
    internal var cp: String = ""
    internal var ph: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_register, container, false)
        inputValidation = InputValidation(context)
        mProgress = ProgressDialog(context)
        session = Session(context!!)

        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setTitle(R.string.register)

        user_thumbnail = v.findViewById(R.id.user_thumbnail)

        v.register.setOnClickListener {
            registerAcc(v)
        }
        v.browse_image!!.setOnClickListener {
            getPermission()
        }

        setHasOptionsMenu(true)
        return v
    }

    private fun registerAcc(v: View) {
        n = v.reg_name.text.toString()
        e = v.reg_email.text.toString()
        p = v.reg_password.text.toString()
        cp = v.reg_cp.text.toString()
        ph = v.reg_phone.text.toString()

        if (encodedImage == "thumbnail") {
            context!!.toast("Please select an image")
        } else{
            if (!inputValidation!!.isInputEditTextFilled(v.reg_name, getString(R.string.error_message_name))) {
                return
            }
        if (!inputValidation!!.isInputEditTextFilled(v.reg_email, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation!!.isInputEditTextEmail(v.reg_email, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(v.reg_password, getString(R.string.error_message_password))) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(v.reg_cp, getString(R.string.error_message_password))) {
            return
        }
        if (!inputValidation!!.isInputEditTextMatches(
                v.reg_password,
                reg_cp,
                getString(R.string.error_password_match)
            )
        ) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(v.reg_phone, getString(R.string.error_message_phone_number))) {
            return
        } else {

            mProgress!!.setTitle("Signing up")
            mProgress!!.setMessage("Please wait while we check your credentials.")
            mProgress!!.setCanceledOnTouchOutside(false)
            mProgress!!.show()

            val key = OCO.getDatabaseRef().child(OCO.USERS).push().key

            val ref = OCO.getStorageRef().child("images").child(key)
            val uploadTask = ref.putFile(filepath!!)

            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    val downloadUri = t.result
                    OCO.getAuth().createUserWithEmailAndPassword(e, p)
                        .addOnCompleteListener(activity!!) { task ->
                            if (task.isSuccessful) {
                                Log.d("Login", "signInWithEmail:success")
                                val user = User(
                                    OCO.getUserId(),
                                    key,
                                    n,
                                    e,
                                    p,
                                    ph,
                                    downloadUri.toString(),
                                    OCO.USER_TYPE
                                )
                                OCO.getDatabaseRef().child("users")
                                    .child(OCO.getUserId()).setValue(user)
                                OCO.u = user
                                session!!.setUserType(OCO.USER_TYPE)
                                mProgress!!.dismiss()

                                val intent = Intent(context, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)


                            } else {
                                mProgress!!.dismiss()
                                // If sign in fails, display a message to the user.
                                Log.w("Login", "signInWithEmail:failure", task.getException())
                                context!!.toast(task.exception!!.message.toString())
                            }
                        }
                } else {
                    // Handle failures
                    context!!.toast("Something went wrong!")
                    mProgress!!.dismiss()
                }
            }

//            activity!!.supportFragmentManager.popBackStack(
//                null,
//                FragmentManager.POP_BACK_STACK_INCLUSIVE
//            )
        }
        }
    }

    fun getPermission() {
        Permissions.check(context/*context*/, permissions, rationale, options, object : PermissionHandler() {
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

        val builder = AlertDialog.Builder(context!!)
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
                bit = MediaStore.Images.Media.getBitmap(context!!.contentResolver, contentURI)
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
        user_thumbnail!!.setImageBitmap(bit)
        encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        filepath = Uri.parse(MediaStore.Images.Media.insertImage(context!!.contentResolver, bit, null, null))

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity!!.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
