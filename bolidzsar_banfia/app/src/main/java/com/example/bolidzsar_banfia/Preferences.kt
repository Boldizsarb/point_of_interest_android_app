package com.example.bolidzsar_banfia
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class Preferences : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, MyPrefsFragment())
            .commit()
        // fragment is a sub component of an activity
        // so the preferences are stored in a fragment rather than an activity
        // getting hold of the fragment manager which is an object
        // then replacing the existing content of the activity with a new new instance of MyPrefsFragment class
        //
    }

    class MyPrefsFragment :
        PreferenceFragmentCompat() {  // it extends from a library class that represents preferences
        override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
        ) {
            addPreferencesFromResource(R.xml.preferences)  // loading prefernces from the preferences file
        }
    }



}