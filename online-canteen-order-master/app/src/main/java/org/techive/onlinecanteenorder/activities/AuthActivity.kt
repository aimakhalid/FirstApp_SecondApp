package org.techive.onlinecanteenorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.fragments.LoginFragment
import org.techive.onlinecanteenorder.helpers.OCO

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar!!.elevation = OCO.ELEVATION

        supportFragmentManager.beginTransaction().replace(R.id.auth_container,LoginFragment()).commit()
    }
}
