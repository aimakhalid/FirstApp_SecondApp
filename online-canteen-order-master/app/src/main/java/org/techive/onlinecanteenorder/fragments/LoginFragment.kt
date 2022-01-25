package org.techive.onlinecanteenorder.fragments


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.jetbrains.anko.support.v4.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.activities.MainActivity
import org.techive.onlinecanteenorder.helpers.InputValidation
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.helpers.Session
import org.techive.onlinecanteenorder.model.User

class LoginFragment : Fragment() {

    var inputValidation: InputValidation? = null
    private var progressDialog: ProgressDialog? = null

    internal var e: String = ""
    internal var p: String = ""
    var session:Session? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        session = Session(context!!)

        inputValidation = InputValidation(context)
        progressDialog = ProgressDialog(context)

        (activity as AppCompatActivity).supportActionBar!!.setTitle(R.string.login)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        v.login.setOnClickListener {
            e = login_email.text.toString()
            p = login_password.text.toString()

            if (!inputValidation!!.isInputEditTextFilled(login_email, getString(R.string.error_message_email))) {
                return@setOnClickListener
            }
            if (!inputValidation!!.isInputEditTextEmail(login_email, getString(R.string.error_message_email))) {
                return@setOnClickListener
            }
            if (!inputValidation!!.isInputEditTextFilled(
                    login_password,
                    getString(R.string.error_message_password)
                )
            ) run {
                return@setOnClickListener
            }
            else {
                //...
                progressDialog!!.setTitle("Logging In")
                progressDialog!!.setMessage("Please wait while we check your credentials.")
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.show()

                OCO.getAuth().signInWithEmailAndPassword(e, p)
                    .addOnCompleteListener(
                        activity!!
                    ) { task ->
                        if (task.isSuccessful) {
                            OCO.getDatabaseRef()
                                .child(OCO.USERS)
                                .child(OCO.getUserId())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        Log.e("loginIn", "OK")
                                        val u = dataSnapshot.getValue(User::class.java)
                                        if (u != null) {
                                            OCO.u = u
                                            session!!.setUserType(u.type!!)
                                            progressDialog!!.dismiss()
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            startActivity(intent)
                                        } else {
                                            toast(R.string.something_went_wrong)
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        progressDialog!!.dismiss()
                                        Log.e("Error", databaseError.toException().message)
                                    }
                                })

                            Log.d("Login", "signInWithEmail:success")

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.exception)
                            progressDialog!!.dismiss()
                            toast("Error : " + task.exception!!.message)
                        }
                    }
            }
        }

        v.create_account.setOnClickListener {
            loadFragment(R.id.auth_container, RegisterFragment(), true)
        }

        v.forgot_password.setOnClickListener {
            loadFragment(R.id.auth_container, RecoverFragment(), true)
        }

        return v
    }

    fun loadFragment(resID: Int, fragment: Fragment, backStack: Boolean) {

        val ft: FragmentTransaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(resID, fragment)
        if (backStack) {
            ft.addToBackStack(null)
            ft.commit()
        } else {
            ft.commit()
        }

    }

}
