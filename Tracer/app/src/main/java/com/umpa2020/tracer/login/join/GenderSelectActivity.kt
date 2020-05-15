package com.umpa2020.tracer.login.join

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding2.widget.color
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_gender_select.*
import kotlinx.android.synthetic.main.signup_toolbar.*
import kotlinx.android.synthetic.main.signup_toolbar.view.*

class GenderSelectActivity : AppCompatActivity(), OnSingleClickListener {
  var isMan = false
  var isWoman = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_gender_select)

    backImageBtn.setOnClickListener(this)
    nextButton.setOnClickListener(this)
    man.setOnClickListener(this)
    woman.setOnClickListener(this)

    val titleText = app_toolbar.titleText
    titleText.text = getString(R.string.txtInputInfoGender)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.backImageBtn -> {
        finish()
      }
      R.id.nextButton -> {
        try {
          val intent = Intent()
          if (isMan)
            intent.putExtra("Gender", getString(R.string.man))
          else
            intent.putExtra("Gender", getString(R.string.woman))
          setResult(RESULT_OK, intent)
          finish()
        } catch (e: Exception) {

        }
      }
      R.id.man -> {
        manTextView.setTextColor(resources.getColor(R.color.red,null))
        womanTextView.setTextColor(resources.getColor(R.color.rankBackgroudGray,null))
        if (manCheck.visibility == View.INVISIBLE && manUnderline.visibility == View.INVISIBLE) {
          manCheck.visibility = View.VISIBLE
          manUnderline.visibility = View.VISIBLE

          womanCheck.visibility = View.INVISIBLE
          womanUnderline.visibility = View.INVISIBLE
          isWoman = false
          isMan = true
        } else {
          manCheck.visibility = View.INVISIBLE
          manUnderline.visibility = View.INVISIBLE

          womanCheck.visibility = View.VISIBLE
          womanUnderline.visibility = View.VISIBLE

          isMan = false
          isWoman = true
        }
        Logg.d("남자 : " + isMan.toString())
        Logg.d("여자 : " + isWoman.toString())
      }
      R.id.woman -> {
        manTextView.setTextColor(resources.getColor(R.color.rankBackgroudGray))
        womanTextView.setTextColor(resources.getColor(R.color.red))
        if (womanCheck.visibility == View.INVISIBLE && womanUnderline.visibility == View.INVISIBLE) {
          womanCheck.visibility = View.VISIBLE
          womanUnderline.visibility = View.VISIBLE

          manCheck.visibility = View.INVISIBLE
          manUnderline.visibility = View.INVISIBLE

          isMan = false
          isWoman = true
        } else {
          womanCheck.visibility = View.INVISIBLE
          womanUnderline.visibility = View.INVISIBLE

          manCheck.visibility = View.VISIBLE
          manUnderline.visibility = View.VISIBLE

          isWoman = false
          isMan = true
        }
        Logg.d("여자 : $isWoman")
        Logg.d("남자 : " + isMan.toString())
      }

    }
    if (isMan || isWoman) {
      nextButton.isEnabled = true
    }
  }
}
