package com.example.core_util

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore
    private lateinit var documentReference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        preferenceManager = PreferenceManager(this)
        database = FirebaseFirestore.getInstance()
    }

    override fun onPause() {
        super.onPause()
        preferenceManager = PreferenceManager(this)
        Log.d("ss","sdsdsd ${preferenceManager.getString(Constants.KEY_SENDER_ID)}")
        database = FirebaseFirestore.getInstance()
       documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_SENDER_ID).toString())
        documentReference.update(Constants.KEY_AVAILABILITY, 0)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager = PreferenceManager(this)
        database = FirebaseFirestore.getInstance()
        Log.d("ss","sdsdsd ${preferenceManager.getString(Constants.KEY_SENDER_ID)}")
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_SENDER_ID).toString())
        documentReference.update(Constants.KEY_AVAILABILITY, 1)
    }
}