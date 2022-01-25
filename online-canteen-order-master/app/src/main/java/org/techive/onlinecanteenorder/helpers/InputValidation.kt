package org.techive.onlinecanteenorder.helpers

import android.app.Activity
import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class InputValidation(private val context: Context?) {

    fun isInputEditTextFilled(edittext: EditText, message: String): Boolean {
        val value = edittext.text.toString().trim { it <= ' ' }
        if (value.isEmpty()) {
            edittext.error = message
            hideKeyboardFrom(edittext)
            return false
        } else {
            edittext.error = null
        }

        return true
    }

    fun isTextViewFilled(textView: TextView, message: String): Boolean {
        val value = textView.text.toString().trim { it <= ' ' }
        if (value.isEmpty()) {
            textView.error = message
            return false
        } else {
            textView.error = null
        }

        return true
    }

    fun isInputEditTextEmail(edittext: EditText, message: String): Boolean {
        val value = edittext.text.toString().trim { it <= ' ' }
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            edittext.error = message
            hideKeyboardFrom(edittext)
            return false
        } else {
            edittext.error = null
        }
        return true
    }

    fun isInputEditTextMatches(edittext1: EditText, edittext2: EditText, message: String): Boolean {
        val value1 = edittext1.text.toString().trim { it <= ' ' }
        val value2 = edittext2.text.toString().trim { it <= ' ' }
        if (!value1.contentEquals(value2)) {
            edittext2.error = message
            hideKeyboardFrom(edittext2)
            return false
        } else {
            edittext2.error = null
        }
        return true
    }

    fun hideKeyboardFrom(view: View) {
        if (context != null) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
    }
}

