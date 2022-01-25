package org.techive.onlinecanteenorder.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ShareCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.toast
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.helpers.OCO

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = OCO.ELEVATION
        title = getString(R.string.menu_about)

        facebook.setOnClickListener {
            facebook()
        }

        linkedIn.setOnClickListener {
            linkedIn()
        }

        twitter.setOnClickListener {
            twitter()
        }

        instagram.setOnClickListener {
            instagram()
        }

        rate_us.setOnClickListener {
            rateUs()
        }

        more_apps.setOnClickListener {
            moreApps()
        }

        share.setOnClickListener {
            shareApp()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun facebook() {

        val facebookIntent = OpenFacebook(this)
        startActivity(facebookIntent)

    }

    fun OpenFacebook(context: Context?): Intent {
        try {
            // my facebook ID for deep linking= 100002385943313
            context!!.packageManager.getPackageInfo("com.facebook.katana", 0)
            return Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/375746559733940"))
        } catch (e: Exception) {
            return Intent(Intent.ACTION_VIEW, Uri.parse("https://web.facebook.com/techiveofficial"))
        }

    }

    fun googlePlus() {

        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText("Welcome to the Google+ platform. https://plus.google.com/103642784230760881331")
            .intent
            .setPackage("com.google.android.apps.plus")

        startActivity(shareIntent)

    }

    fun github() {
        val url = "https://github.com/hussnain-cs786"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    fun instagram() {
        val url = "https://www.instagram.com/techiveofficial/"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    fun twitter() {

        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("twitter://user?screen_name=techiveofficial")
            )
            startActivity(intent)

        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/techiveofficial")
                )
            )
        }

    }

    fun linkedIn() {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/techiveofficial/"))
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isEmpty()) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=techiveofficial"))
        }
        startActivity(intent)
    }

    fun rateUs() {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        }
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }

    }

    fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            val shareMessage = "http://play.google.com/store/apps/details?id=$packageName"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share with"))
        } catch (e: Exception) {
            toast(e.message!!)
        }

    }

    fun moreApps() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Tech Hive")
                )
            )
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Tech Hive")
                )
            )
        }

    }
}
