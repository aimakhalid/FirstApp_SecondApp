package org.techive.onlinecanteenorder.helpers

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.techive.onlinecanteenorder.model.User

class OCO : MultiDexApplication() {
    companion object {

        var u: User? = null

        val USERS = "users"
        val MENUS = "menus"
        val NOTIFICATIONS = "notifications"
        val ORDERS = "orders"
        val MENU_MODEL = "menu-model"
        val CATEGORY = "category"
        val CATEGORIES = "categories"
        val MENU_STATUS = "status"  //0: add, 1: update
        val USER_TYPE = "user"       //'admin' for admin & 'user' for user
        val NOTIFICATION_TITLE = "notification-title"
        val NOTIFICATION_CONTENT = "notification-content"

        val ELEVATION = 0.0F

        fun getAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        public fun getUserId(): String {
            return FirebaseAuth.getInstance().currentUser!!.uid
        }

        fun isSignedIn(): Boolean {
            return FirebaseAuth.getInstance().currentUser != null
        }

        fun getDatabaseRef(): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
        }

        fun getStorageRef(): StorageReference {
            return FirebaseStorage.getInstance().reference
        }

        fun getUploadTask(task: UploadTask): UploadTask {
            return task
        }
    }
}
