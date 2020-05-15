package com.umpa2020.tracer.main.profile.settting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.umpa2020.tracer.R

class AppSettingActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_app_setting)

    supportFragmentManager
      .beginTransaction()
      .replace(R.id.preferenceSettingFrag, SettingPreferenceFragment())
      .commit()
  }
}
