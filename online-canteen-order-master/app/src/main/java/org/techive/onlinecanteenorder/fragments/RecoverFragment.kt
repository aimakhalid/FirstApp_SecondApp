package org.techive.onlinecanteenorder.fragments


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.fragment_recover.*
import kotlinx.android.synthetic.main.fragment_recover.view.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.helpers.InputValidation
import org.techive.onlinecanteenorder.helpers.OCO

class RecoverFragment : Fragment() {

    var inputValidation: InputValidation? = null
    private var progressDialog: ProgressDialog? = null

    internal var e: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_recover, container, false)
        inputValidation = InputValidation(context)
        progressDialog = ProgressDialog(context)

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setTitle(R.string.recover_password)

        v.recover_password.setOnClickListener {
            e = v.forgot_email.text.toString()

            if (!inputValidation!!.isInputEditTextFilled(forgot_email, getString(R.string.error_message_email))) {
                return@setOnClickListener
            }
            if (!inputValidation!!.isInputEditTextEmail(forgot_email, getString(R.string.error_message_email))) {
                return@setOnClickListener
            } else {
                progressDialog!!.setTitle(getString(R.string.checking))
                progressDialog!!.setMessage(getString(R.string.signing_up_message))
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.show()
                OCO.getAuth().sendPasswordResetEmail(e)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressDialog!!.dismiss()
                            context!!.toast(R.string.check_your_email)
                            activity!!.supportFragmentManager.popBackStack(
                                null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )

                        } else {
                            progressDialog!!.hide()
                            context!!.toast(R.string.error_message_email)
                        }
                    }

            }
        }

        setHasOptionsMenu(true)
        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity!!.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}
