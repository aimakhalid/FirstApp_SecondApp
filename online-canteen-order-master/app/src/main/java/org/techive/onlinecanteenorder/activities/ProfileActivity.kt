package org.techive.onlinecanteenorder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.helpers.OCO

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.menu_profile)

        Picasso.get().load(OCO.u!!.thumbnail).placeholder(R.drawable.ic_logo).into(profile_image)
        profile_name.text = OCO.u!!.name
        profile_email.text = OCO.u!!.email
        profile_phone.text = OCO.u!!.phone
        profile_type.text = OCO.u!!.type
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
