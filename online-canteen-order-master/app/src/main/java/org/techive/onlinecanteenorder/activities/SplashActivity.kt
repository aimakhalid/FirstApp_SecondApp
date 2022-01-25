package org.techive.onlinecanteenorder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.techive.onlinecanteenorder.R
import org.techive.onlinecanteenorder.helpers.OCO
import org.techive.onlinecanteenorder.helpers.Session
import org.techive.onlinecanteenorder.model.User

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val session = Session(this)

        Handler().postDelayed({
            if (OCO.isSignedIn()) {
                Log.e("isSignIn", "User is signed in " + OCO.getUserId())
                OCO.getDatabaseRef()
                    .child("users")
                    .child(OCO.getUserId())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            Log.e("OK", "OK")
                            val u = dataSnapshot.getValue<User>(User::class.java)
                            if (u != null) {
                                OCO.u = u
                                session.setUserType(u.type!!)
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                // close this activity
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Something went wrong please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("Error", databaseError.toException().message)
                        }
                    })
            } else {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                // close this activity
                finish()
            }
        }, SPLASH_TIME_OUT.toLong())
    }
}
